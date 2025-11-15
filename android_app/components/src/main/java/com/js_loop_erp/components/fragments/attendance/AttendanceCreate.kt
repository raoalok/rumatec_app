package com.js_loop_erp.components.fragments.attendance

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripTourPlanRouteEndList
import  com.js_loop_erp.components.data_class.TripTourPlanRouteList
import  com.js_loop_erp.components.data_class.TripTourPlanRouteStartList
import  com.js_loop_erp.components.data_flow.AttendanceListDataFlow
import  com.js_loop_erp.components.databinding.AttendanceCreateBinding
import com.js_loop_erp.components.fragments.LoginFragment
import  com.js_loop_erp.components.fragments.tripTourPlan.TripPlanFormMeetingDetailsFragment
import  com.js_loop_erp.components.fragments.tripTourPlan.TripTourPlanInstituteSelectionFragment
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.random.Random

class AttendanceCreate : DialogFragment() {

    private var _binding: AttendanceCreateBinding? = null

    private val binding get() = _binding!!

    private lateinit var attendancePlaceTypeSpinner: AppCompatSpinner
    private lateinit var attendancePlaceFromSpinner: AppCompatSpinner
    private lateinit var attendancePlaceToSpinner: AppCompatSpinner
    private lateinit var attendanceTravelModeSpinner: AppCompatSpinner
    private lateinit var attendanceTravelActivitySpinner: AppCompatSpinner

    private lateinit var attendancePlaceTypeArray: ArrayAdapter<String>
    private lateinit var attendancePlaceFromArray: ArrayAdapter<String>
    private lateinit var attendancePlaceToArray: ArrayAdapter<String>
    private lateinit var attendanceTravelModeArray: ArrayAdapter<String>
    private lateinit var attendanceTravelActivityArray: ArrayAdapter<String>

    private val cal = Calendar.getInstance()

    private lateinit var attendanceUpdateDate: TextView
    private lateinit var attendanceUpdateTimeFrom: TextView
    private lateinit var attendanceUpdateTimeTo: TextView
    private lateinit var attendanceRemark: TextInputEditText
    
    private lateinit var submitButton: Button

    private  var attendanceDate: String? = null
    private  var startTime: String? = null
    private  var endTime: String? = null

    var spinnerInitialized = false

    var responBody:String = ""
    var routes: Array<TripTourPlanRouteList> = emptyArray<TripTourPlanRouteList>()
    var routesStart: Array<TripTourPlanRouteStartList> = emptyArray<TripTourPlanRouteStartList>()
    var routesEnd: Array<TripTourPlanRouteEndList> = emptyArray<TripTourPlanRouteEndList>()


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AttendanceCreateBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Add Attendance")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInsatanceState: Bundle?){
        super.onViewCreated(view, savedInsatanceState)

        attendancePlaceTypeSpinner = view.findViewById<AppCompatSpinner>(R.id.attendance_place_type)
        attendancePlaceFromSpinner = view.findViewById<AppCompatSpinner>(R.id.attendance_place_from)
        attendancePlaceToSpinner = view.findViewById<AppCompatSpinner>(R.id.attendance_place_to)
        attendanceTravelModeSpinner = view.findViewById<AppCompatSpinner>(R.id.attendance_travel_mode)
        attendanceTravelActivitySpinner = view.findViewById<AppCompatSpinner>(R.id.attendance_travel_activity)

        attendancePlaceTypeArray = ArrayAdapter(requireContext(),R.layout.item_select_spinner)
        attendancePlaceFromArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        attendancePlaceToArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        attendanceTravelModeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        attendanceTravelActivityArray = ArrayAdapter(requireContext(),R.layout.item_select_spinner)

        attendanceUpdateDate = view.findViewById<TextView>(R.id.attendance_update_date)
        attendanceUpdateTimeFrom = view.findViewById<TextView>(R.id.attendance_update_time_from)
        attendanceUpdateTimeTo = view.findViewById<TextView>(R.id.attendance_update_time_to)

        submitButton = view.findViewById(R.id.submit_attendance_plan)
        attendanceRemark = view.findViewById(R.id.attendance_remark)
        //attendancePlaceTypeArray.setDropDownViewResource()
        addDataToPlaceType()
        addDataToPlaceFrom()
        addDataToPlaceTo()
        addDataToTravelMode()
        addDataToTravelActivity()

        attendanceUpdateDate.setOnClickListener {
            showDatePicker()
        }
        attendanceUpdateTimeFrom.setOnClickListener {
            showDatePickerTimeFrom()
        }
        attendanceUpdateTimeTo.setOnClickListener {
            showDatePickerTimeTo()
        }

        attendancePlaceTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                if (!spinnerInitialized) {
                    spinnerInitialized = true
                    return
                }
                val selectedItem = parent.getItemAtPosition(position).toString()
                Log.d(TripPlanFormMeetingDetailsFragment.TAG, "onItemSelected:Spinner:Position ${position}")
                getTripPlanRouteStartEndListFromServer(routes[position].id!!.toInt())
                return
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        getTripPlanRouteListFromServer()

