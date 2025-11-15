package com.js_loop_erp.components.fragments.tripReportSubmit

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.marginBottom
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.MainActivity
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.TripUpdateChemistAdapter
import  com.js_loop_erp.components.adapter.TripUpdateCompanionAdapter
import  com.js_loop_erp.components.adapter.TripUpdateDoctorAdapter
import  com.js_loop_erp.components.adapter.TripUpdateHospitalAdapter
import  com.js_loop_erp.components.adapter.TripUpdateStockistAdapter
import  com.js_loop_erp.components.data_class.ProductsTripReport
import  com.js_loop_erp.components.data_class.SelectedCompanion
import  com.js_loop_erp.components.data_class.TripReportApprovedModel
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlan
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanDoctor
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanHospital
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanInstitute
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanParty
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanPetshop
import  com.js_loop_erp.components.data_flow.TripTravelReportSubmitSharedFlow
import  com.js_loop_erp.components.data_flow.TripTravelReportSubmitViewModel
import  com.js_loop_erp.components.databinding.TripApprovedPlanDetailsLayoutBinding
import com.js_loop_erp.components.fragments.LoginFragment
import  com.js_loop_erp.components.fragments.tripTourPlan.TripPlanDetailsViewFragment
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
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
import kotlin.random.Random


class TripApprovedPlanDetails: DialogFragment() {

    private var _binding:TripApprovedPlanDetailsLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var tripPlanButton: FloatingActionButton

    private lateinit var tripEditParty:     ExtendedFloatingActionButton
    private lateinit var tripEditDoctor:        ExtendedFloatingActionButton
    private lateinit var tripEditPetShop:       ExtendedFloatingActionButton
    private lateinit var tripEditHospital:      ExtendedFloatingActionButton
    private lateinit var tripEditInstitute:      ExtendedFloatingActionButton

    private lateinit var updateTourPlanBootomSheet: LinearLayout

    private lateinit var recyclerViewInstitute: RecyclerView
    private lateinit var recyclerViewDoctor:    RecyclerView
    private lateinit var recyclerViewHospital:  RecyclerView
    private lateinit var recyclerViewPetShop:   RecyclerView
    private lateinit var recyclerViewParties:  RecyclerView

    private lateinit var adapterInstituteList: TripUpdateCompanionAdapter
    private lateinit var adapterDoctorList:    TripUpdateDoctorAdapter
    private lateinit var adapterHospitalList:  TripUpdateHospitalAdapter
    private lateinit var adapterPetShopList:   TripUpdateChemistAdapter
    private lateinit var adapterPartyList:  TripUpdateStockistAdapter

    private lateinit var tripVisitDate      : TextView
    private lateinit var tripStartPlace     : TextView
    private lateinit var tripEndPlace       : TextView
    private lateinit var tripTravelMode     : TextView

    private lateinit var tripDetailsButton  : AppCompatImageView

    private lateinit var submitButton: Button

    var fabVisible: Boolean = false

    var items: Array<TripSubmitReportSelectedTourPlan> = emptyArray<TripSubmitReportSelectedTourPlan>()
    var responBody: String = ""
    val tripTravelReportSubmitViewModel: TripTravelReportSubmitViewModel by activityViewModels()
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = TripApprovedPlanDetailsLayoutBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Update Tour Plan")
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tripPlanButton = view.findViewById<FloatingActionButton>(R.id.trip_approved_plan_edit_menu_button)

        submitButton    = view.findViewById<Button>(R.id.submit_update_trip_plan_plan)
        tripDetailsButton = view.findViewById<AppCompatImageView>(R.id.trip_plan_details)

        tripEditParty = view.findViewById<ExtendedFloatingActionButton>(R.id.trip_approved_plan_edit_party)
        tripEditDoctor = view.findViewById<ExtendedFloatingActionButton>(R.id.trip_approved_plan_edit_doctor)
        tripEditPetShop = view.findViewById<ExtendedFloatingActionButton>(R.id.trip_approved_plan_edit_pet_shop)
        tripEditHospital = view.findViewById<ExtendedFloatingActionButton>(R.id.trip_approved_plan_edit_hospital)
        tripEditInstitute = view.findViewById<ExtendedFloatingActionButton>(R.id.trip_approved_plan_edit_institute)

