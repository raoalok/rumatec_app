package com.js_loop_erp.components.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.activity.addCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.TripPlanCustomAdapter
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_flow.TripPlanFormSharedFlow
import  com.js_loop_erp.components.databinding.TripPlanFragmentBinding
import  com.js_loop_erp.components.fragments.tripTourPlan.TripPlanDetailsViewFragment
import  com.js_loop_erp.components.fragments.tripTourPlan.TripPlanFormFragmnet
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.math.log


class TripPlanFragment : DialogFragment(), TripPlanRecyclerViewItemClickListenerI {
    private var _binding: TripPlanFragmentBinding? = null

    private val binding get() = _binding!!

    private val cal = Calendar.getInstance()

    private lateinit var calendarDateChange: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var tripPlanButton: FloatingActionButton

    private var containerId: Int = 0

    var responBody: String = ""
     var formattedDate2: String = ""
     var formattedDate: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TripPlanFragmentBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Tour Trip Plan")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInsatanceState: Bundle?) {
        super.onViewCreated(view, savedInsatanceState)

        calendarDateChange = view.findViewById<CalendarView>(R.id.calendar_view)
        recyclerView = view.findViewById(R.id.trip_plan_fragment_recycler_view)
        tripPlanButton = view.findViewById<FloatingActionButton>(R.id.trip_plan_button)

        recyclerView.layoutManager = LinearLayoutManager(context)
        val data = ArrayList<TripPlanViewModel>()
        val adapter = TripPlanCustomAdapter(this,data)

        val currentDate = Calendar.getInstance().timeInMillis

        calendarDateChange.date = currentDate

        view.rootView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback {
            dismiss()
        }

        calendarDateChange.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val date = calendar.time

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formattedDate = dateFormat.format(date)
            formattedDate2 = dateFormat2.format(calendar.time)
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
        }


        tripPlanButton.setOnClickListener{

            TripPlanFormSharedFlow.updateTripPlanFormMeetingDetails(emptyArray())
            TripPlanFormFragmnet.ROUTE_ID = 0

            if(formattedDate == ""){
                val dateInMillis = calendarDateChange.date
                val calendar = Calendar.getInstance()
                calendar.timeInMillis =  dateInMillis //currentDate
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                formattedDate = dateFormat.format(calendar.time)
                formattedDate2 = dateFormat2.format(calendar.time)
            }

            if(formattedDate != ""){
                val childDialog = TripPlanFormFragmnet.newInstance(formattedDate,formattedDate2, "0")
                childDialog.show(childFragmentManager, "TripPlanFormFragment")
            }

        }

        recyclerView.adapter = adapter

        val itemDataList = getTripPlanListFromServer()

        lifecycleScope.launch {
            TripPlanFormSharedFlow.refreshTripPlanFormSubmit.collect { updates ->
                Log.d(TAG, "onViewCreated...: ${updates}")
                if (updates > 0) {
                    activity?.runOnUiThread(Runnable {
                        getTripPlanListFromServer()
                    })
                }
            }
        }
    }

    fun navigateBackToParent() {
        requireActivity().supportFragmentManager.popBackStack()
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

            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = cal.timeInMillis
        dpd.datePicker.maxDate = cal.timeInMillis + 604800000
        dpd.show()

    }

    private fun getTripPlanListFromServer(){
//        var responBody: String = ""

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
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/list")
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
                            // println(responBody)
                            addListToView(responBody)
                        }
                    }
                }
            })
        }

    }

    fun addListToView(itemDataList: String) {
        try{
        activity?.runOnUiThread(Runnable{
            var data = ArrayList<TripPlanViewModel>()
            val gson = Gson()
            val products = gson.fromJson(itemDataList, Array<TripPlanViewModel>::class.java).toList()
            for(product in products){
                data.add(product)
            }
            Log.d(TAG, "addListToView: Tour_Trip_Plan:${data.size} ${data}")
            val adapter = TripPlanCustomAdapter(this, data)
            recyclerView.adapter = adapter
            view?.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                    Log.d(TAG, "onKey: ")
                    return true
                }
            })
        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    override fun onItemClick(category: Int, position: Int) {
        if(category ==3){
            val fragment = TripPlanDetailsViewFragment.newInstance(position.toString())
            fragment.show(childFragmentManager, "TripPlanDetailsViewFragment")
        } else if(category == 2){
            deleteTripPlanFromServer(position)
        } else if(category == 1){

            val dateInMillis = calendarDateChange.date
            //val currentDate =  dateInMillis //Date(dateInMillis)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis =  dateInMillis //currentDate
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formattedDate = dateFormat.format(calendar.time)
            formattedDate2 = dateFormat2.format(calendar.time)

            val childDialog = TripPlanFormFragmnet.newInstance(formattedDate,formattedDate2, position.toString())
            childDialog.show(childFragmentManager, "TripPlanFormFragment")
        }
    }

    fun deleteTripPlanFromServer(position: Int){

        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Tour Plan?")
        builder.setPositiveButton("Ok") { _, _ ->
            deleteFromServer(position)
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }


    fun deleteFromServer(position: Int) {
        Log.d(ExpenseEditFragment.TAG, "deleteFromServer: ${position} ")
        var responBody: String = " "
        val mediaType = "text/plain".toMediaType()
        val body = "".toRequestBody(mediaType)

        // lifecycleScope.launch {
        var client = OkHttpClient()

        if (ReceiverMediator.USER_TOKEN.length > 8) {
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
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/${position}")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            Log.d(ExpenseEditFragment.TAG, "deleteFromServer: failure ${response} ")

                        } else {
                            //getExpenseListFromServer()
                            Log.d(ExpenseEditFragment.TAG, "deleteFromServer: success ${response} ")
                            getTripPlanListFromServer()
                            //recyclerView.adapter?.notifyItemRemoved(itemPosition)
                            //recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            })
        }
    }

    companion object {
        val TAG = TripPlanFragment::class.java.name
    }

    fun newInstance(containerId: Int): TripPlanFragment {
        val fragment = TripPlanFragment()
        fragment.containerId = containerId
        return fragment
    }


}

interface TripPlanRecyclerViewItemClickListenerI {
    fun onItemClick(category: Int, position: Int)
}
