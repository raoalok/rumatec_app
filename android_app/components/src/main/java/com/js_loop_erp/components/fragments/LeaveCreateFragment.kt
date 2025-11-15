package com.js_loop_erp.components.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.LeaveCreateLayoutBinding
import com.js_loop_erp.components.fragments.LeavePagination.Companion.LEAVE_SUBMIT_FRAGMENT
import com.js_loop_erp.components.fragments.SignInOutFragment.Companion.SIGN_IN_OUT
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
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class LeaveCreateFragment : DialogFragment() {
    private var _binding: LeaveCreateLayoutBinding? = null

    private val binding get() = _binding!!

    private lateinit var leavePlaceTypeSpinner: AppCompatSpinner
    private lateinit var leavePlaceFromSpinner: AppCompatSpinner
    private lateinit var leavePlaceToSpinner: AppCompatSpinner
    private lateinit var leaveTravelModeSpinner: AppCompatSpinner
    private lateinit var leaveTravelActivitySpinner: AppCompatSpinner

    private lateinit var leavePlaceTypeArray: ArrayAdapter<String>
    private lateinit var leavePlaceFromArray: ArrayAdapter<String>
    private lateinit var leavePlaceToArray: ArrayAdapter<String>
    private lateinit var leaveTravelModeArray: ArrayAdapter<String>
    private lateinit var leaveTravelActivityArray: ArrayAdapter<String>

    private val cal = Calendar.getInstance()

    private lateinit var leaveUpdateDateFrom: TextView
    private lateinit var leaveUpdateDateTo: TextView


    private  var activityDate: String? = null
    private  var startTime: String? = null
    private  var endTime: String? = null

    private lateinit var activityRemark: TextInputEditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LeaveCreateLayoutBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Add leave")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInsatanceState: Bundle?) {
        super.onViewCreated(view, savedInsatanceState)

        leavePlaceTypeSpinner = view.findViewById<AppCompatSpinner>(R.id.leave_place_type)
        leavePlaceFromSpinner = view.findViewById<AppCompatSpinner>(R.id.leave_place_from)
        leavePlaceToSpinner = view.findViewById<AppCompatSpinner>(R.id.leave_place_to)
        leaveTravelModeSpinner = view.findViewById<AppCompatSpinner>(R.id.leave_travel_mode)
        leaveTravelActivitySpinner = view.findViewById<AppCompatSpinner>(R.id.leave_travel_activity)

        leavePlaceTypeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        leavePlaceFromArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        leavePlaceToArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        leaveTravelModeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        leaveTravelActivityArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)

        leaveUpdateDateFrom = view.findViewById<TextView>(R.id.leave_update_date_from)
        leaveUpdateDateTo = view.findViewById<TextView>(R.id.leave_update_date_to)


        submitButton = view.findViewById(R.id.submit_leave_plan)
        activityRemark = view.findViewById(R.id.leave_apply_remark)

        addDataToLeaveCategory()

        leaveUpdateDateFrom.setOnClickListener {
            showDatePickerFrom()
        }

        leaveUpdateDateTo.setOnClickListener {
            showDatePickerTo()
        }



        submitButton.setOnClickListener{

            if((startTime != null && endTime != null) && (endTime!! >= startTime!!) && (activityRemark.text!!.length >= 4) ){
                Log.d(TAG, "onViewCreated: ${startTime}  ${endTime}  ${leaveTravelActivitySpinner.selectedItem} ${activityRemark.text!!.trim()}")

                val leaveApplyData = LeaveApplyData (type = leaveTravelActivitySpinner.selectedItem.toString().trim(),
                    startDate = unixToIso8601WithMillis(startTime!!.toString()), //startTime!!.toLong(),
                    endDate = unixToIso8601WithMillis(endTime!!.toString()),
                    remark = activityRemark.text!!.trim().toString())

                val json = Gson().toJson(leaveApplyData)
                Log.d(TAG,json)
                sendLeaveToCloud(leaveApplyData)

                // dismiss()
            }
        }
    }

    private fun addDataToLeaveCategory() {
        val travelActivityArray = ArrayList<String>()
        travelActivityArray.add(" Lop")
        travelActivityArray.add(" EL")
        travelActivityArray.add(" SL")

        leaveTravelActivityArray =
            ArrayAdapter(requireContext(), R.layout.item_select_spinner, travelActivityArray)
        leaveTravelActivitySpinner.adapter = leaveTravelActivityArray
    }

    private fun showDatePickerFrom() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(requireContext(),
            R.style.MyTimePickerDialogTheme,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                //textview_date!!.text = sdf.format(cal.getTime())
                leaveUpdateDateFrom.text = sdf.format(cal.getTime())
                startTime = cal.timeInMillis.toString()

            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = cal.timeInMillis
        dpd.datePicker.maxDate = cal.timeInMillis + 604800000
        dpd.show()
    }

    private fun showDatePickerTo() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(requireContext(),
            R.style.MyTimePickerDialogTheme,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                //textview_date!!.text = sdf.format(cal.getTime())
                leaveUpdateDateTo.text = sdf.format(cal.getTime())
                endTime = cal.timeInMillis.toString()
            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = cal.timeInMillis
        dpd.datePicker.maxDate = cal.timeInMillis + 604800000
        dpd.show()
    }

    private fun sendLeaveToCloud(leaveModel: LeaveApplyData){

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


            val gson = Gson()
            val jsonLeaveApplyData = gson.toJson(leaveModel)
            Log.d(LeaveCreateFragment.TAG, jsonLeaveApplyData)

            var responBody__: Response
            val request = Request.Builder()
                //.url("http://65.0.61.137/api/gps-locations")
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/leaves")
                //.post(body)
                .post(jsonLeaveApplyData.toRequestBody(mediaType))
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
                        Log.d(TAG, "${TAG}:  ${it.code}  ...... ${it!!.toString()}")

                        if (!responBody__.isSuccessful) {
                            Log.d(TAG, "${TAG} Unexpected code ${responBody__.code} ....  ${responBody__.body!!}")
                            showToastMessage()
                            throw IOException(" ${TAG}: Unexpected code $responBody__")
                        } else {
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(),"Leave Submit Success", Toast.LENGTH_LONG).show()
                            }
                            val bundle = Bundle().apply {
                                putInt(LEAVE_SUBMIT_FRAGMENT, 1)
                            }
                            setFragmentResult(LEAVE_SUBMIT_FRAGMENT, bundle)
                        }

                    }
                }
            })
        }
    }

    fun unixToIso8601WithMillis(unixTimeStr: String): String {
        val timestamp = unixTimeStr.toLong()
        val instant = when (unixTimeStr.length) {
            13 -> Instant.ofEpochMilli(timestamp) // milliseconds
            10 -> Instant.ofEpochSecond(timestamp) // seconds
            else -> throw IllegalArgumentException("Invalid timestamp length")
        }
        return DateTimeFormatter.ISO_INSTANT.format(instant).replace("Z", "Z")
    }

    private fun showToastMessage(){
        lifecycleScope.launch(Dispatchers.Main) {
           activity?.runOnUiThread(Runnable{
                Toast.makeText(requireContext(),"Error submitting leave", Toast.LENGTH_LONG).show()
            })
        }
    }

    companion object {
        val TAG = LeaveCreateFragment::class.java.name
    }
}


data class LeaveApplyData (
    val startDate: String? = null,
    val endDate: String? = null,
    val remark: String = " ",
    val type: String = "EL"
)