        recyclerViewInstitute = view.findViewById<RecyclerView>(R.id.update_trip_plan_update_companion_recycler_view)
        recyclerViewDoctor = view.findViewById<RecyclerView>(R.id.update_trip_plan_update_doctor_recycler_view)
        recyclerViewHospital = view.findViewById<RecyclerView>(R.id.update_trip_plan_update_hospital_recycler_view)
        recyclerViewPetShop = view.findViewById<RecyclerView>(R.id.update_trip_plan_update_chemist_recycler_view)
        recyclerViewParties = view.findViewById<RecyclerView>(R.id.update_trip_plan_update_stockist_recycler_view)

        tripVisitDate   = view.findViewById<TextView>(R.id.trip_visit_date)
        tripStartPlace  = view.findViewById<TextView>(R.id.trip_start_place)
        tripEndPlace    = view.findViewById<TextView>(R.id.trip_end_place)
        tripTravelMode  = view.findViewById<TextView>(R.id.trip_travel_mode)

        recyclerViewInstitute.layoutManager = LinearLayoutManager(context)
        recyclerViewDoctor.layoutManager = LinearLayoutManager(context)
        recyclerViewHospital.layoutManager = LinearLayoutManager(context)
        recyclerViewPetShop.layoutManager = LinearLayoutManager(context)
        recyclerViewParties.layoutManager = LinearLayoutManager(context)

        val dataCompanionUpdate = ArrayList<TripSubmitReportSelectedTourPlanInstitute>()
        adapterInstituteList = TripUpdateCompanionAdapter(dataCompanionUpdate)
        recyclerViewInstitute.adapter = adapterInstituteList

        val dataDoctorAdapter = ArrayList<TripSubmitReportSelectedTourPlanDoctor>()
        adapterDoctorList = TripUpdateDoctorAdapter(dataDoctorAdapter)
        recyclerViewDoctor.adapter = adapterDoctorList

        val dataHospitalUpdate = ArrayList<TripSubmitReportSelectedTourPlanHospital>()
        adapterHospitalList = TripUpdateHospitalAdapter(dataHospitalUpdate)
        recyclerViewHospital.adapter = adapterHospitalList

        val dataPetShopUpdate = ArrayList<TripSubmitReportSelectedTourPlanPetshop>()
        adapterPetShopList = TripUpdateChemistAdapter(dataPetShopUpdate)
        recyclerViewPetShop.adapter = adapterPetShopList

        val dataStockistUpdate = ArrayList<TripSubmitReportSelectedTourPlanParty>()
        adapterPartyList = TripUpdateStockistAdapter(dataStockistUpdate)
        recyclerViewParties.adapter = adapterPartyList

        updateTourPlanBootomSheet = view.findViewById(R.id.update_tour_plan_bottom_sheet_drag)
        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(updateTourPlanBootomSheet)

        //bottomSheetBehavior.isDraggable = false

        getTourPlanDetailsFromServer()

        /*lifecycleScope.launch {
            TripPlanSharedFlow.tripUpdateCompanion.collect { updates ->

                    if (updates.isNotEmpty()) {
                        activity?.runOnUiThread(Runnable {
                            adapterInstituteList.filterList(updates.toList())
                        })
                }
            }
        }*/

