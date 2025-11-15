package com.js_loop_erp.components.fragments.access_controlled

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.marginBottom
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.TripApproveAdapter
import  com.js_loop_erp.components.adapter.TripApproveUserListAdapter
import  com.js_loop_erp.components.data_class.TripTourPlanRouteList
import  com.js_loop_erp.components.data_flow.TripManagerApproveUserTrip
import  com.js_loop_erp.components.databinding.TripApproveBinding
import com.js_loop_erp.components.fragments.LoginFragment
import  com.js_loop_erp.components.fragments.TripReportFragment
import  com.js_loop_erp.components.fragments.tripTourPlan.TripTourPlanInstituteSelectionFragment
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
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
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class TripApproveFragment : DialogFragment(),TripApproveI {

    private var _binding: TripApproveBinding? = null

    private val binding get() = _binding!!

    private lateinit var activityPlaceFromSpinner: AppCompatSpinner
    private lateinit var addUsersButton: AppCompatImageView

    private lateinit var activityPlaceTypeArray: ArrayAdapter<String>


    private lateinit var rejectTrip: TextView
    private lateinit var approveTrip: TextView
    private lateinit var approveRejectRemark: TextInputEditText

    private lateinit var recyclerView: RecyclerView

    private lateinit var recyclerViewUsers: RecyclerView
    private lateinit var recyclerViewUsersAdapter: TripApproveUserListAdapter
    private lateinit var recyclerViewUsersListData: ArrayList<TourApprovalSelectUserData>

    private lateinit var slectedTripsListToApprove: ArrayList<TripApproveModel>


    var routes: Array<TripTourPlanRouteList> = emptyArray<TripTourPlanRouteList>()


    var spinnerInitialized = false

    var responBody:String = ""


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TripApproveBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Trips List")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInsatanceState: Bundle?){
        super.onViewCreated(view, savedInsatanceState)


        activityPlaceTypeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        
        rejectTrip = view.findViewById<TextView>(R.id.reject_trip)
        approveTrip = view.findViewById<TextView>(R.id.approve_trip)
        approveRejectRemark = view.findViewById(R.id.approve_reject_remark)
        addUsersButton = view.findViewById(R.id.trip_approve_user_list_add_button)

        recyclerView = view.findViewById(R.id.trip_apporoval_list)
        recyclerViewUsers = view.findViewById(R.id.trip_approve_user_list_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerViewUsers.layoutManager = LinearLayoutManager(context)


        val data  = ArrayList<TripApproveModel>()
        val adapter = TripApproveAdapter(this, data)
        recyclerView.adapter = adapter

        recyclerViewUsersListData = ArrayList<TourApprovalSelectUserData>()
        recyclerViewUsersAdapter = TripApproveUserListAdapter(recyclerViewUsersListData)
        recyclerViewUsers.adapter = recyclerViewUsersAdapter

        TripManagerApproveUserTrip.updateTripApproveUserDetails(emptyArray())

        slectedTripsListToApprove = ArrayList<TripApproveModel>()

        getDataFromServer()
        
        //addDataToPlaceType()

        lifecycleScope.launch {
            TripManagerApproveUserTrip.refreshUserApproveSelectedList.collect { updates ->

                if (TripManagerApproveUserTrip.tripApproveUserDetails.value.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {
                        /*adapterHospitalList.filterList(updates.toList())
                        adapterHospitalListData = updates.toList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewHospital)*/
                        recyclerViewUsersAdapter.filterList(TripManagerApproveUserTrip.tripApproveUserDetails.value.toList())
                        setRecyclerViewHeightBasedOnChildren(recyclerViewUsers)
                    })
                } else {
                    activity?.runOnUiThread(Runnable {
                        /*dapterHospitalList.filterList(emptyList())
                        adapterHospitalListData = emptyList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewHospital)*/
                        recyclerViewUsersAdapter.filterList(emptyList())
                        setRecyclerViewHeightBasedOnChildren(recyclerViewUsers)
                    })
                }
            }
        }

        addUsersButton.setOnClickListener {
            val fragment = TourApprovalSelectUser.newInstance("")
            fragment.show(childFragmentManager, "TourApprovalSelectUser")
        }

        approveTrip.setOnClickListener {

            if (approveRejectRemark.text?.toString()?.trim().toString().length > 0) {
                if (!slectedTripsListToApprove.isEmpty()) {
                    val result = getApprovedTripData(slectedTripsListToApprove, approveRejectRemark.text?.toString()?.trim().toString())
                    if (result != null) {
                        val updatedResult = result/*.toMutableMap().apply {
                            this["remark"] = "approved by admin"
                        }*/

                        sendTripApprovalToServer(updatedResult,"approve")
                        Log.d(TAG, "onItemClick: $updatedResult ............")
                    } else {
                        Log.d(TAG, "onItemClick: ${result}.......empty......")
                        Toast.makeText(requireContext(), "Select Trip to Approve", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Select Trip to Approve", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Add Reject Remark ... ", Toast.LENGTH_LONG).show()
            }
        }

        rejectTrip.setOnClickListener {
            if(approveRejectRemark.text?.toString()?.trim().toString().length > 0) {
                if (!slectedTripsListToApprove.isEmpty()) {
                    val result = getApprovedTripData(slectedTripsListToApprove, approveRejectRemark.text?.toString()?.trim().toString())
                    if (result != null) {
                        val updatedResult = result/*.toMutableMap().apply {
                        this["remark"] = "approved by admin"
                    }*/
                        sendTripApprovalToServer(updatedResult,"reject")
                        Log.d(TAG, "onItemClick: $updatedResult ............")
                    } else {
                        Log.d(TAG, "onItemClick: ${result}.......empty......")
                        Toast.makeText(requireContext(), "Select Trip to Reject", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Select Trip to Reject", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Add Reject Remark ... ", Toast.LENGTH_LONG).show()
            }
        }

        getTripPlanRouteListFromServer()

    }

    fun getDataFromServer(){
        //var responBody: String = " "
        if(ReceiverMediator.USER_TOKEN.length > 8) {
            var client = OkHttpClient()

            if(BuildConfig.DEBUG_ONLY_BUILD){

                Log.d(LoginFragment.TAG, "Debug Build Bypass the SSL check")

                val trustAllCerts = arrayOf<TrustManager>(
                    @SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkClientTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {}
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkServerTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {}
                        override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> = arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                client = OkHttpClient.Builder()
                    .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .build()
            } else {
                client = OkHttpClient.Builder()
                    .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            }


            val request = Request.Builder()
//                .url("http://65.0.61.137/api/inventory/sample")
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/authorizelist")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException){
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response){
                    response.use{
                        if(!response.isSuccessful){

                        } else {
                            responBody= response.body?.string() ?: ""
                            //println(responBody)
                            addListToView(responBody)
                        }
                    }
                }
            })
        }

    }

    fun addListToView(itemDataList: String){
        try{
            activity?.runOnUiThread(Runnable{
                val data = ArrayList<TripApproveModel>()
                val gson = Gson()
                val products = gson.fromJson(itemDataList, Array<TripApproveModel>::class.java).toList()
                for(product in products) {
                    data.add(product)
                }

                val adapter = TripApproveAdapter(this, data)

                recyclerView.adapter = adapter

            })
        } catch (e: Exception){
            Log.d(TripReportFragment.TAG, "filter: $e")
        }
    }

    override fun onItemClick(trips: ArrayList<TripApproveModel>) {

        if(!trips.isNullOrEmpty()){
            slectedTripsListToApprove = trips
        }

        /*val result = getApprovedTripData(trips)
        if(!result.isNullOrEmpty()){
            val updatedResult = result.toMutableMap().apply {
                this["remark"] = "approved by admin"
            }
            Log.d(TAG, "onItemClick: $updatedResult ............")
        } else {
            Log.d(TAG, "onItemClick: ${result}.......empty......")
        }*/
    }

    fun getApprovedTripData(trips: List<TripApproveModel>, remark: String = "Updated by Admin"): ApprovalRequest? {
        val selectedIds = trips.filter { it.isChecked == true }.mapNotNull { it.id }

        return if (selectedIds.isNotEmpty()) {
            ApprovalRequest(
                remark = remark,
                tourIds = selectedIds
            )
        } else {
            null
        }
    }

    private fun addDataToPlaceType(){
        val placeTypeArray = ArrayList<String>()
        placeTypeArray.add("")
    }

    fun getTripPlanRouteListFromServer(){
        if (ReceiverMediator.USER_TOKEN.length > 8) {

            var client = OkHttpClient()

            if(BuildConfig.DEBUG_ONLY_BUILD){

                Log.d(LoginFragment.TAG, "Debug Build Bypass the SSL check")

                val trustAllCerts = arrayOf<TrustManager>(
                    @SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkClientTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {}
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkServerTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {}
                        override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> = arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                client = OkHttpClient.Builder()
                    .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .build()
            } else {
                client = OkHttpClient.Builder()
                    .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            }

            val request = Request.Builder()
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/routes/ids")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {

                        } else {
                            responBody = response.body?.string() ?: ""
                            //println(responBody)
                            addRouteListToView(responBody)
                        }
                    }
                }
            })
        }
    }

    fun addRouteListToView(responBody: String){
        try{
            activity?.runOnUiThread(Runnable {
                val placeTypeArray = ArrayList<String>()
                //val data = ArrayList<TripTourPlanRouteList>()
                val gson = Gson()
                routes = gson.fromJson(responBody, Array<TripTourPlanRouteList>::class.java).toList().toTypedArray()
                for(route in routes){
                    placeTypeArray.add(route.route.toString())
                }

                if(routes.size > 0) {
                    getTripPlanRouteStartEndListFromServer(routes[0].id!!.toInt())
                }
            })
        } catch (e: Exception){
            Log.d(TripTourPlanInstituteSelectionFragment.TAG, "addListToView: ${e.toString()}")
        }
    }

    fun getTripPlanRouteStartEndListFromServer(id: Int){
        if (ReceiverMediator.USER_TOKEN.length > 8) {

            var client = OkHttpClient()

            if(BuildConfig.DEBUG_ONLY_BUILD){

                Log.d(LoginFragment.TAG, "Debug Build Bypass the SSL check")

                val trustAllCerts = arrayOf<TrustManager>(
                    @SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkClientTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {}
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkServerTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {}
                        override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> = arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                client = OkHttpClient.Builder()
                    .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .build()
            } else {
                client = OkHttpClient.Builder()
                    .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            }

            val request = Request.Builder()
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/areas/ids?routeId=${id}")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {

                        } else {
                            responBody = response.body?.string() ?: ""
                            //println(responBody)
                            addRouteStartEndListToView(responBody)
                        }
                    }
                }
            })
        }
    }

    fun addRouteStartEndListToView(responBody: String){
        try{
            activity?.runOnUiThread(Runnable {
                val placeToArray = ArrayList<String>()
                //val data = ArrayList<TripTourPlanRouteList>()
                val gson = Gson()

            })
        } catch (e: Exception){
            Log.d(TripTourPlanInstituteSelectionFragment.TAG, "addListToView: ${e.toString()}")
        }
    }

    fun setRecyclerViewHeightBasedOnChildren(recyclerView: RecyclerView) {
        val adapter = recyclerView.adapter ?: return
        val layoutManager = recyclerView.layoutManager ?: return

        var totalHeight = 0
        for (i in 0 until adapter.itemCount) {
            val holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
            adapter.onBindViewHolder(holder, i)
            holder.itemView.measure(
                View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += holder.itemView.measuredHeight + 20
        }

        val params = recyclerView.layoutParams
        params.height = totalHeight + (recyclerView.itemDecorationCount * (recyclerView.getChildAt(0)?.marginBottom ?: 0))
        recyclerView.layoutParams = params
        recyclerView.requestLayout()
    }


    fun sendTripApprovalToServer(approvedTrips: ApprovalRequest, status: String){

        if (ReceiverMediator.USER_TOKEN.length > 8) {

            var client = OkHttpClient()

            if(BuildConfig.DEBUG_ONLY_BUILD){

                Log.d(LoginFragment.TAG, "Debug Build Bypass the SSL check")

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
                        override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> = arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                client = OkHttpClient.Builder()
                    .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .build()
            } else {
                client = OkHttpClient.Builder()
                    .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            }

            val mediaType = "application/json".toMediaType()

            val body = Gson().toJson(approvedTrips).toRequestBody(mediaType)

            val request = Request.Builder()
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/${status}")
                .put(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            Log.d(TAG, "onResponseonResponse: failure")
                            activity?.runOnUiThread {
                                Toast.makeText(context, "Error submitting ${status} trip", Toast.LENGTH_LONG).show()
                                dialog?.dismiss()
                            }
                        } else {
                            Log.d(TAG, "onResponseonResponse: successss")
                            activity?.runOnUiThread {
                                Toast.makeText(context, "Success submitting ${status} trip", Toast.LENGTH_LONG).show()
                                dialog?.dismiss()
                            }
                            //println(responBody)
                           // addRouteStartEndListToView(responBody)
                        }
                    }
                }
            })
        }


        /*val mediaType = "application/json".toMediaType()
val body = "{\n    \"remark\": \"approve\",\n    \"tourIds\": [1]\n}".toRequestBody(mediaType)
val request = Request.Builder()
  .url("https://rverp.in/api/application/tours/approve")
  .put(body)
  .addHeader("Content-Type", "application/json")
  .addHeader("Authorization", "••••••")
  .build()
val response = client.newCall(request).execute()*/

    }


    companion object {
        val TAG = TripApproveFragment::class.java.name
    }

}

interface TripApproveI {
    fun onItemClick(item: ArrayList<TripApproveModel>)
}

data class ApprovalRequest(
    val remark: String,
    val tourIds: List<Int>
)