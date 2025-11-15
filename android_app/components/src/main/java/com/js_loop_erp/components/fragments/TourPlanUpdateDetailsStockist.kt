package com.js_loop_erp.components.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.TourPlanUpdateStockistAdapter
import  com.js_loop_erp.components.data_class.TripReportApprovedModel
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanInstitute
import  com.js_loop_erp.components.data_flow.TripTravelReportSubmitSharedFlow
import  com.js_loop_erp.components.databinding.TourPlanUpdateStockistDetailsBinding
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
class TourPlanUpdateDetailsStockist: DialogFragment(), TourPlanUpdateStockistDetailsItemClickListenerI {

        private var _binding: TourPlanUpdateStockistDetailsBinding? = null
        private val binding get() = _binding!!
        private lateinit var adapter: TourPlanUpdateStockistAdapter


        private lateinit var recyclerView: RecyclerView
//    private lateinit var buttonSubmit: AppCompatButton
        private lateinit var buttonSubmit: ExtendedFloatingActionButton
        private lateinit var searchButton: SearchView
        var responBody: String = ""
        var products: Array<TripSubmitReportSelectedTourPlanInstitute> = emptyArray<TripSubmitReportSelectedTourPlanInstitute>()
        var argEdit: String = " "

        override fun onCreate(savedInstanceState: Bundle?){
            super.onCreate(savedInstanceState)
            setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogGreyTheme)
            argEdit = arguments?.getString("ARG1_KEY").toString()
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            _binding = TourPlanUpdateStockistDetailsBinding.inflate(inflater, container, false)
//        getDialog()?.setTitle("Tour Visit Update ${argEdit}")
            getDialog()?.setTitle("Tour Visit Update Party..")
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?){
            super.onViewCreated(view, savedInstanceState)

            buttonSubmit = view.findViewById(R.id.button_get_tour_plan_checked_doctor_items)
            searchButton= view.findViewById(R.id.tourUpdateActionSearchDoctor)

            recyclerView = view.findViewById(R.id.trip_plan_update_doctor_recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(context)

            val dialogContext = requireContext()
            val data: ArrayList<TripSubmitReportSelectedTourPlanInstitute> = ArrayList()
            adapter = TourPlanUpdateStockistAdapter(this, data)
            recyclerView.adapter = adapter

            lifecycleScope.launch {
                TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.collect { updates ->
                    activity?.runOnUiThread(Runnable {
                        //Log.d(TAG, "Received trip details: addListToView 332  ${updates.toString()}")
                        val data3: ArrayList<TripSubmitReportSelectedTourPlanInstitute> = ArrayList(updates.institutes)
                        if (data3.size > 0) {
                            adapter.filterList(data3)
                        }
                    })
                }
            }

            buttonSubmit.setOnClickListener {
                if(adapter.getSelected() != null){
                    TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportInstitute(adapter.getSelected()!!)
                    dismiss()
                } else {
                    Toast.makeText(context,"Select Doctor", Toast.LENGTH_LONG).show()
                }
            }

            searchButton.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener,
                android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(msg: String): Boolean {
                    Log.d(InventoryFragment.TAG, "onQueryTextChange: this this this ")
                    return false
                }
            })

        }

        fun getDataFromServer(){

            val tripDetails: TripReportApprovedModel = TripTravelReportSubmitSharedFlow.tripTravelReportSubmitSharedFlow.value

            if (ReceiverMediator.USER_TOKEN.length > 8 && tripDetails.id != null) {
                val client = OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    //.url("http://65.0.61.137/api/inventory/sample")
                    //.url("http://65.0.61.137/api/samples")
                    .url("http://65.0.61.137/api/application/tours/view/${tripDetails.id}")
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
                                // println(responBody)
                                //addListToView(responBody)
                            }
                        }
                    }
                })
            }
            else {
                Toast.makeText(context, "Trip Id is not valid", Toast.LENGTH_LONG).show()
            }
        }

    override fun onItemSelected(item: TripSubmitReportSelectedTourPlanInstitute, isChecked: Boolean) {
            Toast.makeText(context, "Source ${item.name}", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "onItemSelected: addListToView ${item.name} ")
        }

    companion object {
            val TAG: String = TourPlanUpdateDetailsStockist::class.java.name

            fun newInstance(arg1: String): TourPlanUpdateDetailsStockist {
                val fragment = TourPlanUpdateDetailsStockist()
                val args = Bundle()
                args.putString("ARG1_KEY", arg1)
                fragment.arguments = args
                return fragment
            }
        }
    }
    interface TourPlanUpdateStockistDetailsItemClickListenerI {
        fun onItemSelected(item: TripSubmitReportSelectedTourPlanInstitute, isChecked: Boolean)
    }