        lifecycleScope.launch {
            TripTravelReportSubmitSharedFlow.updateData.collect { updates ->
                Log.d(TAG, "onViewCreated: .....")
                val data= TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value
                if (data.doctors.size > 0) {
                    activity?.runOnUiThread(Runnable {
                        adapterDoctorList.filterList(data.doctors)
                        setRecyclerViewHeightBasedOnChildren(recyclerViewDoctor)
                    })
                }

                if (data.institutes.size > 0) {
                    activity?.runOnUiThread(Runnable {
                        adapterInstituteList.filterList(data.institutes)
                        setRecyclerViewHeightBasedOnChildren(recyclerViewInstitute)
                    })
                }

                if (data.parties.size > 0) {
                    activity?.runOnUiThread(Runnable {
                        adapterPartyList.filterList(data.parties)
                        setRecyclerViewHeightBasedOnChildren(recyclerViewParties)
                    })
                }

                if (data.petshops.size > 0) {
                    activity?.runOnUiThread(Runnable {
                        adapterPetShopList.filterList(data.petshops)
                        setRecyclerViewHeightBasedOnChildren(recyclerViewPetShop)
                    })
                }

                if (data.hospitals.size > 0) {
                    activity?.runOnUiThread(Runnable {
                        adapterHospitalList.filterList(data.hospitals)
                        setRecyclerViewHeightBasedOnChildren(recyclerViewHospital)
                    })
                }
                
                
            }
        }

/*        lifecycleScope.launch {

            TripPlanSharedFlow.tripUpdateChemist.collect { updates ->

                if (updates.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {
                        adapterPetShopList.filterList(updates.toList())
                    })
                }
            }
        }*/


/*        lifecycleScope.launch {

            TripPlanSharedFlow.tripUpdateHospital.collect { updates ->

                if (updates.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {
                        adapterHospitalList.filterList(updates.toList())
                    })
                }
            }
        }*/

/*        lifecycleScope.launch {
            TripPlanSharedFlow.tripUpdateStockist.collect { updates ->

                if (updates.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {
                        adapterInstituteList.filterList(updates.toList())
                    })
                }
            }
        }*/

        tripPlanButton.setOnClickListener {
            if (fabVisible) {
                showHideFabButtons(false)
            } else {
                showHideFabButtons(true)
            }
        }