        submitButton.setOnClickListener {
            if(attendanceDate != null && startTime != null && endTime != null ){
                val attendanceListModel = AttendanceListModel()
               if(attendanceDate != null && attendanceTravelModeSpinner.selectedItem != null  && attendancePlaceTypeSpinner.selectedItem != null && attendancePlaceFromSpinner.selectedItem != null && attendancePlaceToSpinner.selectedItem != null && attendanceRemark.text != null && attendanceTravelActivitySpinner.selectedItem != null){
                   attendanceListModel.date = attendanceDate
                   attendanceListModel.mode =  attendanceTravelModeSpinner.selectedItem!!.toString()
                   attendanceListModel.route = attendancePlaceTypeSpinner.selectedItem!!.toString()
                   attendanceListModel.fromArea = attendancePlaceFromSpinner.selectedItem!!.toString()
                   attendanceListModel.toArea = attendancePlaceToSpinner.selectedItem!!.toString()
                   attendanceListModel.remark = attendanceRemark.text!!.toString()
                   attendanceListModel.activity = attendanceTravelActivitySpinner.selectedItem!!.toString()
                   attendanceListModel.id = System.currentTimeMillis().toInt()
               }

                Log.d(TAG, "onViewCreated: ${attendanceListModel}")

                AttendanceListDataFlow.addAttendanceList(attendanceListModel)
                AttendanceListDataFlow.updateAllData(Random.nextInt(1,100))

                dismiss()
            }
        }

    }

    private fun addDataToPlaceType(){
        val placeTypeArray = ArrayList<String>()
        placeTypeArray.add("")

        attendancePlaceTypeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeTypeArray)
        attendancePlaceTypeSpinner.adapter = attendancePlaceTypeArray
    }

    private fun addDataToPlaceFrom(){
        val placeFromArray = ArrayList<String>()
        placeFromArray.add("")

        attendancePlaceFromArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeFromArray)
        attendancePlaceFromSpinner.adapter = attendancePlaceFromArray
    }

    private fun addDataToPlaceTo(){
        val placeToArray = ArrayList<String>()
        placeToArray.add("")

        attendancePlaceToArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeToArray)
        attendancePlaceToSpinner.adapter = attendancePlaceToArray
    }
    private fun addDataToTravelMode(){
        val travelModeArray = ArrayList<String>()
        travelModeArray.add(" Bike")
        travelModeArray.add(" Car")
        travelModeArray.add(" Bus")
        travelModeArray.add(" Metro")

        attendanceTravelModeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, travelModeArray)
        attendanceTravelModeSpinner.adapter = attendanceTravelModeArray
    }

    private fun addDataToTravelActivity(){
        val travelActivityArray = ArrayList<String>()
        travelActivityArray.add("Activity 1")
        travelActivityArray.add("Activity 2")
        travelActivityArray.add("Activity 3")
        travelActivityArray.add("Activity 4")

        attendanceTravelActivityArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, travelActivityArray)
        attendanceTravelActivitySpinner.adapter = attendanceTravelActivityArray
    }

    private fun showDatePicker() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(requireContext(),R.style.MyTimePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = android.icu.text.SimpleDateFormat(myFormat, Locale.US)
            //textview_date!!.text = sdf.format(cal.getTime())
            attendanceUpdateDate.text = sdf.format(cal.getTime())

            // val dateFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val myFormat2 = "yyyy-MM-dd" // mention the format you need
            val sdf2 = android.icu.text.SimpleDateFormat(myFormat2, Locale.getDefault())
            attendanceDate = sdf2.format(cal.getTime()).toString()


        }, year, month, day)
        dpd.datePicker.minDate = cal.timeInMillis
        dpd.datePicker.maxDate = cal.timeInMillis + 604800000
        dpd.show()

    }

    private fun showDatePickerTimeFrom() {

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.MyTimePickerDialogTheme,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                // Handle the time set event
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val time = timeFormat.parse("$selectedHour:$selectedMinute")
                Log.d("Selected Time", timeFormat.format(time))
                attendanceUpdateTimeFrom.text = timeFormat.format(time).toString()
                startTime = timeFormat.format(time).toString()
            },
            hour,
            minute,
            true // true for 24-hour format
        )
        timePickerDialog.show()
    }

    private fun showDatePickerTimeTo() {

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.MyTimePickerDialogTheme,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                // Handle the time set event
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val time = timeFormat.parse("$selectedHour:$selectedMinute")
                Log.d("Selected Time", timeFormat.format(time))
                attendanceUpdateTimeTo.text =  timeFormat.format(time).toString()
                endTime = timeFormat.format(time).toString()
            },
            hour,
            minute,
            true // true for 24-hour format
        )
        timePickerDialog.show()
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
                attendancePlaceTypeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeTypeArray)
                attendancePlaceTypeSpinner.adapter = attendancePlaceTypeArray

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
                routesStart = gson.fromJson(responBody, Array<TripTourPlanRouteStartList>::class.java).toList().toTypedArray()
                for(route in routesStart){
                    placeToArray.add(route.area.toString())
                }

                attendancePlaceFromArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeToArray)
                attendancePlaceFromSpinner.adapter = attendancePlaceFromArray

                attendancePlaceToArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeToArray)
                attendancePlaceToSpinner.adapter = attendancePlaceToArray

            })
        } catch (e: Exception){
            Log.d(TripTourPlanInstituteSelectionFragment.TAG, "addListToView: ${e.toString()}")
        }
    }

    companion object {
         val TAG = AttendanceCreate::class.java.name
    }

}
