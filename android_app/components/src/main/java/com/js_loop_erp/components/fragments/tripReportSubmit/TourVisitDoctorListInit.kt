package com.js_loop_erp.components.fragments.tripReportSubmit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.marginBottom
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.TripSubmitCompanionAdapter
import  com.js_loop_erp.components.adapter.TripSubmitProductsAdapter
import  com.js_loop_erp.components.adapter.TripSubmitSamplesAdapter
import  com.js_loop_erp.components.data_class.ProductList
import  com.js_loop_erp.components.data_class.ProductListSamples
import  com.js_loop_erp.components.data_class.ProductsTripReport
import  com.js_loop_erp.components.data_class.SamplesUpdate
import  com.js_loop_erp.components.data_class.SelectedCompanion
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlan
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanCompanion
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanDoctor
import  com.js_loop_erp.components.data_flow.TripTravelReportSubmitSharedFlow
import  com.js_loop_erp.components.databinding.TourVisitDoctorListInitBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class TourVisitDoctorListInit : DialogFragment() {
    private var _binding: TourVisitDoctorListInitBinding? = null
    private val binding get() = _binding!!

    private lateinit var  tripReportSubmitAddDoctor    :AppCompatImageView
    private lateinit var  tripReportSubmitAddCmpanion  :AppCompatImageView
    private lateinit var  tripReportSubmitAddProducts  :AppCompatImageView
    private lateinit var  tripReportSubmitAddSamples   :AppCompatImageView

    private lateinit var tripReportAddDate             : TextView

    private lateinit var recyclerViewCompanion  : RecyclerView
    private lateinit var recyclerViewProducts   : RecyclerView
    private lateinit var recyclerViewSamples    : RecyclerView

    private lateinit var recyclerViewCompanionAdapter  : TripSubmitCompanionAdapter
    private lateinit var recyclerViewProductsAdapter   : TripSubmitProductsAdapter
    private lateinit var recyclerViewSamplesAdapter    : TripSubmitSamplesAdapter

    private lateinit var doctorName : TextView
    private lateinit var tripSubmitData             : Button

    private val cal = Calendar.getInstance()
    private lateinit var  timePicker: TimePicker
    private var dateSubmit  : String? = null
    private var timeSubmit  : String = "T$10:00:00.000Z"

    private var meetingTime : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogGreyTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TourVisitDoctorListInitBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Trip Plan Submit Doctors Details")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInsatanceState: Bundle?) {
        super.onViewCreated(view, savedInsatanceState)

        tripReportSubmitAddDoctor       =   view.findViewById<AppCompatImageView>(R.id.trip_plan_report_visit_doctor_add_doctor)
        tripReportSubmitAddCmpanion     =   view.findViewById<AppCompatImageView>(R.id.trip_plan_report_visit_doctor_add_companion)
        tripReportSubmitAddProducts     =   view.findViewById<AppCompatImageView>(R.id.trip_plan_report_visit_doctor_add_products)
        tripReportSubmitAddSamples      =   view.findViewById<AppCompatImageView>(R.id.trip_plan_report_visit_doctor_add_samples)

        tripReportAddDate               =   view.findViewById<TextView>(R.id.trip_report_add_date)

        timePicker                      =   view.findViewById<TimePicker>(R.id.timePicker)
        doctorName                      =   view.findViewById<TextView>(R.id.update_tour_plan_update_doctor_name_field)

        tripSubmitData                  =   view.findViewById<Button>(R.id.submit_update_trip_plan_plan)

        recyclerViewCompanion  =     view.findViewById<RecyclerView>(R.id.update_trip_plan_update_companion_recycler_view)
        recyclerViewProducts   =     view.findViewById<RecyclerView>(R.id.update_trip_plan_update_products_recycler_view)
        recyclerViewSamples    =     view.findViewById<RecyclerView>(R.id.update_trip_plan_update_samples_recycler_view)

        recyclerViewCompanion.layoutManager  = LinearLayoutManager(context)
        recyclerViewProducts.layoutManager   = LinearLayoutManager(context)
        recyclerViewSamples.layoutManager    = LinearLayoutManager(context)



        val dataAdapterCompanion = ArrayList<TripSubmitReportSelectedTourPlanCompanion>()
        recyclerViewCompanionAdapter = TripSubmitCompanionAdapter(dataAdapterCompanion)
        recyclerViewCompanion.adapter = recyclerViewCompanionAdapter

        val dataAdapterProduct= ArrayList<ProductList>()
        recyclerViewProductsAdapter = TripSubmitProductsAdapter(dataAdapterProduct)
        recyclerViewProducts.adapter = recyclerViewProductsAdapter

        val dataAdapterSamples= ArrayList<ProductListSamples>()
        recyclerViewSamplesAdapter = TripSubmitSamplesAdapter(dataAdapterSamples)
        recyclerViewSamples.adapter = recyclerViewSamplesAdapter

//        TripTravelReportSubmitSharedFlow.resetPlanReportTripPlanReportSamples(emptyArray())

        TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportDoctor(
            TripSubmitReportSelectedTourPlanDoctor()
        )

        TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportProducts(emptyArray())
        TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportSamples(emptyArray())
        TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportCompanion(emptyArray())

        viewLifecycleOwner.lifecycleScope.launch (Dispatchers.Main){
            TripTravelReportSubmitSharedFlow.tripPlanReportDoctor.collect{
                activity?.runOnUiThread {
                  if(it != null && it.name != null){
                      doctorName.visibility = View.VISIBLE
                      doctorName.text = it.name.toString()
                  }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
           //viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            TripTravelReportSubmitSharedFlow.tripPlanReportCompanion.collect { updates ->
                activity?.runOnUiThread(Runnable {
                    val data3: Array<TripSubmitReportSelectedTourPlanCompanion> = updates
                    //data3.add(ArrayList(updates.doctors))
                    if (data3.size > 0) {
                        Log.d(TAG, "Received trip details: addListToView 332  ${updates.size}")
                        val selectedUpdates = data3.filter { it.isSelected == true }
                        recyclerViewCompanionAdapter.filterList(selectedUpdates)
                        setRecyclerViewHeightBasedOnChildren(recyclerViewCompanion)
                    } else {
                        recyclerViewCompanionAdapter.filterList(emptyList())
                        setRecyclerViewHeightBasedOnChildren(recyclerViewCompanion)

                    }
                })
            //}
            }
        }


        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            //viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            TripTravelReportSubmitSharedFlow.tripPlanReportProducts.collect { updates ->
                activity?.runOnUiThread(Runnable {
                    val data3: Array<ProductList> = updates
                    //data3.add(ArrayList(updates.doctors))
                    if (data3.size > 0) {
                        Log.d(TAG, "Received trip details: addListToView 332  ${updates.size}")
                        val selectedUpdates = data3.filter { it.isSelected == true }
                        recyclerViewProductsAdapter.filterList(selectedUpdates)
                        setRecyclerViewHeightBasedOnChildren(recyclerViewProducts)
                    } else {
                        recyclerViewProductsAdapter.filterList(emptyList())
                        setRecyclerViewHeightBasedOnChildren(recyclerViewProducts)
                    }
                })
                //}
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            //viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            TripTravelReportSubmitSharedFlow.tripPlanReportSamples.collect { updates ->
                activity?.runOnUiThread(Runnable {
                    val data3: Array<ProductListSamples> = updates
                    //data3.add(ArrayList(updates.doctors))
                    if (data3.size > 0 ) {
                        Log.d(TAG, "Received trip details: addListToView 332  ${updates.size}")
                        val selectedUpdates = data3.filter { it.isSelected == true }
                        recyclerViewSamplesAdapter.filterList(selectedUpdates)
                        setRecyclerViewHeightBasedOnChildren(recyclerViewSamples)
                    } else {
                        recyclerViewSamplesAdapter.filterList(emptyList())
                        setRecyclerViewHeightBasedOnChildren(recyclerViewSamples)
                    }
                })
                //}
            }
        }



        tripReportSubmitAddDoctor.setOnClickListener {
            val childDialog = TourPlanUpdateDetailsDoctor.newInstance("Doctor")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetailsDoctor")
        }

        binding.tripPlanSubmitDoctorDoctorNameCard.setOnClickListener {
            val childDialog = TourPlanUpdateDetailsDoctor.newInstance("Doctor")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetailsDoctor")
        }

        tripReportSubmitAddCmpanion.setOnClickListener {
            val childDialog = TourPlanUpdateDetailsCompanion.newInstance("Product")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetailsCompanion")
        }

        binding.tripPlanSubmitDoctorCompanionCard.setOnClickListener {
            val childDialog = TourPlanUpdateDetailsCompanion.newInstance("Product")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetailsCompanion")
        }

        tripReportSubmitAddProducts.setOnClickListener {
            val childDialog = TourPlanUpdateDetailsProduct.newInstance("Product")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetailsProduct")
        }

        binding.tripPlanSubmitDoctorProductCard.setOnClickListener {
            val childDialog = TourPlanUpdateDetailsProduct.newInstance("Product")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetailsProduct")
        }

        tripReportSubmitAddSamples.setOnClickListener {
            val childDialog = TourPlanUpdateDetailsSamples.newInstance("Samples")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetailsSamples")
        }

        binding.tripPlanSubmitDoctorSamplesCard.setOnClickListener {
            val childDialog = TourPlanUpdateDetailsSamples.newInstance("Samples")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetailsSamples")
        }

        tripReportAddDate.setOnClickListener{
            showDatePicker()
        }

        timePicker.hour = 10
        timePicker.minute = 0
        timePicker.setIs24HourView(false)// = false

        timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            timeSubmit = "T"+hourOfDay+":"+minute+":00.000Z"
        }

        tripSubmitData.setOnClickListener click@{


            TripTravelReportSubmitSharedFlow._tripPlanReportDoctor.value?.let{

                val originalList: Array<ProductListSamples> = recyclerViewSamplesAdapter.getItems().toTypedArray()
                val nonZeroQuantityList = originalList.ensureNonZeroQuantity()

                if(nonZeroQuantityList){
                    Toast.makeText(context,"Sample QTY is Zero", Toast.LENGTH_LONG).show()
                    return@click
                }


                if(TripTravelReportSubmitSharedFlow._tripPlanReportDoctor.value!!.id != null && TripTravelReportSubmitSharedFlow._tripPlanReportDoctor.value?.id!! > 0){
                    TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportSamples(recyclerViewSamplesAdapter.getItems().toTypedArray())
                    val date =  TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value.date
                    if (date != null) {
                        TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportDate(date.split("T")[0]+ timeSubmit)
                    }
                    Log.d(TAG, "onViewCreated: addListToView ${dateSubmit+timeSubmit} ")
                    Log.d(TAG, "onViewCreated: addListToView ${TripTravelReportSubmitSharedFlow._tripPlanReportDoctor.value}")

                    val tripProducts    = TripTravelReportSubmitSharedFlow._tripPlanReportProducts.value.filter{it.isSelected == true}.map { product -> ProductsTripReport(productId = product.id, name = product.name) }// it.id?.toInt() ?: 0 }
                    val tripSamples     =TripTravelReportSubmitSharedFlow._tripPlanReportSamples.value.filter { it.isSelected == true }.map { sample -> SamplesUpdate(productId = sample.id, quantity = sample.quantity, name = sample.name) }

                    val tripCompanions  =TripTravelReportSubmitSharedFlow._tripPlanReportCompanion.value.filter{it.isSelected == true}.map { companion -> SelectedCompanion(id = companion.companionId, name = companion.name) }
                    val tripTime  =TripTravelReportSubmitSharedFlow._tripPlanReportDate.value.toString()

                    var sampleQtyCheck: Boolean = false

                    TripTravelReportSubmitSharedFlow._tripPlanReportDoctor.value?.doctorId?.let { it1 ->
                        updateDoctorProducts(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value, it1,tripProducts)
                        updateDoctorCompanions(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value, it1,tripCompanions)
                        updateDoctorDate(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value, it1,tripTime.replace("$",""))
                        sampleQtyCheck =   updateDoctorSamples(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value, it1,tripSamples)
                    }

                    if(sampleQtyCheck == true){
                        TripTravelReportSubmitSharedFlow.updateAllData(Random.nextInt(1, 100))
                        dismiss()
                    } else {
                        Toast.makeText(context, "Enter the Samples QTY", Toast.LENGTH_LONG).show()
                    }

                }  else {
                    Toast.makeText(context, "Enter the details", Toast.LENGTH_LONG).show()
                }
            }  ?: run {
            Toast.makeText(context, "Enter the details", Toast.LENGTH_LONG).show()
        }

        }

    }

    fun Array<ProductListSamples>.ensureNonZeroQuantity(): Boolean {
        var changed = false
        for (i in this.indices) {
            if (this[i].quantity == null || this[i].quantity == 0) {
                this[i] = this[i].copy(quantity = 1)
                changed = true
            }
        }
        return changed
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
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                //val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                //tripReportAddDate!!.text = sdf.format(cal.getTime())
                tripReportAddDate.text = sdf.format(cal.getTime())
                dateSubmit = dateFormat.format(cal.getTime()).toString()

            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = cal.timeInMillis - 604800000
        dpd.datePicker.maxDate = cal.timeInMillis + 604800000
        dpd.show()

    }

    private fun showTimePickerDialog(onTimeSelected: (hourOfDay: Int, minute: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                onTimeSelected(hourOfDay, minute)
            },
            currentHour,
            currentMinute,
            true
        )