        tripEditParty.setOnClickListener {
            /*val childDialog = TourPlanUpdateDetails.newInstance("Companion")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetails")*/
            //bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            if(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value.parties.size > 0){
                val childDialog = TourVisitPartyListInit.newInstance("Party")
                childDialog.show(childFragmentManager, "TourVisitPartyListInit")
                showHideFabButtons(false)
            } else {
                Toast.makeText(context, "No Party in this Trip", Toast.LENGTH_LONG).show()
            }
        }

        tripEditDoctor.setOnClickListener {
            /*val childDialog = TourPlanUpdateDetailsDoctor.newInstance("Doctor")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetails")*/

            //TripTravelReportSubmitSharedFlow.updateCurrentTripTravelRoutePlanDetails(items[0])

            if(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value.doctors.size > 0){
                val childDialog = TourVisitDoctorListInit.newInstance("Doctor")
                childDialog.show(childFragmentManager, "TourVisitDoctorListInit")

                //bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                showHideFabButtons(false)
            } else {
                Toast.makeText(context, "No Doctors in this Trip", Toast.LENGTH_LONG).show()
            }


        }

        tripEditPetShop.setOnClickListener {
            /*val childDialog = TourPlanUpdateDetailsChemist.newInstance("Chemist")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetails")*/
            if(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value.petshops.size > 0){
                val childDialog = TourVisitPetShopListInit.newInstance("PetShop")
                childDialog.show(childFragmentManager, "TourVisitPetShopListInit")
                showHideFabButtons(false)
            } else {
                Toast.makeText(context, "No PetShop in this Trip", Toast.LENGTH_LONG).show()
            }
        }

        tripEditHospital.setOnClickListener {
            /*val childDialog = TourPlanUpdateDetailsHospital.newInstance("Hospital")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetails")*/
            if(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value.hospitals.size > 0){
                val childDialog = TourVisitHospitalListInit.newInstance("Hospital")
                childDialog.show(childFragmentManager, "TourVisitHospitalListInit")
                showHideFabButtons(false)
            } else {
                Toast.makeText(context, "No Hospital in this Trip", Toast.LENGTH_LONG).show()
            }
        }

        tripEditInstitute.setOnClickListener {
            /*val childDialog = TourPlanUpdateDetailsStockist.newInstance("Stockist")
            childDialog.show(childFragmentManager, "TourPlanUpdateDetails")*/
            if(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value.institutes.size > 0){
                val childDialog = TourVisitInstitutesListInit.newInstance("Institute")
                childDialog.show(childFragmentManager, "TourVisitInstitutesListInit")
                showHideFabButtons(false)
            } else {
                Toast.makeText(context, "No Institute in this Trip", Toast.LENGTH_LONG).show()
            }
        }

        submitButton.setOnClickListener{
            Log.d(TAG, "onViewCreated: ..... ${TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value}")
            val gson = Gson()
            val json = gson.toJson(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value)
            Log.d(TAG, "onViewCreated: ${json}")

            val builder = android.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Submit Trip Details?")
            builder.setPositiveButton("Ok") { _, _ ->
                submitTripDetails(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value)
            }
            builder.setNegativeButton("Cancel") { _, _ ->
            }
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

            /*activity?.runOnUiThread(Runnable {
                adapterDoctorList.filterList(TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.value.doctors)
                setRecyclerViewHeightBasedOnChildren(recyclerViewDoctor)
                
            })*/
        }

        tripDetailsButton.setOnClickListener{
            val tripDetails: TripReportApprovedModel = TripTravelReportSubmitSharedFlow.tripTravelReportSubmitSharedFlow.value

            val fragment = TripPlanDetailsViewFragment.newInstance(tripDetails.id.toString())
            fragment.show(childFragmentManager, "TripPlanDetailsViewFragment")
        }

        parentFragmentManager.setFragmentResultListener(TAG, viewLifecycleOwner) { _, bundle ->
            val confirmed = bundle.getInt(TAG)
            activity?.runOnUiThread {
                Toast.makeText(context,"Submitted Trip Details...", Toast.LENGTH_LONG).show()
                dialog!!.dismiss()
                dismiss()
            }
        }

    }

    private fun submitTripDetails(data: TripSubmitReportSelectedTourPlan){
        val tripDetails: TripReportApprovedModel = TripTravelReportSubmitSharedFlow.tripTravelReportSubmitSharedFlow.value
        if (ReceiverMediator.USER_TOKEN.length > 8 && tripDetails.id != null) {


            val builder = AlertDialog.Builder(context)
            val inflater: LayoutInflater = layoutInflater
            val dialogView: View = inflater.inflate(R.layout.alert_dialog_style, null)

   /*       val dialogTitle: TextView = dialogView.findViewById(R.id.dialogTitle)
            val dialogMessage: TextView = dialogView.findViewById(R.id.dialogMessage)
            val positiveButton: Button = dialogView.findViewById(R.id.positiveButton)
            val negativeButton: Button = dialogView.findViewById(R.id.negativeButton)*/

            builder.setView(dialogView)
            builder.setCancelable(false)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog = builder.create()
            dialog?.show()


            val gson = Gson()
            val json = gson.toJson(data)

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
            val body = json.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/report/${tripDetails.id}")
                .method("PUT", body)
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
                            Log.d(TAG, "onResponse: ${response.body?.string()} ")
                            Log.d(TAG, "onResponse: ${json}")
                            activity?.runOnUiThread {
                                Toast.makeText(context,"Error Submitting Trip Details", Toast.LENGTH_LONG).show()
                                dialog?.dismiss()
                            }
                        } else {
                            responBody = response.body?.string() ?: ""
                            //println("addListToView" + responBody)
                            //addListToView(responBody)
                           // activity?.runOnUiThread {
                                //Toast.makeText(context,"Submitted Trip Details", Toast.LENGTH_LONG).show()
                                //binding.submitUpdateTripPlanPlan.visibility = View.INVISIBLE
                                //dialog?.dismiss()
                                var bundle: Bundle = Bundle()
                                bundle.putInt(TAG,123)
                                setFragmentResult(TAG,bundle)
                              //  dialog!!.dismiss()
                            //}
                        }
                    }
                }
            })
        }
    }

    private fun showHideFabButtons(show: Boolean) {
        if (show) {
            fabVisible = true
            tripPlanButton.setImageResource(R.drawable.baseline_close_24)
            tripEditDoctor.visibility = View.VISIBLE
            tripEditPetShop.visibility = View.VISIBLE
            tripEditHospital.visibility = View.VISIBLE
            tripEditInstitute.visibility = View.VISIBLE
            tripEditParty.visibility = View.VISIBLE
        } else {
            fabVisible = false
            tripPlanButton.setImageResource(R.drawable.baseline_edit_24)
            tripEditDoctor.visibility = View.GONE
            tripEditPetShop.visibility = View.GONE
            tripEditHospital.visibility = View.GONE
            tripEditInstitute.visibility = View.GONE
            tripEditParty.visibility = View.GONE

        }
    }

