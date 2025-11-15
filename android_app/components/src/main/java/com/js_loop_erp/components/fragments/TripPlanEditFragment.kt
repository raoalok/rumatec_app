package com.js_loop_erp.components.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import  com.js_loop_erp.components.MainActivity
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.TripPlanEditFragmentBinding
import  com.js_loop_erp.components.data_flow.TripPlanDataViewModel
import  com.js_loop_erp.components.data_flow.TripPlanSharedFlow
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.Locale

class TripPlanEditFragment: DialogFragment(), TripPlanEditItemClickListenerI {
    private var _binding: TripPlanEditFragmentBinding? = null

    private val binding get() = _binding!!

    var argEdit: String = " "
    var responBody: String = " "

    var products: Array<TripPlanEditViewModel> = emptyArray()

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchButton: SearchView
    private lateinit var adapter: TripPlanEditAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
        argEdit = arguments?.getString("ARG1_KEY").toString()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Set your animation style here
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TripPlanEditFragmentBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Trip Plan Select ${argEdit}")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.trip_plan_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchButton = view.findViewById(R.id.tripPlanActionSearch)

        val data = ArrayList<TripPlanEditViewModel>()

        getTripPlanListFromServer()
        adapter = TripPlanEditAdapter(this, data)
        recyclerView.adapter = adapter

        searchButton.setOnClickListener{
        }

        searchButton.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                filter(msg)
                return false
            }
        })
    }

    private fun getTripPlanListFromServer() {

        if (ReceiverMediator.USER_TOKEN.length > 8) {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                //.url("http://65.0.61.137/api/inventory/sample")
                //.url("http://65.0.61.137/api/expenses/agent")
                .url("http://65.0.61.137/api/users/ids")
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
                            //println(responBody)
                            addListToView(responBody)
                        }
                    }
                }
            })
        }
    }


    override fun onItemClick(position: Int) {
        // TODO("Not yet implemented")
        //deleteFromServer(position, 0)
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Select the Item?")
        //builder.setMessage("Device Location is Required.")
        builder.setPositiveButton("Ok") { _, _ ->
            Log.d(TAG, "onItemClick: ${products[position].name.toString()}")
            TripPlanSharedFlow.updateData(products[position].name.toString())
            //tripPlanEditViewModel.tripEditCompanion_.value = "Heeee"
            this.dismiss()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

    fun addListToView(expenseDataList: String) {
        try{
        activity?.runOnUiThread(Runnable {
            val data = ArrayList<TripPlanEditViewModel>()
            val gson = Gson()
            products = gson.fromJson(expenseDataList, Array<TripPlanEditViewModel>::class.java).toList().toTypedArray()
            for (product in products) {
                data.add(product)
            }
            val adapter = TripPlanEditAdapter(this, data)

            recyclerView.adapter = adapter
        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        try {
            val gson = Gson()
            val products = gson.fromJson(responBody, Array<TripPlanEditViewModel>::class.java).toList()

            if(products.size > 0){
                activity?.runOnUiThread(Runnable {

                    val filteredlist: ArrayList<TripPlanEditViewModel> = ArrayList()

                    for (product in products) {
                        if (product.description?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true) {
                            filteredlist.add(product)
                        }
                    }
                    if (filteredlist.isEmpty()) {

                    } else {
                        adapter.filterList(filteredlist)
                    }

                })
            }

        } catch (e:IOException){
            Log.d(TAG, "filter: ${e.toString()}")
        }

    }

    companion object {
        val TAG: String = TripPlanEditFragment::class.java.name
        
        fun newInstance(arg1: String): TripPlanEditFragment {
            val fragment = TripPlanEditFragment()
            val args = Bundle()
            args.putString("ARG1_KEY", arg1)
            fragment.arguments = args
            return fragment
        }
    }
}

interface TripPlanEditItemClickListenerI {
    fun onItemClick(position: Int)
}