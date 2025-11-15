package com.js_loop_erp.components.fragments.tripTourPlan

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.view.marginBottom
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.TripAddFormChemistAdapter
import  com.js_loop_erp.components.adapter.TripAddFormCompanionAdapter
import  com.js_loop_erp.components.adapter.TripAddFormDoctorAdapter
import  com.js_loop_erp.components.adapter.TripAddFormHospitalAdapter
import  com.js_loop_erp.components.adapter.TripAddFormInstituteAdapter
import  com.js_loop_erp.components.adapter.TripAddFormStockistAdapter
import  com.js_loop_erp.components.data_class.TripTourPlanChemistSelection
import  com.js_loop_erp.components.data_class.TripTourPlanDoctorSelection
import  com.js_loop_erp.components.data_class.TripTourPlanHospitalSelection
import  com.js_loop_erp.components.data_class.TripTourPlanStockistSelection
import  com.js_loop_erp.components.databinding.TripPlanFormBinding
import  com.js_loop_erp.components.data_flow.TripPlanFormSharedFlow
import  com.js_loop_erp.components.data_class.TripTourPlanCompanionSelection
import  com.js_loop_erp.components.data_class.TripTourPlanInstituteSelection
import  com.js_loop_erp.components.data_class.TripTourPlanMeetingDetailsSelection
import com.js_loop_erp.components.fragments.LoginFragment
import  com.js_loop_erp.components.fragments.TripPlanFragment
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
import java.util.Calendar
import java.util.Locale
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.random.Random

class TripPlanFormFragmnet: DialogFragment() {

    private var _binding: TripPlanFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var tripPlanCompanion: AppCompatImageView
    private lateinit var tripPlanMeetingDetails: AppCompatImageView
    private lateinit var tripPlanMeetingDetailsCard: CardView
    private lateinit var tripPlanDoctor: AppCompatImageView
    private lateinit var tripPlanHospital: AppCompatImageView
    private lateinit var tripPlanChemist: AppCompatImageView
    private lateinit var tripPlanInstitute: AppCompatImageView
    private lateinit var tripPlanStockist: AppCompatImageView

    private lateinit var tripPlanSubmit: Button
    private lateinit var tripReportAddDate             : TextView
    private val cal = Calendar.getInstance()
    private var dateSubmit  : String? = null
    private var timeSubmit  : String = "T10:00:00.000Z"

    private lateinit var tripPlanCompanionText: TextView

    private lateinit var recyclerViewCompanion: RecyclerView
    private lateinit var recyclerViewDoctor: RecyclerView
    private lateinit var recyclerViewHospital: RecyclerView
    private lateinit var recyclerViewChemist: RecyclerView
    private lateinit var recyclerViewInstitute: RecyclerView
    private lateinit var recyclerViewStockist: RecyclerView

    private lateinit var adapterCompanionList: TripAddFormCompanionAdapter
    private lateinit var adapterDoctorList: TripAddFormDoctorAdapter
    private lateinit var adapterHospitalList: TripAddFormHospitalAdapter
    private lateinit var adapterChemistList: TripAddFormChemistAdapter
    private lateinit var adapterInstituteList: TripAddFormInstituteAdapter
    private lateinit var adapterStockistList: TripAddFormStockistAdapter

    private lateinit var adapterCompanionListData: List< TripTourPlanCompanionSelection>
    private lateinit var adapterDoctorListData: List< TripTourPlanDoctorSelection>
    private lateinit var adapterHospitalListData:List< TripTourPlanHospitalSelection>
    private lateinit var adapterChemistListData:List< TripTourPlanChemistSelection>
    private lateinit var adapterInstituteListData:List< TripTourPlanInstituteSelection>
    private lateinit var adapterStockistListData: List< TripTourPlanStockistSelection>
    private lateinit var adapterMeetngDetailsData:List< TripTourPlanMeetingDetailsSelection>

    private lateinit var categoryTripPlan: TextView
    private lateinit var startPlaceTripPlan: TextView
    private lateinit var endPlaceTripPlan: TextView
    private lateinit var travelModeTripPlan: TextView

    var argEdit1:String= ""
    var argEdit2:String= ""
    var argEdit3:Int= 0

    var responBody: String = ""