/*
    val tripReport = TripReportApprovedModel()

    // Add a companion
    tripReport.companions.add(Companion(name = "New Companion"))

    // Add a doctor
    tripReport.doctors.add(Doctor(id = 1, tourId = 1, instituteId = 1, name = "Dr. Smith"))

*/

    fun getTourPlanDetailsFromServer(){
        val tripDetails: TripReportApprovedModel = TripTravelReportSubmitSharedFlow.tripTravelReportSubmitSharedFlow.value

        if (ReceiverMediator.USER_TOKEN.length > 8 && tripDetails.id != null) {
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
                //.url("http://65.0.61.137/api/samples")
                //.url("http://65.0.61.137/api/application/tours/view/${tripDetails.id}")
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/report/${tripDetails.id}")
                //.url("http://65.0.61.137/api/application/tours/report/${1}")
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
                            //println("addListToView" + responBody)
                            addListToView(responBody)
                        }
                    }
                }
            })
        }
        else {
            Toast.makeText(context, "Trip Id is not valid", Toast.LENGTH_LONG).show()
        }
    }

    fun addListToView(itemDataList: String){
        try{
            val data = TripSubmitReportSelectedTourPlan()
            val gson = Gson()

            items = arrayOf(gson.fromJson(itemDataList,TripSubmitReportSelectedTourPlan::class.java))

            //Log.d(TAG, "addListToView 1: ${items[0].institutes.toString()}")

            //tripTravelReportSubmitViewModel.updateCurrentTripTravelRoutePlanDetails(items[0])
            TripTravelReportSubmitSharedFlow.updateCurrentTripTravelRoutePlanDetails(items[0])

            TripTravelReportSubmitSharedFlow.updateAllData(Random.nextInt(1, 100))


            activity?.runOnUiThread(Runnable {
                tripVisitDate.text = "Date: ${items[0].date.toString()}"
                tripStartPlace.text = "Start Place: ${items[0].fromArea.toString()}"
                tripEndPlace.text = "End Place: ${items[0].toArea.toString()}"
                tripTravelMode.text = "Mode: ${items[0].mode.toString()}"
            })

        } catch (e: Exception){
           Log.d(TourPlanUpdateDetailsDoctor.TAG, "filter: $e")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate( com.js_loop_erp.components.R.menu.bottom_nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun updateDoctorCompanions(
        tourPlan: TripSubmitReportSelectedTourPlan,
        doctorId: Int,
        newCompanionIds: List<SelectedCompanion>
    ): Boolean {
        // Find the doctor by doctorId
        val doctor = tourPlan.doctors.find { it.doctorId == doctorId }

        // If the doctor is found, update the companionIds and return true
        return if (doctor != null) {
            doctor.companionIds.clear()
            doctor.companionIds.addAll(newCompanionIds)
            true
        } else {
            // Return false if the doctor was not found
            false
        }
    }


    fun updateDoctorProducts(
        tourPlan: TripSubmitReportSelectedTourPlan,
        doctorId: Int,
        newProductIds: List<ProductsTripReport>
    ): Boolean {
        // Find the doctor by doctorId
        val doctor = tourPlan.doctors.find { it.doctorId == doctorId }

        return if (doctor != null) {
            doctor.products.clear()
            doctor.products.addAll(newProductIds)
            true
        } else {
            // Return false if the doctor was not found
            false
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

    companion object {
        val TAG: String = TripApprovedPlanDetails::class.java.name
    }
}
