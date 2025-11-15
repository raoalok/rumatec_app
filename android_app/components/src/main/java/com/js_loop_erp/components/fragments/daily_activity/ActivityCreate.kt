package com.js_loop_erp.components.fragments.daily_activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripTourPlanRouteEndList
import  com.js_loop_erp.components.data_class.TripTourPlanRouteList
import  com.js_loop_erp.components.data_class.TripTourPlanRouteStartList
import  com.js_loop_erp.components.data_flow.ActivityListDataFlow
import  com.js_loop_erp.components.databinding.ActivityCreateBinding
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
import java.util.Calendar
import java.util.Locale
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.random.Random

class ActivityCreate : DialogFragment() {

    private var _binding: ActivityCreateBinding? = null

    private val binding get() = _binding!!

    private lateinit var activityCategoryTypeSpinner: AppCompatSpinner
    private lateinit var activityPlaceFromSpinner: AppCompatSpinner
    private lateinit var activityPlaceToSpinner: AppCompatSpinner
    private lateinit var activityTravelModeSpinner: AppCompatSpinner
    private lateinit var activityTravelActivitySpinner: AppCompatSpinner

    private lateinit var activityPlaceTypeArray: ArrayAdapter<String>
    private lateinit var activityPlaceFromArray: ArrayAdapter<String>
    private lateinit var activityPlaceToArray: ArrayAdapter<String>
    private lateinit var activityTravelModeArray: ArrayAdapter<String>
    private lateinit var activityTravelActivityArray: ArrayAdapter<String>

    private val cal = Calendar.getInstance()

    private lateinit var activityUpdateDate: TextView
    private lateinit var activityUpdateTimeFrom: TextView
    private lateinit var activityUpdateTimeTo: TextView
    private lateinit var activityKms: EditText
    private lateinit var activityRemark: TextInputEditText

    private lateinit var submitButton: Button

