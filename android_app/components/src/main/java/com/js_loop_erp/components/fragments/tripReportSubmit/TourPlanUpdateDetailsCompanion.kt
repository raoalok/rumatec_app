package com.js_loop_erp.components.fragments.tripReportSubmit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.gson.Gson
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.TourPlanUpdateDetailsCompanionAdapter
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanCompanion
import  com.js_loop_erp.components.data_flow.TripTravelReportSubmitSharedFlow
import  com.js_loop_erp.components.databinding.TourPlanUpdateDetailsCompanionBinding
import  com.js_loop_erp.components.fragments.InventoryFragment
import kotlinx.coroutines.launch
import java.util.Locale

class TourPlanUpdateDetailsCompanion : DialogFragment(),TourPlanUpdateDetailsCompanionItemClickListenerI {

    private var _binding: TourPlanUpdateDetailsCompanionBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TourPlanUpdateDetailsCompanionAdapter


    private lateinit var recyclerView: RecyclerView
    //    private lateinit var buttonSubmit: AppCompatButton
    private lateinit var buttonSubmit: ExtendedFloatingActionButton
    private lateinit var searchButton: SearchView
    var responBody: String = ""
    var products: Array<TripSubmitReportSelectedTourPlanCompanion> = emptyArray<TripSubmitReportSelectedTourPlanCompanion>()

    var argEdit: String = " "

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogGreyTheme)
        argEdit = arguments?.getString("ARG1_KEY").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TourPlanUpdateDetailsCompanionBinding.inflate(inflater, container, false)
//        getDialog()?.setTitle("Tour Visit Update ${argEdit}")
        getDialog()?.setTitle("Tour Visit Doctor Add Companions")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        buttonSubmit = view.findViewById(R.id.button_get_tour_plan_checked_doctor_items)
        searchButton= view.findViewById(R.id.tourUpdateActionSearchDoctor)



        recyclerView = view.findViewById(R.id.trip_plan_update_doctor_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = ArrayList<TripSubmitReportSelectedTourPlanCompanion>()
        adapter = TourPlanUpdateDetailsCompanionAdapter(this, data)
        recyclerView.adapter = adapter
        //getDataFromServer()

        lifecycleScope.launch {
            TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.collect { updates ->
                activity?.runOnUiThread(Runnable {
                    //Log.d(TAG, "Received trip details: addListToView 332  ${updates.toString()}")
                    val data3: ArrayList<TripSubmitReportSelectedTourPlanCompanion> = ArrayList(updates.companions)
                    //data3.add(ArrayList(updates.doctors))
                    if(data3.size>0){
                        products = data3.toTypedArray()
                        adapter.filterList(data3)
                    }
                    //recyclerView.adapter = adapter
                    //Log.d(TAG, "onViewCreated: addListToView11 ${data3[0].toString()}")
                    //Log.d(TAG, "Received trip details: addListToView 554  ${data3[0].toString()}")
                })
            }
        }

        buttonSubmit.setOnClickListener {
            //adapter.onItemClick
            if(adapter.getCheckedItems().isNotEmpty()){
                //Log.d(TAG, "onViewCreated: addListToView 111 ${products.size}")
                val selectedUpdates = adapter.getCheckedItems().filter { it.isSelected == true }
                TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportCompanion(selectedUpdates.toTypedArray())
                dismiss()
            } else {
                TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportCompanion(emptyArray())
            }
        }

        searchButton.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                Log.d(InventoryFragment.TAG, "onQueryTextChange: this this this ")
                filter(msg)
                return false
            }
        })

    }

    fun addListToView(itemDataList: String){
        try{
            activity?.runOnUiThread(Runnable{
                val data = ArrayList<TripSubmitReportSelectedTourPlanCompanion>()
                val gson = Gson()
                products = gson.fromJson(itemDataList, Array<TripSubmitReportSelectedTourPlanCompanion>::class.java).toList().toTypedArray()
                for(product in products) {
                    data.add(product)
                }

                adapter = TourPlanUpdateDetailsCompanionAdapter(this, data)

                recyclerView.adapter = adapter

            })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    override fun onItemSelected(item: TripSubmitReportSelectedTourPlanCompanion, isChecked: Boolean) {
        Log.d("TAG", "addListToView__: ${item.name}  :: ${isChecked}")
        val index = products.indexOf(item)
        if (index != -1) {
            val updatedItem = item.copy(isSelected = isChecked)
            products[index] = updatedItem
        } else {
        }
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.

        var data = ArrayList<TripSubmitReportSelectedTourPlanCompanion>()
        val gson = Gson()
        //val products = gson.fromJson(responBody, Array<TourPlanUpdate>::class.java).toList()

        activity?.runOnUiThread(Runnable {

            val filteredlist: ArrayList<TripSubmitReportSelectedTourPlanCompanion> = ArrayList()

            for (product in products) {
                if (product.name?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true) {
                    filteredlist.add(product)
                }
            }
            if (filteredlist.isEmpty()) {

            } else {
                adapter.filterList(filteredlist)
                Log.d(TAG, "filter:${filteredlist.size} ")
            }

        })
    }
    companion object {
        val TAG: String = TourPlanUpdateDetailsCompanion::class.java.name

        fun newInstance(arg1: String): TourPlanUpdateDetailsCompanion {
            val fragment = TourPlanUpdateDetailsCompanion()
            val args = Bundle()
            args.putString("ARG1_KEY", arg1)
            fragment.arguments = args
            return fragment
        }
    }
}
interface TourPlanUpdateDetailsCompanionItemClickListenerI {
    fun onItemSelected(item: TripSubmitReportSelectedTourPlanCompanion, isChecked: Boolean)
}