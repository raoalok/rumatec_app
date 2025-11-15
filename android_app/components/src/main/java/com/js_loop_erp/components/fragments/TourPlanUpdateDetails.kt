package com.js_loop_erp.components.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.gson.Gson
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.TourPlanUpdateAdapter
import  com.js_loop_erp.components.data_class.TourPlanUpdateCompanion
import  com.js_loop_erp.components.data_flow.TripPlanSharedFlow
import  com.js_loop_erp.components.databinding.TourPlanUpdateDetailsBinding
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.Locale

class TourPlanUpdateDetails: DialogFragment(), TourPlanUpdateDetailsItemClickListenerI {
    private var _binding: TourPlanUpdateDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TourPlanUpdateAdapter


    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonSubmit: ExtendedFloatingActionButton
    private lateinit var searchButton: SearchView
    var responBody: String = ""
    var products: Array<TourPlanUpdateCompanion> = emptyArray<TourPlanUpdateCompanion>()

    var argEdit: String = " "



    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
        argEdit = arguments?.getString("ARG1_KEY").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TourPlanUpdateDetailsBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Tour Visit Update ${argEdit}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        buttonSubmit = view.findViewById(R.id.button_get_tour_plan_checked_items)
        searchButton= view.findViewById(R.id.tourUpdateActionSearch)



        recyclerView = view.findViewById(R.id.trip_plan_update_companion_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = ArrayList<TourPlanUpdateCompanion>()
        adapter = TourPlanUpdateAdapter(this, data)
        getDataFromServer()



        buttonSubmit.setOnClickListener {
            if(adapter.getCheckedItems().isNotEmpty()){

                val selectedUpdates = products.filter { it.isSelected == true }
                TripPlanSharedFlow.updateTripUpdateCompanion(selectedUpdates.toTypedArray())
                dismiss()
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

        if (ReceiverMediator.USER_TOKEN.length > 8) {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                //.url("http://65.0.61.137/api/inventory/sample")
                .url("http://65.0.61.137/api/products/list/")
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
    }

    fun addListToView(itemDataList: String){
        try{
        activity?.runOnUiThread(Runnable{
            val data = ArrayList<TourPlanUpdateCompanion>()
            val gson = Gson()
            products = gson.fromJson(itemDataList, Array<TourPlanUpdateCompanion>::class.java).toList().toTypedArray()
            for(product in products) {
                data.add(product)
            }

            adapter = TourPlanUpdateAdapter(this, data)

            recyclerView.adapter = adapter

        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    override fun onItemSelected(item: TourPlanUpdateCompanion, isChecked: Boolean) {


        val index = products.indexOf(item)
        if (index != -1) {
            val updatedItem = item.copy(isSelected = isChecked)
            products[index] = updatedItem
        } else {

        }
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.

        var data = ArrayList<TourPlanUpdateCompanion>()
        val gson = Gson()
        activity?.runOnUiThread(Runnable {

            val filteredlist: ArrayList<TourPlanUpdateCompanion> = ArrayList()

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
        val TAG: String = TourPlanUpdateDetails::class.java.name

        fun newInstance(arg1: String): TourPlanUpdateDetails {
            val fragment = TourPlanUpdateDetails()
            val args = Bundle()
            args.putString("ARG1_KEY", arg1)
            fragment.arguments = args
            return fragment
        }
    }
}
interface TourPlanUpdateDetailsItemClickListenerI {
    fun onItemSelected(item: TourPlanUpdateCompanion, isChecked: Boolean)
}