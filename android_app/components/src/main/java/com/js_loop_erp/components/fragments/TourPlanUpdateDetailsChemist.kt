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
import  com.js_loop_erp.components.adapter.TourPlanUpdateChemistAdapter
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanPetshop
import  com.js_loop_erp.components.data_class.TripReportApprovedModel
import  com.js_loop_erp.components.data_flow.TripTravelReportSubmitSharedFlow
import  com.js_loop_erp.components.databinding.TourPlanUpdateDoctorDetailsBinding
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class TourPlanUpdateDetailsChemist : DialogFragment(), TourPlanUpdateChemistDetailsItemClickListenerI {

    private var _binding: TourPlanUpdateDoctorDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TourPlanUpdateChemistAdapter


    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonSubmit: ExtendedFloatingActionButton
    private lateinit var searchButton: SearchView
    var responBody: String = ""
    var products: Array<TripSubmitReportSelectedTourPlanPetshop> = emptyArray<TripSubmitReportSelectedTourPlanPetshop>()

    var argEdit: String = " "

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogGreyTheme)
        argEdit = arguments?.getString("ARG1_KEY").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TourPlanUpdateDoctorDetailsBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Tour Visit Update Pet Shop..")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        buttonSubmit = view.findViewById(R.id.button_get_tour_plan_checked_doctor_items)
        searchButton= view.findViewById(R.id.tourUpdateActionSearchDoctor)

        recyclerView = view.findViewById(R.id.trip_plan_update_doctor_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val dialogContext = requireContext()
        val data: ArrayList<TripSubmitReportSelectedTourPlanPetshop> = ArrayList()
        adapter = TourPlanUpdateChemistAdapter(this, data)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.collect { updates ->
                activity?.runOnUiThread(Runnable {
                    //Log.d(TAG, "Received trip details: addListToView 332  ${updates.toString()}")
                    val data3: ArrayList<TripSubmitReportSelectedTourPlanPetshop> = ArrayList(updates.petshops)
                    if (data3.size > 0) {
                        adapter.filterList(data3)
                    }
                })
            }
        }

        buttonSubmit.setOnClickListener {

            if(adapter.getSelected() != null){
                TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportPetShop(adapter.getSelected()!!)
                dismiss()
            } else {
                Toast.makeText(context,"Select Pet Shop", Toast.LENGTH_LONG).show()
            }
        }

        searchButton.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                Log.d(InventoryFragment.TAG, "onQueryTextChange: this this this ")
                filter(msg)
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
        /*        try{
                activity?.runOnUiThread(Runnable{
                    val data = ArrayList<TripSubmitReportSelectedTourPlan>()
                    val gson = Gson()
                    products = gson.fromJson(itemDataList, Array<TripSubmitReportSelectedTourPlan>::class.java).toList().toTypedArray()
                    for(product in products) {
                        data.add(product)
                    }

                    adapter = TourPlanUpdateDoctorAdapter(this, data)

                    recyclerView.adapter = adapter

                })
                } catch (e: Exception){
                    Log.d(TAG, "filter: $e")
                }*/
    }

    fun onItemClick(position: Int) {
        //Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(item: TripSubmitReportSelectedTourPlanPetshop, isChecked: Boolean) {
        Toast.makeText(context, "Source ${item.name}", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onItemSelected: addListToView ${item.name} ")
        /*        val index = products.indexOf(item)
                if (index != -1) {
                    val updatedItem = item.copy(isSelected = isChecked)
                    products[index] = updatedItem
                } else {
                    // Handle item not found
                    //println("Item not found in the list")
                }*/
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.

//        var data = ArrayList<TripSubmitReportSelectedTourPlan>()
//        val gson = Gson()
//        //val products = gson.fromJson(responBody, Array<TourPlanUpdate>::class.java).toList()
//
//        activity?.runOnUiThread(Runnable {
//
//            val filteredlist: ArrayList<TripSubmitReportSelectedTourPlan> = ArrayList()
//
//            for (product in products) {
//                if (product.product?.toLowerCase()?.contains(text.toLowerCase()) == true) {
//                    filteredlist.add(product)
//                }
//            }
//            if (filteredlist.isEmpty()) {
//
//            } else {
//                adapter.filterList(filteredlist)
//                Log.d(TAG, "filter:${filteredlist.size} ")
//            }
//
//        })
    }
    companion object {
        val TAG: String = TourPlanUpdateDetailsChemist::class.java.name

        fun newInstance(arg1: String): TourPlanUpdateDetailsChemist {
            val fragment = TourPlanUpdateDetailsChemist()
            val args = Bundle()
            args.putString("ARG1_KEY", arg1)
            fragment.arguments = args
            return fragment
        }
    }
}
interface TourPlanUpdateChemistDetailsItemClickListenerI {
    fun onItemSelected(item: TripSubmitReportSelectedTourPlanPetshop, isChecked: Boolean)
}