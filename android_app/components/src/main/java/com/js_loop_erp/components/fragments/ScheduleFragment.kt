package com.js_loop_erp.components.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.ScheduleFragmentBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ScheduleFragment: DialogFragment() {
    private var _binding: ScheduleFragmentBinding? = null

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    private lateinit var spinner_: AppCompatSpinner
    private lateinit var dataAdapter: ArrayAdapter<String>

    private lateinit var scheduleAppointmentSpinner: AppCompatSpinner
    private lateinit var dataAdapterScheduleAppointmentSpinner: ArrayAdapter<String>

    private lateinit var submitScheduleButton: Button

    var dataSchedule = ArrayList<ScheduleViewModel>()
    var dataSchedule_ = ArrayList<String>()
    var products = ArrayList<ScheduleViewModel>()

    private val cal = Calendar.getInstance()
    var textview_date: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInsatanceState: Bundle?
        ): View {
        _binding = ScheduleFragmentBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Schedule Planning")
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Set your animation style here
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner_ = view.findViewById<AppCompatSpinner>(R.id.schedule_list_spinner)
        dataAdapter = ArrayAdapter(requireContext(), R.layout.schedule_reason_spinner)
        submitScheduleButton = view.findViewById<Button>(R.id.add_to_schedule_button)

        scheduleAppointmentSpinner = view.findViewById<AppCompatSpinner>(R.id.schedule_visit_reason_spinner)
        dataAdapterScheduleAppointmentSpinner = ArrayAdapter(requireContext(),R.layout.schedule_reason_spinner)

        textview_date =  view.findViewById<TextView>(R.id.text_view_date_1)

        textview_date!!.text = "--/--/----"

        submitScheduleButton.setOnClickListener {
            dismiss()
            //showDatePicker()
        }
        textview_date!!.setOnClickListener{
            showDatePicker()
        }

        addDataToSpinnerSchedule()
        getClientListFromServer()

        val chart: BarChart = view.findViewById(R.id.chart)

        val entries = mutableListOf<BarEntry>()
        // Add your data entries here
        entries.add(BarEntry(1f, 50f))
        entries.add(BarEntry(2f, 80f))
        entries.add(BarEntry(3f, 60f))
        entries.add(BarEntry(4f, 70f))
        entries.add(BarEntry(5f, 90f))

        val entries2 = mutableListOf<BarEntry>()
        // Add your data entries2 here
        entries2.add(BarEntry(1f, 40f))
        entries2.add(BarEntry(2f, 60f))
        entries2.add(BarEntry(3f, 50f))
        entries2.add(BarEntry(4f, 30f))
        entries2.add(BarEntry(5f, 70f))

        val dataSet = BarDataSet(entries, "Actual Sales")
        val dataSet2 = BarDataSet(entries2, "Projected Sales")

        dataSet.color = Color.BLUE
        dataSet2.color = Color.GREEN

        val data = BarData(dataSet, dataSet2)
        chart.data = data

        val xAxis: XAxis = chart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val date = Date(value.toLong())
                return dateFormat.format(date)
            }
        }

        chart.invalidate()
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }

    fun addDataToSpinnerSchedule(){
        var responBody : String = ""

        if(ReceiverMediator.USER_TOKEN.length > 0){
            val client = OkHttpClient.Builder()
                .connectTimeout(20,java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("http://65.0.61.137/api/inventory/sample")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("Authorization","Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()
            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: java.io.IOException){
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response){
                    response.use {
                        if (!response.isSuccessful){

                        } else {
                            responBody = response.body?.string() ?: ""
                           // println(responBody)
                            addDataToSpinner(responBody)
                        }
                    }
                }
            })
        }
    }



    fun addDataToSpinner(itemDataList: String){
        try{
        activity?.runOnUiThread(Runnable{
            val gson = Gson()
            products = gson.fromJson(itemDataList,Array<ScheduleViewModel>::class.java).toList() as ArrayList<ScheduleViewModel>
            for(product in products) {
                val product_ = "Product Id: "+ product.productId.toString() + ", " + "Batch: " + product.batch.toString() + ", " + "Stock:" + product.stock.toString()
                dataSchedule_.add(product_)
            }

            dataAdapter = ArrayAdapter(requireContext(),R.layout.schedule_reason_spinner,dataSchedule_)
            spinner_.setAdapter(dataAdapter)

            Log.d(InventoryUpdateFragment.TAG, "addDataToSpinner: ${dataSchedule_[0].toString()}")
            //spinner_.setSelection(0)
            dataAdapter.setDropDownViewResource(R.layout.select_spinner_)
            //  spinner_.adapter = dataAdapter
        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    fun getClientListFromServer(){
        activity?.runOnUiThread(Runnable{
            val data_ = ArrayList<String>()
            data_.add("New Visit")
            data_.add("Follow-up Visit")
            data_.add("New Meeting Visit")
            data_.add("Sample Redelivry")
            dataAdapterScheduleAppointmentSpinner =  ArrayAdapter(requireContext(),R.layout.schedule_reason_spinner, data_)
            scheduleAppointmentSpinner.setAdapter(dataAdapterScheduleAppointmentSpinner)
            dataAdapterScheduleAppointmentSpinner.setDropDownViewResource(R.layout.select_spinner_)
        })
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

            updateDateInView()

            Log.d("dsadsadasd","dasdasdsad")

        }, year, month, day)
        dpd.datePicker.minDate = cal.timeInMillis
        dpd.datePicker.maxDate = cal.timeInMillis + 604800000
        dpd.show()

    }

    val dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                               dayOfMonth: Int) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
    }

    companion object {
        const val TAG = "ScheduleFragment"
    }
}