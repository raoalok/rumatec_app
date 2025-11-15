package com.js_loop_erp.components.fragments.tripTourPlan

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
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.size
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripTourPlanMeetingDetailsSelection
import  com.js_loop_erp.components.data_class.TripTourPlanRouteEndList
import  com.js_loop_erp.components.data_class.TripTourPlanRouteList
import  com.js_loop_erp.components.data_class.TripTourPlanRouteStartList
import  com.js_loop_erp.components.data_flow.TripPlanFormSharedFlow
import  com.js_loop_erp.components.databinding.TripPlanFormMeetingDetailsFragmentBinding
import com.js_loop_erp.components.fragments.LoginFragment
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

class TripPlanFormMeetingDetailsFragment: DialogFragment() {
    var _binding: TripPlanFormMeetingDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    
    
    private lateinit var routeListTripPlanSpinner: AppCompatSpinner
    private lateinit var fromAreaTripPlanSpinner: AppCompatSpinner
    private lateinit var toAreaTripPlanSpinner: AppCompatSpinner
    private lateinit var modeTripPlanSpinner: AppCompatSpinner
    private lateinit var trip_planTravelActivitySpinner: AppCompatSpinner

    private lateinit var routeListTripPlanArray: ArrayAdapter<String>
    private lateinit var fromAreaTripPlanArray: ArrayAdapter<String>
    private lateinit var toAreaTripPlanArray: ArrayAdapter<String>
    private lateinit var modeTripPlanArray: ArrayAdapter<String>
    
    private lateinit var trip_planTravelActivityArray: ArrayAdapter<String>

    private val cal = Calendar.getInstance()

    private lateinit var trip_planUpdateDate: TextView
    private lateinit var trip_planUpdateTimeFrom: TextView
    private lateinit var trip_planUpdateTimeTo: TextView


    private lateinit var submitButton: Button
    var spinnerInitialized = false


    var responBody:String = ""
    var routes: Array<TripTourPlanRouteList> = emptyArray<TripTourPlanRouteList>()
    var routesStart: Array<TripTourPlanRouteStartList> = emptyArray<TripTourPlanRouteStartList>()
    var routesEnd: Array<TripTourPlanRouteEndList> = emptyArray<TripTourPlanRouteEndList>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart(){
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TripPlanFormMeetingDetailsFragmentBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Add Meeting Detail")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInsatanceState: Bundle?) {
        super.onViewCreated(view, savedInsatanceState)

        routeListTripPlanSpinner = view.findViewById<AppCompatSpinner>(R.id.trip_plan_place_type)
        fromAreaTripPlanSpinner = view.findViewById<AppCompatSpinner>(R.id.trip_plan_place_from)
        toAreaTripPlanSpinner = view.findViewById<AppCompatSpinner>(R.id.trip_plan_place_to)
        modeTripPlanSpinner = view.findViewById<AppCompatSpinner>(R.id.trip_plan_travel_mode)
        trip_planTravelActivitySpinner = view.findViewById<AppCompatSpinner>(R.id.trip_plan_travel_activity)

        routeListTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        fromAreaTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        toAreaTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        modeTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)
        trip_planTravelActivityArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner)

        trip_planUpdateDate = view.findViewById<TextView>(R.id.trip_plan_update_date)
        trip_planUpdateTimeFrom = view.findViewById<TextView>(R.id.trip_plan_update_time_from)
        trip_planUpdateTimeTo = view.findViewById<TextView>(R.id.trip_plan_update_time_to)

        submitButton = view.findViewById(R.id.submit_trip_meeting_plan)

        //routeListTripPlanArray.setDropDownViewResource()
        addDataToPlaceType()
        addDataToPlaceFrom()
        addDataToPlaceTo()
        addDataToTravelMode()
        addDataToTravelActivity()

        trip_planUpdateDate.setOnClickListener {
            showDatePicker()
        }
        trip_planUpdateTimeFrom.setOnClickListener {
            showDatePickerTimeFrom()
        }
        trip_planUpdateTimeTo.setOnClickListener {
            showDatePickerTimeTo()
        }


/*        routeListTripPlanSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (!spinnerInitialized) {
                    spinnerInitialized = true
                    return
                }
                //val selecteditem:Int = parent.getItemIdAtPosition(position)
                Log.d(TAG, "onItemSelected:Spinner:Position ${position}")
                getTripPlanRouteStartEndListFromServer(routes[position].id!!.toInt())
                // Do something with the selected item
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do something when nothing is selected
            }
        }*/

