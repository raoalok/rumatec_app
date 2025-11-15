package com.js_loop_erp.components.fragments.access_controlled

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.gson.Gson
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.CheckUserLoginStatusAdapter
import  com.js_loop_erp.components.databinding.CheckUserLoginStatusBinding
import  com.js_loop_erp.components.fragments.InventoryFragment
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.Locale

class CheckUserLoginStatus: DialogFragment(), CheckUserLoginStatusI {

    private var _binding: CheckUserLoginStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CheckUserLoginStatusAdapter


    private lateinit var recyclerView: RecyclerView
    //    private lateinit var buttonSubmit: AppCompatButton
    private lateinit var buttonSubmit: ExtendedFloatingActionButton
    private lateinit var searchButton: SearchView
    var responBody: String = ""
    var products: Array<CheckUserLoginStatusData> = emptyArray<CheckUserLoginStatusData>()

    var argEdit: String = " "

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogGreyTheme)
        argEdit = arguments?.getString("ARG1_KEY").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CheckUserLoginStatusBinding.inflate(inflater, container, false)
//        getDialog()?.setTitle("Tour Visit Update ${argEdit}")
        getDialog()?.setTitle("Login Status")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        buttonSubmit = view.findViewById(R.id.button_get_tour_approve_checked_doctor_items)
        searchButton= view.findViewById(R.id.tourApproveActionSearchDoctor)

        recyclerView = view.findViewById(R.id.login_status_check)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = ArrayList<CheckUserLoginStatusData>()
        adapter = CheckUserLoginStatusAdapter(this, data)
        recyclerView.adapter = adapter
        getDataFromServer()

        buttonSubmit.setOnClickListener {
            //adapter.onItemClick
            if(adapter.getCheckedItems().isNotEmpty()){
                //Log.d(TAG, "onViewCreated: addListToView 111 ${products.size}")
                val selectedUpdates = adapter.getCheckedItems().filter { it.isSelected == true }
                //TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportCompanion(selectedUpdates.toTypedArray())
                //TripManagerApproveUserTrip.updateTripApproveUserDetails(selectedUpdates.toTypedArray())
                //TripManagerApproveUserTrip.updateRefreshUserApproveSelectedList(Random.nextInt(1,100))
                dismiss()
            } else {
                //TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportCompanion(emptyArray())
                //TripManagerApproveUserTrip.updateTripApproveUserDetails(emptyArray())
                //TripManagerApproveUserTrip.updateRefreshUserApproveSelectedList(Random.nextInt(1,100))
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

        binding.allUsers.setOnClickListener {
            filter("")
        }

        binding.loggedInUsers.setOnClickListener {
            filter("Sush")
        }

        binding.notLoggedInUsers.setOnClickListener {
            filter("Vik")
        }

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
                val data = ArrayList<CheckUserLoginStatusData>()
                val gson = Gson()
                products = gson.fromJson(itemDataList, Array<CheckUserLoginStatusData>::class.java).toList().toTypedArray()
                for(product in products) {
                    data.add(product)
                }

                adapter = CheckUserLoginStatusAdapter(this, data)

                recyclerView.adapter = adapter

            })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    override fun onItemSelected(item: CheckUserLoginStatusData, isChecked: Boolean) {
        //Toast.makeText(context, "Source $item", Toast.LENGTH_SHORT).show()
        Log.d("TAG", "addListToView__: ${item.name}  :: ${isChecked}")
        val index = products.indexOf(item)
        if (index != -1) {
            val updatedItem = item.copy(isSelected = isChecked)
            products[index] = updatedItem
        } else {
        }
    }

    private fun filter(text: String) {

        var data = ArrayList<CheckUserLoginStatusData>()
        val gson = Gson()
        //val products = gson.fromJson(responBody, Array<TourPlanUpdate>::class.java).toList()

        activity?.runOnUiThread(Runnable {

            val filteredlist: ArrayList<CheckUserLoginStatusData> = ArrayList()

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
        val TAG: String = TourApprovalSelectUser::class.java.name

        fun newInstance(arg1: String): TourApprovalSelectUser {
            val fragment = TourApprovalSelectUser()
            val args = Bundle()
            args.putString("ARG1_KEY", arg1)
            fragment.arguments = args
            return fragment
        }
    }
}
interface CheckUserLoginStatusI {
    fun onItemSelected(item: CheckUserLoginStatusData, isChecked: Boolean)
}