    override fun  onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ChildDialogGreyTheme)
        argEdit1 = arguments?.getString("ARG1_KEY").toString()
        argEdit2 = arguments?.getString("ARG2_KEY").toString()
        argEdit3 = arguments?.getString("ARG3_KEY")?.toIntOrNull() ?: 0
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

    override fun onCreateView(inflater:LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding =  TripPlanFormBinding.inflate(inflater,container, false)
        getDialog()?.setTitle("Trip Plan Form")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view,savedInstanceState)

        getDialog()?.getWindow()?.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getDialog()?.getWindow()?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        val window = dialog?.window

//        window?.decorView?.let { decorView ->
//            val insetsController = ViewCompat.getWindowInsetsController(decorView)
//            insetsController?.apply {
//                show(WindowInsetsCompat.Type.systemBars())
//                // systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
//                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//
//            }
//        }

        tripPlanCompanion       = view.findViewById<AppCompatImageView>(R.id.trip_plan_edit_companion)
        tripPlanMeetingDetails  = view.findViewById<AppCompatImageView>(R.id.trip_plan_edit_meeting_details)
        tripPlanMeetingDetailsCard = view.findViewById<CardView>(R.id.trip_plan_edit_meeting_details_card)
        tripPlanDoctor          = view.findViewById<AppCompatImageView>(R.id.trip_plan_edit_doctor)
        tripPlanHospital        = view.findViewById<AppCompatImageView>(R.id.trip_plan_edit_hospital)
        tripPlanChemist         = view.findViewById<AppCompatImageView>(R.id.trip_plan_edit_chemist)
        tripPlanInstitute         = view.findViewById<AppCompatImageView>(R.id.trip_plan_edit_institute)
        tripPlanStockist        = view.findViewById<AppCompatImageView>(R.id.trip_plan_edit_stockist)

        tripPlanSubmit          = view.findViewById<Button>(R.id.submit_trip_plan_plan)
        tripReportAddDate               =   view.findViewById<TextView>(R.id.trip_report_add_date)


        recyclerViewCompanion = view.findViewById<RecyclerView>(R.id.trip_plan_form_companion_list)
        recyclerViewDoctor = view.findViewById<RecyclerView>(R.id.trip_plan_form_doctor_list)
        recyclerViewHospital = view.findViewById<RecyclerView>(R.id.trip_plan_form_hospital_list)
        recyclerViewChemist = view.findViewById<RecyclerView>(R.id.trip_plan_form_chemist_list)
        recyclerViewInstitute = view.findViewById<RecyclerView>(R.id.trip_plan_form_institute_list)
        recyclerViewStockist = view.findViewById<RecyclerView>(R.id.trip_plan_form_stockist_list)

        recyclerViewCompanion.layoutManager = LinearLayoutManager(context)
        recyclerViewDoctor.layoutManager = LinearLayoutManager(context)
        recyclerViewHospital.layoutManager = LinearLayoutManager(context)
        recyclerViewChemist.layoutManager = LinearLayoutManager(context)
        recyclerViewInstitute.layoutManager = LinearLayoutManager(context)
        recyclerViewStockist.layoutManager = LinearLayoutManager(context)


        categoryTripPlan   = view.findViewById(R.id.category_metting_details)
        startPlaceTripPlan = view.findViewById(R.id.metting_start_place)
        endPlaceTripPlan   = view.findViewById(R.id.metting_end_place)
        travelModeTripPlan = view.findViewById(R.id.meeting_travel_mode)

        recyclerViewCompanion.isNestedScrollingEnabled = true
        recyclerViewDoctor.isNestedScrollingEnabled = true
        recyclerViewHospital.isNestedScrollingEnabled = true
        recyclerViewChemist.isNestedScrollingEnabled = true
        recyclerViewInstitute.isNestedScrollingEnabled = true
        recyclerViewStockist.isNestedScrollingEnabled = true

        adapterCompanionListData = listOf()
        adapterDoctorListData = listOf()
        adapterHospitalListData = listOf()
        adapterChemistListData = listOf()
        adapterInstituteListData = listOf()
        adapterStockistListData = listOf()
        //adapterMeetingDetailsData = listOf()

        val dataCompanionAddForm = ArrayList<TripTourPlanCompanionSelection>()
        TripPlanFormSharedFlow.updateTripPlanFormCompanion(dataCompanionAddForm.toTypedArray())
        adapterCompanionList = TripAddFormCompanionAdapter(dataCompanionAddForm)
        recyclerViewCompanion.adapter = adapterCompanionList

        val dataDoctorAdapterAddForm = ArrayList<TripTourPlanDoctorSelection>()
        TripPlanFormSharedFlow.updateTripPlanFormDoctor(dataDoctorAdapterAddForm.toTypedArray())
        adapterDoctorList = TripAddFormDoctorAdapter(dataDoctorAdapterAddForm)
        recyclerViewDoctor.adapter = adapterDoctorList

        val dataHospitalUpdateAddForm = ArrayList<TripTourPlanHospitalSelection>()
        TripPlanFormSharedFlow.updateTripPlanFormHospital(dataHospitalUpdateAddForm.toTypedArray())
        adapterHospitalList = TripAddFormHospitalAdapter(dataHospitalUpdateAddForm)
        recyclerViewHospital.adapter = adapterHospitalList

        val dataChemistUpdateAddForm = ArrayList<TripTourPlanChemistSelection>()
        TripPlanFormSharedFlow.updateTripPlanFormChemist(dataChemistUpdateAddForm.toTypedArray())
        adapterChemistList = TripAddFormChemistAdapter(dataChemistUpdateAddForm)
        recyclerViewChemist.adapter = adapterChemistList

        val dataInstituteUpdateAddForm = ArrayList<TripTourPlanInstituteSelection>()
        TripPlanFormSharedFlow.updateTripPlanFormInstitute(dataInstituteUpdateAddForm.toTypedArray())
        adapterInstituteList = TripAddFormInstituteAdapter(dataInstituteUpdateAddForm)
        recyclerViewInstitute.adapter = adapterInstituteList

        val dataStockistUpdateAddForm = ArrayList<TripTourPlanStockistSelection>()
        TripPlanFormSharedFlow.updateTripPlanFormStockist(dataStockistUpdateAddForm.toTypedArray())
        adapterStockistList = TripAddFormStockistAdapter(dataStockistUpdateAddForm)
        recyclerViewStockist.adapter = adapterStockistList

        val dataMeetingDetailsUpdateAddForm = ArrayList<TripTourPlanMeetingDetailsSelection>()
        TripPlanFormSharedFlow.updateTripPlanFormMeetingDetails(dataMeetingDetailsUpdateAddForm.toTypedArray())




        lifecycleScope.launch {
            TripPlanFormSharedFlow.tripPlanFormCompanion.collect { updates ->

                if (updates.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {
                        adapterCompanionList.filterList(updates.toList())
                        adapterCompanionListData = updates.toList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewCompanion)
                    })
                } else {
                    activity?.runOnUiThread(Runnable {
                        adapterCompanionList.filterList(emptyList())
                        adapterCompanionListData = emptyList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewCompanion)
                    })
                }
            }
        }

        lifecycleScope.launch {
            TripPlanFormSharedFlow.tripPlanFormDoctor.collect { updates ->

                if (updates.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {
                        adapterDoctorList.filterList(updates.toList())
                        adapterDoctorListData = updates.toList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewDoctor)
                    })
                } else {
                    activity?.runOnUiThread(Runnable {
                        adapterDoctorList.filterList(emptyList())
                        adapterDoctorListData = emptyList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewDoctor)
                    })
                }
            }
        }

        lifecycleScope.launch {
            TripPlanFormSharedFlow.tripPlanFormHospital.collect { updates ->

                if (updates.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {
                        adapterHospitalList.filterList(updates.toList())
                        adapterHospitalListData = updates.toList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewHospital)
                    })
                } else {
                    activity?.runOnUiThread(Runnable {
                        adapterHospitalList.filterList(emptyList())
                        adapterHospitalListData = emptyList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewHospital)
                    })
                }
            }
        }

        lifecycleScope.launch {
            TripPlanFormSharedFlow.tripPlanFormChemist.collect { updates ->

                if (updates.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {
                        adapterChemistList.filterList(updates.toList())
                        adapterChemistListData = updates.toList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewChemist)
                    })
                } else {
                    activity?.runOnUiThread(Runnable {
                        adapterChemistList.filterList(emptyList())
                        adapterChemistListData = emptyList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewChemist)
                    })
                }
            }
        }

        lifecycleScope.launch {
            TripPlanFormSharedFlow.tripPlanFormInstitute.collect { updates ->

                if (updates.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {
                        adapterInstituteList.filterList(updates.toList())
                        adapterInstituteListData = updates.toList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewInstitute)
                    })
                } else {
                    activity?.runOnUiThread(Runnable {
                        adapterInstituteList.filterList(emptyList())
                        adapterInstituteListData = emptyList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewInstitute)
                    })
                }
            }
        }

        lifecycleScope.launch {
            TripPlanFormSharedFlow.tripPlanFormStockist.collect { updates ->

                if (updates.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {
                        adapterStockistList.filterList(updates.toList())
                        adapterStockistListData = updates.toList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewStockist)
                    })
                } else {
                    activity?.runOnUiThread(Runnable {
                        adapterStockistList.filterList(emptyList())
                        adapterStockistListData = emptyList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewStockist)
                    })
                }
            }
        }

        lifecycleScope.launch {
            TripPlanFormSharedFlow.tripPlanFormMeetingDetails.collect { updates ->

                if (updates.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {

                        adapterMeetngDetailsData = updates.toList()

                        val route = "<b>Route:</b> ${updates[0].category}"
                        val startPlace = "<b>Start Place:</b> ${updates[0].startPlace}"
                        val endPlace = "<b>End Place:</b>  ${updates[0].endPlace}."
                        val travelMode = "<b>Travel Mode:</b> ${updates[0].travelMode}"

                        categoryTripPlan.text =   Html.fromHtml(route, Html.FROM_HTML_MODE_LEGACY)
                        startPlaceTripPlan.text = Html.fromHtml(startPlace, Html.FROM_HTML_MODE_LEGACY)
                        endPlaceTripPlan.text  =  Html.fromHtml(endPlace, Html.FROM_HTML_MODE_LEGACY)
                        travelModeTripPlan.text = Html.fromHtml(travelMode, Html.FROM_HTML_MODE_LEGACY)
                    })
                }
            }
        }


        tripPlanCompanion.setOnClickListener {
            /*val childDialog = TourPlanUpdateDetails.newInstance("Companio00n")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetails")*/
            if(ROUTE_ID > 0){
                val fragment = TripTourPlanCompanionSelectionFragment.newInstance("Companion")
                fragment.show(childFragmentManager, "TripTourPlanCompanionSelectionFragment")
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tripPlanEditCompanionCard.setOnClickListener {
            if(ROUTE_ID > 0){
                val fragment = TripTourPlanCompanionSelectionFragment.newInstance("Companion")
                fragment.show(childFragmentManager, "TripTourPlanCompanionSelectionFragment")
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
                }
            }
        }

        tripPlanMeetingDetails.setOnClickListener {
            /*val fragment = TripTourPlanCompanionSelectionFragment.newInstance("Compaccnionnn")
            fragment.show(childFragmentManager, "TripTourPlanCompanionSelectionFragment")*/
            val fragment = TripPlanFormMeetingDetailsFragment()
            fragment.show(childFragmentManager,"TripPlanFormMeetingDetailsFragment")
        }

        tripPlanMeetingDetailsCard.setOnClickListener {
            val fragment = TripPlanFormMeetingDetailsFragment()
            fragment.show(childFragmentManager,"TripPlanFormMeetingDetailsFragment")
        }

        tripPlanDoctor.setOnClickListener {
           /* val fragment = TripPlanEditFragment.newInstance("Doctor")
            fragment.show(childFragmentManager, "TripPlanEditFragment")*/
            if(ROUTE_ID > 0){
                val fragment = TripTourPlanDoctorSelectionFragment.newInstance("Doctor")
                fragment.show(childFragmentManager, "TripPlanEditFragment")
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tripPlanEditDoctorCard.setOnClickListener {
            if(ROUTE_ID > 0){
                val fragment = TripTourPlanDoctorSelectionFragment.newInstance("Doctor")
                fragment.show(childFragmentManager, "TripPlanEditFragment")
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
                }
            }
        }

        tripPlanHospital.setOnClickListener {
/*            val fragment = TripPlanEditFragment.newInstance("Hospital")
            fragment.show(childFragmentManager, "TripPlanEditFragment")*/

            if(ROUTE_ID > 0){
                val fragment = TripTourPlanHospitalSelectionFragment.newInstance("Hospital")
                fragment.show(childFragmentManager, "TripPlanEditFragment")
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tripPlanEditHospitalCard.setOnClickListener {
            if(ROUTE_ID > 0){
                val fragment = TripTourPlanHospitalSelectionFragment.newInstance("Hospital")
                fragment.show(childFragmentManager, "TripPlanEditFragment")
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
                }
            }
        }

        tripPlanChemist.setOnClickListener {
/*            val fragment = TripPlanEditFragment.newInstance("Chemist")
            fragment.show(childFragmentManager, "TripPlanEditFragment")*/
            if(ROUTE_ID > 0){
            val fragment = TripTourPlanChemistSelectionFragment.newInstance("Pet Shop")
            fragment.show(childFragmentManager, "TripPlanEditFragment")
             } else {
                 activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
               }
            }
        }

        binding.tripPlanEditChemistCard.setOnClickListener {
            if(ROUTE_ID > 0){
                val fragment = TripTourPlanChemistSelectionFragment.newInstance("Pet Shop")
                fragment.show(childFragmentManager, "TripPlanEditFragment")
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
                }
            }
        }

        tripPlanInstitute.setOnClickListener {
/*            val fragment = TripPlanEditFragment.newInstance("Chemist")
            fragment.show(childFragmentManager, "TripPlanEditFragment")*/
            if(ROUTE_ID > 0){
                val fragment = TripTourPlanInstituteSelectionFragment.newInstance("Institute")
                fragment.show(childFragmentManager, "TripPlanEditFragment")
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tripPlanEditInstituteCard.setOnClickListener {
            if(ROUTE_ID > 0){
                val fragment = TripTourPlanInstituteSelectionFragment.newInstance("Institute")
                fragment.show(childFragmentManager, "TripPlanEditFragment")
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
                }
            }
        }

        tripPlanStockist.setOnClickListener {
/*            val fragment = TripPlanEditFragment.newInstance("Stockist")
            fragment.show(childFragmentManager, "TripPlanEditFragment")*/
            if(ROUTE_ID > 0){
                val fragment = TripTourPlanStockistSelectionFragment.newInstance("Stockist")
                fragment.show(childFragmentManager, "TripPlanEditFragment")
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tripPlanEditStockistCard.setOnClickListener {
            if(ROUTE_ID > 0){
                val fragment = TripTourPlanStockistSelectionFragment.newInstance("Stockist")
                fragment.show(childFragmentManager, "TripPlanEditFragment")
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Select Route in Travel Meeting Details", Toast.LENGTH_LONG).show()
                }
            }
        }

        tripPlanSubmit.setOnClickListener {
            if(dateSubmit != null){
                subnitAndCloseDialog()
            } else {
                Toast.makeText(context,"Enter the Date", Toast.LENGTH_LONG).show()
            }
            Log.d(TAG, "subnitAndCloseDialog:")

        }

        tripReportAddDate.setOnClickListener{
            showDatePicker()
        }

 /*       lifecycleScope.launch {
                TripPlanSharedFlow.dataFlow.collect { data ->
                    Log.d("TAG", " tripEditCompanion Updated value: $data")
                    tripPlanCompanionText.text = data.toString()
                }
        }*/

        if(argEdit3 > 0){
            getTourDetailsFromServer()
        }

        view.rootView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback {
            dismiss()
        }

    }

    fun subnitAndCloseDialog(){
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Submit Trip Plan?")
        builder.setPositiveButton("Ok") { _, _ ->
            Log.d(TAG, "subnitAndCloseDialog: ${TripPlanFormSharedFlow._tripPlanFormMeetingDetails.value.toString()}")
            if(argEdit3 > 0){
                updateTourPlanToCloud()
            } else {
                pushTourPlanToCloud()
            }

            //this.dismiss()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
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
                dateSubmit += timeSubmit

            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = cal.timeInMillis - 604800000
        dpd.datePicker.maxDate = cal.timeInMillis + 604800000
        dpd.show()

    }

    fun pushTourPlanToCloud() {
        if (ROUTE_ID > 0) {
            if (ReceiverMediator.USER_TOKEN.length > 8) {
                try {

                    //val fromAreaIdList: List<TripTourPlanMeetingDetailsSelection> = TripPlanFormSharedFlow._tripPlanFormMeetingDetails.toList()

                    //val fromAreaIdList: List<Int?> = TripPlanFormSharedFlow._tripPlanFormMeetingDetails.map { it. }

                    val adapterStockistListId: List<Int> =
                        adapterStockistListData.map { it.id ?: null!! }

                    val adapterCompanionListDataId: List<Int> =
                        adapterCompanionListData.map { it.id ?: null!! }
                    val adapterDoctorListDataId: List<Int> =
                        adapterDoctorListData.map { it.id ?: null!! }
                    val adapterHospitalListDataId: List<Int> =
                        adapterHospitalListData.map { it.id ?: null!! }
                    val adapterChemistListDataId: List<Int> =
                        adapterChemistListData.map { it.id ?: null!! }
                    val adapterInstituteListDataId: List<Int> =
                        adapterInstituteListData.map { it.id ?: null!! }
                    //val adapterMeetngDetailsDataId:List<Int> = adapterMeetngDetailsData.map { it.id ?: null!!}

                    val route = TripFormPlanSubmitToServer(
                        routeId = ROUTE_ID,
                        fromAreaId = adapterMeetngDetailsData[0].startPlaceId,
                        toAreaId = adapterMeetngDetailsData[0].endPlaceId,
                        mode = adapterMeetngDetailsData[0].travelMode,
                        date = dateSubmit, //argEdit2.toString(),
                        companionIds = adapterCompanionListDataId,
                        doctorIds = adapterDoctorListDataId,
                        partyIds = adapterStockistListId,
                        hospitalIds = adapterHospitalListDataId,
                        petshopIds = adapterChemistListDataId,
                        instituteIds = adapterInstituteListDataId,
                        remark = binding.remarkTripPlanFragment.text.toString()
                    )

                    val gson = Gson()
                    val jsonString = gson.toJson(route)
                    Log.d(TAG, "pushTourPlanToCloud: ${jsonString}")


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


                    val mediaType = "application/json".toMediaType()
                    val body = jsonString.toRequestBody(mediaType)

                    val request = Request.Builder()
                        //.url("http://65.0.61.137/api/inventory/sample")
                        //.url("http://65.0.61.137/api/expenses/agent")
                        //.url("http://65.0.61.137/api/users/ids")
                        .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours")
                        .post(body)
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
                                    Log.d(TAG, "onResponse: error ${response.toString()}")
                                } else {
                                    Log.d(
                                        TAG,
                                        "onResponse: success pushTourPlanToCloud ${response.toString()}"
                                    )
                                    TripPlanFormSharedFlow.updateRefreshTripPlanFormSubmit(Random.nextInt(1,100))
                                    dismiss()
                                    //responBody = response.body?.string() ?: ""
                                    //println(responBody)
                                    //addListToView(responBody)
                                }
                            }
                        }
                    })
                } catch (e: Exception) {
                    Log.d(TAG, "pushTourPlanToCloud: ${e.toString()}")

                }
            }
        }
    }

    fun updateTourPlanToCloud(){
        if(ROUTE_ID > 0) {
            if (ReceiverMediator.USER_TOKEN.length > 8) {
                try {

                val adapterStockistListId: List<Int> = adapterStockistListData.map { it.id ?: null!! }

                val adapterCompanionListDataId: List<Int> = adapterCompanionListData.map { it.id ?: null!!}
                val adapterDoctorListDataId: List<Int> =  adapterDoctorListData.map { it.id ?: null!!}
                val adapterHospitalListDataId:List< Int> = adapterHospitalListData.map { it.id ?: null!!}
                val adapterChemistListDataId:List< Int> = adapterChemistListData.map { it.id ?: null!!}
                val adapterInstituteListDataId:List<Int> = adapterInstituteListData.map { it.id ?: null!!}
                //val adapterMeetngDetailsDataId:List<Int> = adapterMeetngDetailsData.map { it.id ?: null!!}

                val route = TripFormPlanSubmitToServer(
                    routeId = ROUTE_ID,
                    fromAreaId = adapterMeetngDetailsData[0].startPlaceId,
                    toAreaId = adapterMeetngDetailsData[0].endPlaceId,
                    mode = adapterMeetngDetailsData[0].travelMode,
                    date = argEdit2.toString(),
                    companionIds = adapterCompanionListDataId,
                    doctorIds =  adapterDoctorListDataId,
                    partyIds = adapterStockistListId,
                    hospitalIds = adapterHospitalListDataId,
                    petshopIds = adapterChemistListDataId,
                    instituteIds = adapterInstituteListDataId,
                    remark = binding.remarkTripPlanFragment.text.toString()
                )

                val gson = Gson()
                val jsonString = gson.toJson(route)
                Log.d(TAG, "pushTourPlanToCloud: ${jsonString}")


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

                val mediaType = "application/json".toMediaType()
                val body = jsonString.toRequestBody(mediaType)

                val request = Request.Builder()
                    //.url("http://65.0.61.137/api/inventory/sample")
                    //.url("http://65.0.61.137/api/expenses/agent")
                    //.url("http://65.0.61.137/api/users/ids")
                    //.url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/${argEdit3}")
                    .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/${argEdit3}")
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
                                Log.d(TAG, "onResponse: error ${response.toString()}")
                            } else {
                                Log.d(TAG, "onResponse: success")
                                TripPlanFormSharedFlow.updateRefreshTripPlanFormSubmit(Random.nextInt(1,100))
                                dismiss()
                                //responBody = response.body?.string() ?: ""
                                //println(responBody)
                                //addListToView(responBody)
                            }
                        }
                    }
                })
                } catch (e: Exception) {
                    Log.d(TAG, "pushTourPlanToCloud: ${e.toString()}")
                }
            }
        }
    }

    fun getTourDetailsFromServer() {
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
                //.url(" http://65.0.61.137/api/application/tours/list")
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/view/${argEdit3}")
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
                            Log.d(TripPlanDetailsViewFragment.TAG, "onResponse: error")

                        } else {
                            responBody= response.body?.string() ?: ""
                            Log.d(TripPlanDetailsViewFragment.TAG, "onResponse: ${responBody}")
                            println(responBody)
                            addDetailsToView()
                        }
                    }
                }
            })
        }
    }
    fun addDetailsToView() {

        try {
            activity?.runOnUiThread(Runnable {
                val trip: TourPlanEditData = Gson().fromJson(responBody, TourPlanEditData::class.java)

                val companionList = mutableListOf<String>()
                val doctorsList = mutableListOf<String>()
                val partiesList = mutableListOf<String>()
                val hospitalList = mutableListOf<String>()
                val petShopsList = mutableListOf<String>()
                val institutesList = mutableListOf<String>()


                trip.companions?.forEachIndexed { index, companion ->
                    println("TripPlanDetailsViewFragment Companion ${index + 1}: ${companion.name}")
                    adapterCompanionListData.map { companion}
                }


                adapterCompanionListData = trip.companions?.toList() ?: emptyList()
                adapterDoctorListData = trip.doctors?.toList() ?: emptyList()
                adapterHospitalListData = trip.hospitals?.toList() ?: emptyList()
                adapterChemistListData = trip.petshops?.toList() ?: emptyList()
                adapterInstituteListData = trip.institutes?.toList() ?: emptyList()
                adapterStockistListData = trip.parties?.toList()?: emptyList()

//                adapterCompanionList.filterList(adapterCompanionListData)
//                setRecyclerViewHeightBasedOnChildren(recyclerViewCompanion)

                TripPlanFormSharedFlow.updateTripPlanFormCompanion(adapterCompanionListData.toTypedArray())

//                adapterDoctorList.filterList(adapterDoctorListData)
//                setRecyclerViewHeightBasedOnChildren(recyclerViewDoctor)

                TripPlanFormSharedFlow.updateTripPlanFormDoctor(adapterDoctorListData.toTypedArray())

//                adapterHospitalList.filterList(adapterHospitalListData)
//                setRecyclerViewHeightBasedOnChildren(recyclerViewHospital)
                TripPlanFormSharedFlow.updateTripPlanFormHospital(adapterHospitalListData.toTypedArray())

//                adapterChemistList.filterList(adapterChemistListData)
//                setRecyclerViewHeightBasedOnChildren(recyclerViewChemist)

                TripPlanFormSharedFlow.updateTripPlanFormChemist(adapterChemistListData.toTypedArray())

//                adapterInstituteList.filterList(adapterInstituteListData)
//                setRecyclerViewHeightBasedOnChildren(recyclerViewInstitute)

                TripPlanFormSharedFlow.updateTripPlanFormInstitute(adapterInstituteListData.toTypedArray())

//                adapterStockistList.filterList(adapterStockistListData)
//                setRecyclerViewHeightBasedOnChildren(recyclerViewStockist)

                TripPlanFormSharedFlow.updateTripPlanFormStockist(adapterStockistListData.toTypedArray())

                adapterMeetngDetailsData = listOf(
                    TripTourPlanMeetingDetailsSelection(
                        startPlace = trip.fromArea,
                        endPlace = trip.toArea,
                        travelMode = trip.mode,
                        category = trip.route,
                        startPlaceId = trip.fromAreaId,
                        endPlaceId = trip.toAreaId,
                        travelModeId = 0,
                        categoryId = trip.routeId
                    )
                )

                TripPlanFormSharedFlow.updateTripPlanFormMeetingDetails(adapterMeetngDetailsData.toTypedArray())

                ROUTE_ID = trip.routeId?.toInt() ?: 0

//
//                val route = "<b>Route:</b> ${adapterMeetngDetailsData[0].category}"
//                val startPlace = "<b>Start Place:</b> ${adapterMeetngDetailsData[0].startPlace}"
//                val endPlace = "<b>End Place:</b>  ${adapterMeetngDetailsData[0].endPlace}."
//                val travelMode = "<b>Travel Mode:</b> ${adapterMeetngDetailsData[0].travelMode}"
//
//                categoryTripPlan.text =   Html.fromHtml(route, Html.FROM_HTML_MODE_LEGACY)
//                startPlaceTripPlan.text = Html.fromHtml(startPlace, Html.FROM_HTML_MODE_LEGACY)
//                endPlaceTripPlan.text  =  Html.fromHtml(endPlace, Html.FROM_HTML_MODE_LEGACY)
//                travelModeTripPlan.text = Html.fromHtml(travelMode, Html.FROM_HTML_MODE_LEGACY)


            })
        } catch (e: Exception) {
            Log.d(TripPlanFragment.TAG, "filter: $e")
        }

    }

    companion object {
        val TAG: String = this.javaClass.simpleName

        var ROUTE_ID: Int = 0
        var TRIP_REMARK: String = " "

        fun newInstance(arg1: String, arg2: String, arg3: String): TripPlanFormFragmnet {
            val fragment = TripPlanFormFragmnet()
            val args = Bundle()
            args.putString("ARG1_KEY",arg1)
            args.putString("ARG2_KEY",arg2)
            args.putString("ARG3_KEY",arg3)
            fragment.arguments = args
            return fragment
        }
    }

}


