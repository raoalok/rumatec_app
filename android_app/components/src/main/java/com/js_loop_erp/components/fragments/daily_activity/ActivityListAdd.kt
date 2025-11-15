package com.js_loop_erp.components.fragments.daily_activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.ActivityListAdapter
import  com.js_loop_erp.components.data_flow.ActivityListDataFlow
import  com.js_loop_erp.components.databinding.ActivityListBinding
import com.js_loop_erp.components.fragments.LeaveCreateFragment
import com.js_loop_erp.components.fragments.LeavePagination.Companion.LEAVE_SUBMIT_FRAGMENT
import com.js_loop_erp.components.fragments.SignInOutFragment
import com.js_loop_erp.components.fragments.daily_activity.ActivityPagination.Companion.ACTIVITY_SUBMIT_FRAGMENT
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.security.SecureRandom
import java.time.Instant
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.random.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

class ActivityListAdd : DialogFragment(), ActivityListI{
    private var _binding: ActivityListBinding? = null
    private val binding get() = _binding!!

    private lateinit var activitySubmit: Button
    private lateinit var activityRecyclerView: RecyclerView
    private lateinit var activityListAdapter: ActivityListAdapter
    private lateinit var createNewActivity: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ActivityListBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Activity List")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        activitySubmit = view.findViewById(R.id.activity_submit_button)
        activityRecyclerView = view.findViewById(R.id.activity_list_recycler_view)
        createNewActivity = view.findViewById(R.id.create_new_activity)

        activityRecyclerView.layoutManager = LinearLayoutManager(context)

        val activityListData =  ArrayList<ActivityListModel>()
        activityListAdapter = ActivityListAdapter(this, activityListData)
        activityRecyclerView.adapter = activityListAdapter

        lifecycleScope.launch {
            ActivityListDataFlow.updateData.collect { updates ->
                val data = ActivityListDataFlow._activityList.value
                if(data.size > 0){
                    activity?.runOnUiThread(Runnable {
                        activityListAdapter.filterList(data.toList())
                    })
                } else {
                    activityListAdapter.filterList(emptyList())
                }
            }
        }

        activitySubmit.setOnClickListener {
            if(ActivityListDataFlow._activityList.value.size > 0){
                Log.d(TAG, "onViewCreated: ${ActivityListDataFlow._activityList.value.toList()} ")
                val activityList = Gson().toJson(ActivityListDataFlow._activityList.value.toList())
//                Log.d(LeaveCreateFragment.TAG,activityList)
                activityPushToCloud(ActivityListDataFlow._activityList.value.toList())
            }
        }

        createNewActivity.setOnClickListener {
            val childDialog = ActivityCreate()
            childDialog.show(childFragmentManager, ActivityCreate.TAG)
        }

    }

    override fun onItemClick(position: Int) {
       // TODO("Not yet implemented")
        ActivityListDataFlow.removeActivityItemById(position)
        ActivityListDataFlow.updateAllData(Random.nextInt(1,100))
    }

    override fun onItemLongClick(item: ActivityListModel) {
       // TODO("Not yet implemented")

    }

    private fun activityPushToCloud(activityListModel: List<ActivityListModel>){

        if(ReceiverMediator.USER_TOKEN.length > 8) {
            var authToken = ReceiverMediator.USER_TOKEN
            var unixTimeNow: String = System.currentTimeMillis().toString()

            var client = OkHttpClient()
            if (BuildConfig.DEBUG_ONLY_BUILD) {

                Log.d(SignInOutFragment.TAG, "Debug Build Bypass the SSL check")

                val trustAllCerts = arrayOf<TrustManager>(
                    @SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkClientTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkServerTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> =
                            arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                client = OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .build()
            } else {
                client = OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            }

            val mediaType = "application/json".toMediaType()

            val serverList: List<ActivityListModelServer> = activityListModel.map { it.toServerModel() }
            val tripListWrapper = TripWrapper(serverList)
            val gson = Gson()
            val jsonActivityDataList = gson.toJson(tripListWrapper)
            Log.d(TAG, jsonActivityDataList)

            var responBody__: Response
            val request = Request.Builder()
                //.url("http://65.0.61.137/api/gps-locations")
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/activities")
                //.post(body)
                .post(jsonActivityDataList.toRequestBody(mediaType))
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${authToken}")
                //.addHeader("Authorization", "${authToken}")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    android.util.Log.d(TAG, "${TAG}: fail " + unixTimeNow + e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { it ->
                        responBody__ = it
                        android.util.Log.d(TAG, "${TAG}:  ${it.code}  ...... ${it!!.toString()}")

                        if (!responBody__.isSuccessful) {
                            android.util.Log.d(TAG, "${TAG} Unexpected code ${responBody__.code} ....  ${responBody__.body!!.toString()}")
                            showToastMessage()
                            throw IOException(" ${TAG}: Unexpected code $responBody__")
                        } else {
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(),"Activity Submit Success", Toast.LENGTH_LONG).show()
                            }

                            ActivityListDataFlow.updateActivityList(emptyArray<ActivityListModel>())
                            val bundle = Bundle().apply {
                                putInt(ACTIVITY_SUBMIT_FRAGMENT, 1)
                            }
                            setFragmentResult(ACTIVITY_SUBMIT_FRAGMENT, bundle)
                        }
                    }
                }
            })
        }
    }

    fun ActivityListModel.toServerModel(): ActivityListModelServer {
        return ActivityListModelServer(
            mode = this.mode.toString().trim(),
            startDate = unixToIso8601WithMillis(toUnixTime(this.date.toString(), this.startTime.toString()).toString()),
            endDate = unixToIso8601WithMillis(toUnixTime(this.date.toString(), this.endTime.toString()).toString()),
            remark = this.remark.toString(),
            type = this.activity.toString(),
            kilometers =  this.activityKms!!.toInt().toString(),
            category = this.category.toString()
        )
    }

    fun unixToIso8601WithMillis(unixTimeStr: String): String {
        val timestamp = unixTimeStr.toLong()
        val instant = when (unixTimeStr.length) {
            13 -> Instant.ofEpochMilli(timestamp) // milliseconds
            10 -> Instant.ofEpochSecond(timestamp) // seconds
            else -> throw IllegalArgumentException("Invalid timestamp length")
        }
        return DateTimeFormatter.ISO_INSTANT.format(instant).replace("Z", ".000Z")
    }

    fun toUnixTime(date: String, time: String): Long {
        val dateTimeString = "$date $time"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
        return localDateTime.toEpochSecond(ZoneOffset.UTC)
    }

    private fun showToastMessage(){
        lifecycleScope.launch(Dispatchers.Main) {
            activity?.runOnUiThread(Runnable{
                Toast.makeText(requireContext(),"Error submitting leave", Toast.LENGTH_LONG).show()
            })
        }
    }

    companion object {
        val TAG: String = ActivityListAdd::class.java.name
    }
}

interface ActivityListI {
    fun onItemClick(position: Int)
    fun onItemLongClick(item: ActivityListModel)
}