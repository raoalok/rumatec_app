package com.js_loop_erp.components.fragments.tripReportSubmit

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.TripTourUpdateCustomAdapter
import  com.js_loop_erp.components.databinding.TripTourReportFragmentBinding
import  com.js_loop_erp.components.fragments.ExpenseEditFragment
import  com.js_loop_erp.components.fragments.TripTourViewModel
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

class TripTourReportFragmnet : DialogFragment(), TripTourRecyclerViewItemClickListenerI {
    private var _binding: TripTourReportFragmentBinding? = null

    private val binding get() = _binding!!

    private val cal = Calendar.getInstance()

    private lateinit var calendarDateChange: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var TripTourButton: FloatingActionButton

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
        _binding = TripTourReportFragmentBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Trip Plan")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInsatanceState: Bundle?) {
        super.onViewCreated(view, savedInsatanceState)

        calendarDateChange = view.findViewById<CalendarView>(R.id.calendar_view)
        recyclerView = view.findViewById(R.id.trip_tour_fragment_recycler_view)
        TripTourButton = view.findViewById<FloatingActionButton>(R.id.trip_tour_button)

        recyclerView.layoutManager = LinearLayoutManager(context)
        val data = ArrayList<TripTourViewModel>()
        val adapter = TripTourUpdateCustomAdapter(this,data)

        val currentDate = Calendar.getInstance().timeInMillis

        // Set the date on the CalendarView
        calendarDateChange.date = currentDate

        view.rootView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback {
            dismiss()
        }

        calendarDateChange.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Display the clicked date


            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val date = calendar.time

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formattedDate = dateFormat.format(date)
            formattedDate2 = dateFormat2.format(calendar.time)
            // Now you can use the formattedDate string


            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            //Toast.makeText(context, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()
        }


        TripTourButton.setOnClickListener{

            if(formattedDate == ""){
                val dateInMillis = calendarDateChange.date
                //val currentDate =  dateInMillis //Date(dateInMillis)
                val calendar = Calendar.getInstance()
                calendar.timeInMillis =  dateInMillis //currentDate
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                formattedDate = dateFormat.format(calendar.time)
                formattedDate2 = dateFormat2.format(calendar.time)
            }

            if(formattedDate != ""){
                /*val childDialog = TripTourFormFragmnet.newInstance(formattedDate,formattedDate2, "0")
                childDialog.show(childFragmentManager, "TripTourFormFragment")*/
            }

        }

        recyclerView.adapter = adapter

        val itemDataList = getTripTourListFromServer()


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

    private fun getTripTourListFromServer(){
//        var responBody: String = ""

        if(ReceiverMediator.USER_TOKEN.length > 8) {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
//                .url("http://65.0.61.137/api/inventory/sample")
                .url(" http://65.0.61.137/api/application/tours/list")
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
                var data = ArrayList<TripTourViewModel>()
                val gson = Gson()
                val products = gson.fromJson(itemDataList, Array<TripTourViewModel>::class.java).toList()
                for(product in products){
                    data.add(product)
                }


                val adapter = TripTourUpdateCustomAdapter(this, data)

                recyclerView.adapter = adapter

                view?.setOnKeyListener(object : View.OnKeyListener {
                    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                        Log.d(TAG, "onKey: kdslfkasl")
                        return true
                    }
                })
            })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    override fun onItemClick(category: Int, position: Int) {
        //Toast.makeText(context, " Category: ${category} Position: $position", Toast.LENGTH_SHORT).show()
        if(category ==3){
            /*val fragment = TripTourDetailsViewFragment.newInstance(position.toString())
            fragment.show(childFragmentManager, "TripTourDetailsViewFragment")*/
        } else if(category == 2){
            deleteTripTourFromServer(position)
        } else if(category == 1){

            val dateInMillis = calendarDateChange.date
            //val currentDate =  dateInMillis //Date(dateInMillis)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis =  dateInMillis //currentDate
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formattedDate = dateFormat.format(calendar.time)
            formattedDate2 = dateFormat2.format(calendar.time)

            /*val childDialog = TripTourFormFragmnet.newInstance(formattedDate,formattedDate2, position.toString())
            childDialog.show(childFragmentManager, "TripTourFormFragment")*/
        }
    }

    fun deleteTripTourFromServer(position: Int){
        // TODO("Not yet implemented")
        //deleteFromServer(position, 0)
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Tour Plan?")
        //builder.setMessage("Device Location is Required.")
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

        if (ReceiverMediator.USER_TOKEN.length > 8) {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                //.url("http://65.0.61.137/api/application/expenses/${position}")
                .url("http://65.0.61.137/api/application/tours/${position}")
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
                            getTripTourListFromServer()
                            //recyclerView.adapter?.notifyItemRemoved(itemPosition)
                            //recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            })
        }
        //   }
    }

    companion object {
        val TAG = TripTourReportFragmnet::class.java.name
    }

    fun newInstance(containerId: Int): TripTourReportFragmnet {
        val fragment = TripTourReportFragmnet()
        fragment.containerId = containerId
        return fragment
    }


}

interface TripTourRecyclerViewItemClickListenerI {
    fun onItemClick(category: Int, position: Int)
}