/*
        routeListTripPlanSpinner.setOnItemClickListener { parent, view, position, id ->
            //routeListTripPlanSpinner.selectedItemPosition
            Log.d(TAG, "onItemSelected:Spinner:Position ${position}")
            getTripPlanRouteStartEndListFromServer(routes[ position].id!!.toInt())
        }
*/

        routeListTripPlanSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                if (!spinnerInitialized) {
                    spinnerInitialized = true
                    return
                }
                val selectedItem = parent.getItemAtPosition(position).toString()
                Log.d(TAG, "onItemSelected:Spinner:Position ${position}")
                getTripPlanRouteStartEndListFromServer(routes[position].id!!.toInt())
                return
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        submitButton.setOnClickListener {

//            val tripMeetingDetails = TripTourPlanMeetingDetailsSelection()
//
//            tripMeetingDetails.category = routeListTripPlanSpinner.selectedItem.toString()
//            tripMeetingDetails.startPlace = fromAreaTripPlanSpinner.selectedItem.toString()
//            tripMeetingDetails.endPlace = toAreaTripPlanSpinner.selectedItem.toString()
//            tripMeetingDetails.travelMode = modeTripPlanSpinner.selectedItem.toString()
//
//            tripMeetingDetails.categoryId   = routes[routeListTripPlanSpinner.selectedItemId.toInt()].id
//            tripMeetingDetails.startPlaceId = routesStart[fromAreaTripPlanSpinner.selectedItemId.toInt()].id
//            tripMeetingDetails.endPlaceId   = routesStart[toAreaTripPlanSpinner.selectedItemId.toInt()].id
//
//            TripPlanFormSharedFlow.updateTripPlanFormMeetingDetails(arrayOf(tripMeetingDetails))
//
//            TripPlanFormFragmnet.ROUTE_ID =  routes[routeListTripPlanSpinner.selectedItemId.toInt()].id!!.toInt()
//
//            dismiss()

            val routePos = routeListTripPlanSpinner.selectedItemPosition
            val fromPos = fromAreaTripPlanSpinner.selectedItemPosition
            val toPos = toAreaTripPlanSpinner.selectedItemPosition

            if (routePos == AdapterView.INVALID_POSITION || fromPos == AdapterView.INVALID_POSITION || toPos == AdapterView.INVALID_POSITION) {
                Toast.makeText(requireContext(), "Select all fields before proceeding", Toast.LENGTH_SHORT).show()
            } else {
                val tripMeetingDetails = TripTourPlanMeetingDetailsSelection().apply {
                    category     = routeListTripPlanSpinner.selectedItem?.toString().orEmpty()
                    startPlace   = fromAreaTripPlanSpinner.selectedItem?.toString().orEmpty()
                    endPlace     = toAreaTripPlanSpinner.selectedItem?.toString().orEmpty()
                    travelMode   = modeTripPlanSpinner.selectedItem?.toString().orEmpty()

                    categoryId   = routes.getOrNull(routePos)?.id
                    startPlaceId = routesStart.getOrNull(fromPos)?.id
                    endPlaceId   = routesStart.getOrNull(toPos)?.id
                }

                TripPlanFormSharedFlow.updateTripPlanFormMeetingDetails(arrayOf(tripMeetingDetails))

                TripPlanFormFragmnet.ROUTE_ID = routes.getOrNull(routePos)?.id?.toInt() ?: -1

                dismiss()
            }

        }

    }

    private fun addDataToPlaceType() {
        val placeTypeArray = ArrayList<String>()
        placeTypeArray.add("Select Route")

        routeListTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeTypeArray)
        routeListTripPlanSpinner.adapter = routeListTripPlanArray

        getTripPlanRouteListFromServer()

    }

    private fun addDataToPlaceFrom() {
        val placeFromArray = ArrayList<String>()
        placeFromArray.add("")
        fromAreaTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeFromArray)
        fromAreaTripPlanSpinner.adapter = fromAreaTripPlanArray
    }

    private fun addDataToPlaceTo() {
        val placeToArray = ArrayList<String>()
        placeToArray.add("")
        toAreaTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeToArray)
        toAreaTripPlanSpinner.adapter = toAreaTripPlanArray
    }

    private fun addDataToTravelMode() {
        val travelModeArray = ArrayList<String>()
        travelModeArray.add(" Bike")
        travelModeArray.add(" Car")
        travelModeArray.add(" Bus")
        travelModeArray.add(" Metro")

        modeTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, travelModeArray)
        modeTripPlanSpinner.adapter = modeTripPlanArray
    }

    private fun addDataToTravelActivity() {
        val travelActivityArray = ArrayList<String>()
        travelActivityArray.add(" Bike")
        travelActivityArray.add(" Car")
        travelActivityArray.add(" Bus")
        travelActivityArray.add(" Metro")
        travelActivityArray.add(" Flight")

        trip_planTravelActivityArray =
            ArrayAdapter(requireContext(), R.layout.item_select_spinner, travelActivityArray)
        trip_planTravelActivitySpinner.adapter = trip_planTravelActivityArray
    }

    private fun showDatePicker() {

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
                trip_planUpdateDate.text = sdf.format(cal.getTime())

            },
            year,
            month,
            day
        )
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
                trip_planUpdateTimeFrom.text = timeFormat.format(time).toString()
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
                trip_planUpdateTimeTo.text = timeFormat.format(time).toString()
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
                //.url("http://65.0.61.137/api/application/routes/ids")
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/routes/ids")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()

            Log.d(TAG, "getTripPlanRouteListFromServer: ${ReceiverMediator.USER_TOKEN.length}  ${"${ReceiverMediator.SERVER_SYNC_URI}/api/application/routes/ids"}")

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            Log.d(TAG, "onResponse: $response")
                        } else {
                            responBody = response.body?.string() ?: ""
                            //println(responBody)
                            addRouteListToView(responBody)
                        }
                    }
                }
            })
        } else {
            Log.d(TAG, "getTripPlanRouteListFromServer: Missing Token")
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

                routeListTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeTypeArray)
                routeListTripPlanSpinner.adapter = routeListTripPlanArray
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
                //.url("http://65.0.61.137/api/inventory/sample")
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


                toAreaTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeToArray)
                toAreaTripPlanSpinner.adapter = toAreaTripPlanArray

                fromAreaTripPlanArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeToArray)
                fromAreaTripPlanSpinner.adapter = fromAreaTripPlanArray

            })
        } catch (e: Exception){
            Log.d(TAG, "addListToView: ${e.toString()}")
        }
    }

    companion object {
        val TAG = TripPlanFormMeetingDetailsFragment::class.java.name
    }
}