    private  var activityDate: String? = null
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


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dialog?.window?.setDecorFitsSystemWindows(false)
            dialog?.window?.decorView?.findViewById<View>(android.R.id.content)?.setOnApplyWindowInsetsListener { v, insets ->
                val imeInsets = insets.getInsets(WindowInsets.Type.ime())
                val navInsets = insets.getInsets(WindowInsets.Type.systemBars())

                v.setPadding(
                    v.paddingLeft,
                    v.paddingTop,
                    v.paddingRight,
                    maxOf(imeInsets.bottom, navInsets.bottom)
                )
                insets
            }
        } else {
            // Fallback for pre-Android 11
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityCreateBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Add Activity")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInsatanceState: Bundle?){
        super.onViewCreated(view, savedInsatanceState)

        activityCategoryTypeSpinner = view.findViewById<AppCompatSpinner>(R.id.activity_category)
        activityPlaceFromSpinner = view.findViewById<AppCompatSpinner>(R.id.activity_place_from)
        activityPlaceToSpinner = view.findViewById<AppCompatSpinner>(R.id.activity_place_to)
        activityTravelModeSpinner = view.findViewById<AppCompatSpinner>(R.id.activity_travel_mode)
        activityTravelActivitySpinner = view.findViewById<AppCompatSpinner>(R.id.activity_travel_activity)

        activityPlaceTypeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        activityPlaceFromArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        activityPlaceToArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        activityTravelModeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        activityTravelActivityArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)

        activityUpdateDate = view.findViewById<TextView>(R.id.activity_update_date)
        activityUpdateTimeFrom = view.findViewById<TextView>(R.id.activity_update_time_from)
        activityUpdateTimeTo = view.findViewById<TextView>(R.id.activity_update_time_to)
        activityKms = view.findViewById<EditText>(R.id.editTextKilometer)

        submitButton = view.findViewById(R.id.submit_activity_plan)
        activityRemark = view.findViewById(R.id.activity_remark)
        //activityPlaceTypeArray.setDropDownViewResource()
        addDataToPlaceType()
        addDataToPlaceFrom()
        addDataToPlaceTo()
        addDataToTravelMode()
        addDataToTravelActivity()

        activityUpdateDate.setOnClickListener {
            showDatePicker()
        }
        activityUpdateTimeFrom.setOnClickListener {
            showDatePickerTimeFrom()
        }
        activityUpdateTimeTo.setOnClickListener {
            showDatePickerTimeTo()
        }

        activityCategoryTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        submitButton.setOnClickListener click@ {
            if(activityKms.text.toString().toDoubleOrNull() == null){
                Toast.makeText(context,"Input KMs", Toast.LENGTH_LONG).show()
                return@click
            }

            if(activityDate != null && startTime != null && endTime != null && activityDate!!.length.toString().isNotEmpty()){
                val activityListModel = ActivityListModel()
                activityListModel.date = activityDate
                activityListModel.mode =  activityTravelModeSpinner.selectedItem.toString()
                activityListModel.category = activityCategoryTypeSpinner.selectedItem.toString()
                //activityListModel.fromArea = activityPlaceFromSpinner.selectedItem.toString()
                //activityListModel.toArea = activityPlaceToSpinner.selectedItem.toString()
                activityListModel.remark = activityRemark.text.toString()
                activityListModel.activity = activityTravelActivitySpinner.selectedItem.toString()
                activityListModel.activityKms = activityKms.text.toString().toDoubleOrNull()
                activityListModel.id = System.currentTimeMillis().toInt()
                activityListModel.startTime = startTime
                activityListModel.endTime = endTime
                Log.d(TAG, "onViewCreated: ${activityListModel}")

                ActivityListDataFlow.addActivityList(activityListModel)
                ActivityListDataFlow.updateAllData(Random.nextInt(1,100))

                dismiss()
            }
        }

    }

    fun String.toDoubleOrNull(): Double? {
        return try {
            if (this.startsWith(".")) {
                "0$this".toDouble()
            } else {
                this.toDouble()
            }
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun addDataToPlaceType(){
        val placeTypeArray = ArrayList<String>()
        placeTypeArray.add("HQ")
        placeTypeArray.add("OS")
        placeTypeArray.add("EXHQ")


        activityPlaceTypeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeTypeArray)
        activityCategoryTypeSpinner.adapter = activityPlaceTypeArray
    }

    private fun addDataToPlaceFrom(){
        val placeFromArray = ArrayList<String>()
        placeFromArray.add("")

        activityPlaceFromArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeFromArray)
        activityPlaceFromSpinner.adapter = activityPlaceFromArray
    }

    private fun addDataToPlaceTo(){
        val placeToArray = ArrayList<String>()
        placeToArray.add("")


        activityPlaceToArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeToArray)
        activityPlaceToSpinner.adapter = activityPlaceToArray
    }
    private fun addDataToTravelMode(){
        val travelModeArray = ArrayList<String>()
        travelModeArray.add(" Bike")
        travelModeArray.add(" Car")
        travelModeArray.add(" Bus")
        travelModeArray.add(" Metro")

        activityTravelModeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, travelModeArray)
        activityTravelModeSpinner.adapter = activityTravelModeArray
    }

    private fun addDataToTravelActivity(){

        val travelActivityArray = arrayListOf(
            "Clinic Visit",
            "Pharmacy Visit",
            "Hospital Visit",
            "Retail Store Visit",
            "Product Demonstration",
            "Sales Follow-up",
            "New Client Meeting",
            "Sample Distribution",
            "Health Camp Support",
            "Training Session",
            "Distributor Visit",
            "Territory Survey",
            "Internal Meeting",
            "Break / Personal Time"
        )

        activityTravelActivityArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, travelActivityArray)
        activityTravelActivitySpinner.adapter = activityTravelActivityArray
    }

    private fun showDatePicker() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(requireContext(),
            R.style.MyTimePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            //textview_date!!.text = sdf.format(cal.getTime())
            activityUpdateDate.text = sdf.format(cal.getTime())

            // val dateFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val myFormat2 = "yyyy-MM-dd" // mention the format you need
            val sdf2 = SimpleDateFormat(myFormat2, Locale.getDefault())
            activityDate = sdf2.format(cal.getTime()).toString()


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
                val timeFormat = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
                val time = timeFormat.parse("$selectedHour:$selectedMinute")
                Log.d("Selected Time", timeFormat.format(time))
                activityUpdateTimeFrom.text = timeFormat.format(time).toString()
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
                val timeFormat = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
                val time = timeFormat.parse("$selectedHour:$selectedMinute")
                Log.d("Selected Time", timeFormat.format(time))
                activityUpdateTimeTo.text =  timeFormat.format(time).toString()
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

                Log.d(LoginFragment.TAG, "loginCheck: Debug Build Bypass the SSL check")

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
                //.url("http://65.0.61.137/api/inventory/sample")
                //.url("http://65.0.61.137/api/expenses/agent")
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

//                val placeTypeArray = ArrayList<String>()
//                placeTypeArray.add("Place 1")
//                placeTypeArray.add("Place 2")
//                placeTypeArray.add("Place 3")
//                placeTypeArray.add("Place 4")

//                routeListTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeTypeArray)
//                routeListTripPlanSpinner.adapter = routeListTripPlanArray
               // activityPlaceTypeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeTypeArray)
               // activityCategoryTypeSpinner.adapter = activityPlaceTypeArray

            })
        } catch (e: Exception){
            Log.d(TripTourPlanInstituteSelectionFragment.TAG, "addListToView: ${e.toString()}")
        }
    }

    fun getTripPlanRouteStartEndListFromServer(id: Int){
        if (ReceiverMediator.USER_TOKEN.length > 8) {
            var client = OkHttpClient()


            if(BuildConfig.DEBUG_ONLY_BUILD){

                Log.d(com.js_loop_erp.components.fragments.LoginFragment.TAG, "loginCheck: Debug Build Bypass the SSL check")

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
                //.url("http://65.0.61.137/api/inventory/sample")
                //.url("http://65.0.61.137/api/expenses/agent")
//                .url("http://65.0.61.137/api/application/routes/ids")
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

//                val placeTypeArray = ArrayList<String>()
//                placeTypeArray.add("Place 1")
//                placeTypeArray.add("Place 2")
//                placeTypeArray.add("Place 3")
//                placeTypeArray.add("Place 4")

//                toAreaTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeToArray)
//                toAreaTripPlanSpinner.adapter = toAreaTripPlanArray
                activityPlaceFromArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeToArray)
                activityPlaceFromSpinner.adapter = activityPlaceFromArray

//                fromAreaTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeToArray)
//                fromAreaTripPlanSpinner.adapter = fromAreaTripPlanArray
                activityPlaceToArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeToArray)
                activityPlaceToSpinner.adapter = activityPlaceToArray

            })
        } catch (e: Exception){
            Log.d(TripTourPlanInstituteSelectionFragment.TAG, "addListToView: ${e.toString()}")
        }
    }

    companion object {
        val TAG: String = ActivityCreate::class.java.name
    }

}