data class TripFormPlanSubmitToServer(
    val routeId: Int? = null,
    val fromAreaId: Int? = null,
    val toAreaId: Int? = null,
    val mode: String? = null,
    val date: String? = null,
    val remark: String? = null,
    val companionIds: List<Int>? = null,
    val doctorIds: List<Int>? = null,
    val partyIds: List<Int>? = null,
    val hospitalIds: List<Int>? = null,
    val petshopIds: List<Int>? = null,
    val instituteIds: List<Int>? = null
)


data class TourPlanEditData(
    val id: Int? = 0,
    val routeId: Int? = 0,
    val fromAreaId: Int? = 0,
    val toAreaId: Int? = 0,
    val mode: String? = "",
    val date: String? = "",
    val remark: String? = "",
    val createdAt: String? = "",
    val createdBy: String? = "",
    val updatedAt: String? = "",
    val updatedBy: String? = "",
    val deletedAt: String? = "",
    val deletedBy: String? = "",
    val approveAt: String? = "",
    val approveBy: String? = "",
    val approveRemark: String? = "",
    val rejectAt: String? = "",
    val rejectBy: String? = "",
    val rejectRemark: String? = "",
    val route: String? = "",
    val fromArea: String? = "",
    val toArea: String? = "",
    val companions: List<TripTourPlanCompanionSelection>? = null,
    val doctors: List<TripTourPlanDoctorSelection>? = null,
    val parties: List<TripTourPlanStockistSelection>? = null,
    val hospitals: List<TripTourPlanHospitalSelection>? = null,
    val petshops: List<TripTourPlanChemistSelection>? = null,
    val institutes: List<TripTourPlanInstituteSelection>? = null
)