//        timePickerDialog.timePickerMode = TimePicker.MODE_CLOCK // Set clock mode
//
        timePickerDialog.show()
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


    fun updateDoctorProducts(tourPlan: TripSubmitReportSelectedTourPlan, doctorId: Int, newProductIds: List<ProductsTripReport>): Boolean {
        // Find the doctor by doctorId
        val doctor = tourPlan.doctors.find { it.doctorId == doctorId }
        Log.d(TAG, "updateDoctorProducts: initial addListToView ${tourPlan.doctors.toString()}")
        return if (doctor != null) {
            doctor.products.clear()
            doctor.products.addAll(newProductIds)
            Log.d(TAG, "updateDoctorProducts: final addListToView ${tourPlan.doctors.toString()}")
            true
        } else {
            // Return false if the doctor was not found
            false
        }
    }

    /*fun updateDoctorSamples(tourPlan: TripSubmitReportSelectedTourPlan, doctorId: Int, newSamplesIds: List<SamplesUpdate>): Boolean {
        // Find the doctor by doctorId
        val doctor = tourPlan.doctors.find { it.doctorId == doctorId }
        Log.d(TAG, "updateDoctorProducts: initial addListToView ${tourPlan.doctors.toString()}")
        return if (doctor != null) {
            doctor.samples.clear()
            doctor.samples.addAll(newSamplesIds)
            Log.d(TAG, "updateDoctorProducts: final addListToView ${tourPlan.doctors.toString()}")
            true
        } else {
            // Return false if the doctor was not found
            false
        }
    }*/

    fun updateDoctorSamples(tourPlan: TripSubmitReportSelectedTourPlan, doctorId: Int, newSamplesIds: List<SamplesUpdate>): Boolean {
        // Find the doctor by doctorId
        val doctor = tourPlan.doctors.find { it.doctorId == doctorId }
        Log.d(TAG, "updateDoctorSamples: initial samples ${tourPlan.doctors}")
        val samplesRef = TripTravelReportSubmitSharedFlow.tripReportProductListQtyCheck.value

        if (doctor != null) {
            doctor.samples.clear()
            for (sample in newSamplesIds) {
                val sampleRefernceQty = samplesRef.find { sample.productId == it.id }
                if(sampleRefernceQty?.quantity!! >= sample.quantity!! && (sample.quantity!! > 0)){
                    Log.d(TAG, "updateDoctorSamples..: ${sampleRefernceQty?.quantity!!}   ${sample.quantity!!}")
                    doctor.samples.add(sample)
                } else {
                    //Toast.makeText(context, "Required Quantity is more then Stock", Toast.LENGTH_LONG).show()
                    return false
                }
            }
            Log.d(TAG, "updateDoctorSamples: final samples ${tourPlan.doctors}")
            return true
        } else {
            // Return false if the doctor was not found
            return false
        }
    }

    fun updateDoctorCompanions(
        tourPlan: TripSubmitReportSelectedTourPlan,
        doctorId: Int,
        newCompanionIds: List<SelectedCompanion>
    ): Boolean {
        // Find the doctor by doctorId
        val doctor = tourPlan.doctors.find { it.doctorId == doctorId }
        Log.d(TAG, "updateDoctorProducts: initial addListToView ${tourPlan.doctors.toString()}")
        return if (doctor != null) {
            doctor.companionIds.clear()
            doctor.companionIds.addAll(newCompanionIds)
            Log.d(TAG, "updateDoctorProducts: final addListToView ${tourPlan.doctors.toString()}")
            true
        } else {
            // Return false if the doctor was not found
            false
        }
    }

    fun updateDoctorDate(
        tourPlan: TripSubmitReportSelectedTourPlan,
        doctorId: Int,
        newMeetingTime: String
    ): Boolean {
        // Find the doctor by doctorId
        val doctor = tourPlan.doctors.find { it.doctorId == doctorId }
        Log.d(TAG, "updateDoctorProducts: initial addListToView ${tourPlan.doctors.toString()}")
        return if (doctor != null) {
            doctor.timestamp = newMeetingTime // clear()
            Log.d(TAG, "updateDoctorProducts: final addListToView ${tourPlan.doctors.toString()}")
            true
        } else {
            // Return false if the doctor was not found
            false
        }
    }


    companion object {
        val TAG: String = TourVisitDoctorListInit::class.java.name

        fun newInstance(arg1: String): TourVisitDoctorListInit {
            val fragment = TourVisitDoctorListInit()
            val args = Bundle()
            args.putString("ARG1_KEY", arg1)
            fragment.arguments = args
            return fragment
        }
    }

}