package com.js_loop_erp.components.fragments.access_controlled

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.marginBottom
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.LeaveApproveAdapter
import  com.js_loop_erp.components.data_class.TripTourPlanRouteList
import  com.js_loop_erp.components.data_flow.TripManagerApproveUserTrip
import  com.js_loop_erp.components.databinding.LeaveApproveRejectBinding
import  com.js_loop_erp.components.fragments.TripReportFragment
import  com.js_loop_erp.components.fragments.tripTourPlan.TripTourPlanInstituteSelectionFragment
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.concurrent.TimeUnit

class LeaveApproveReject : DialogFragment(),LeaveApproveRejectI {

    private var _binding: LeaveApproveRejectBinding? = null

    private val binding get() = _binding!!

    private lateinit var activityPlaceTypeArray: ArrayAdapter<String>


    private lateinit var approveRejectRemark: TextInputEditText

    private lateinit var recyclerView: RecyclerView


    var routes: Array<TripTourPlanRouteList> = emptyArray<TripTourPlanRouteList>()


    var spinnerInitialized = false

    var responBody:String = ""


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LeaveApproveRejectBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Leave List")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInsatanceState: Bundle?){
        super.onViewCreated(view, savedInsatanceState)




        approveRejectRemark = view.findViewById(R.id.leave_approve_reject_remark)

        recyclerView = view.findViewById(R.id.leave_apporoval_list)

        recyclerView.layoutManager = LinearLayoutManager(context)


        val data  = ArrayList<LeaveApproveModel>()
        val adapter = LeaveApproveAdapter(this, data)
        recyclerView.adapter = adapter

        TripManagerApproveUserTrip.updateTripApproveUserDetails(emptyArray())

        getDataFromServer()

        //addDataToPlaceType()

        lifecycleScope.launch {
            TripManagerApproveUserTrip.refreshUserApproveSelectedList.collect { updates ->

                if (TripManagerApproveUserTrip.tripApproveUserDetails.value.isNotEmpty()) {
                    activity?.runOnUiThread(Runnable {
                        /*adapterHospitalList.filterList(updates.toList())
                        adapterHospitalListData = updates.toList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewHospital)*/
                        //recyclerViewUsersAdapter.filterList(TripManagerApproveUserTrip.tripApproveUserDetails.value.toList())
                        //setRecyclerViewHeightBasedOnChildren(recyclerViewUsers)
                    })
                } else {
                    activity?.runOnUiThread(Runnable {
                        /*dapterHospitalList.filterList(emptyList())
                        adapterHospitalListData = emptyList()
                        setRecyclerViewHeightBasedOnChildren(recyclerViewHospital)*/
                        //recyclerViewUsersAdapter.filterList(emptyList())
                        //setRecyclerViewHeightBasedOnChildren(recyclerViewUsers)
                    })
                }
            }
        }

        /*addUsersButton.setOnClickListener {
            val fragment = TourApprovalSelectUser.newInstance("")
            fragment.show(childFragmentManager, "TourApprovalSelectUser")
        }*/


        /*        selectUserTrips.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        // An item was selected. You can retrieve the selected item using
                        if (!spinnerInitialized) {
                            spinnerInitialized = true
                            return
                        }
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        Log.d(TripPlanFormMeetingDetailsFragment.TAG, "onItemSelected:Spinner:Position ${position}")
                        getTripPlanRouteStartEndListFromServer(routes[position].id!!.toInt())
                        return
                    }
        
                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }*/

        getTripPlanRouteListFromServer()

    }

    fun getDataFromServer(){
        //var responBody: String = " "
        if(ReceiverMediator.USER_TOKEN.length > 8) {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
//                .url("http://65.0.61.137/api/inventory/sample")
                .url("http://65.0.61.137/api/application/tours/approvedlist")
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

                        } else {
                            responBody= response.body?.string() ?: ""
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
                val data = ArrayList<LeaveApproveModel>()
                val gson = Gson()
                val products = gson.fromJson(itemDataList, Array<LeaveApproveModel>::class.java).toList()
                for(product in products) {
                    data.add(product)
                }

                val adapter = LeaveApproveAdapter(this, data)

                recyclerView.adapter = adapter

            })
        } catch (e: Exception){
            Log.d(TripReportFragment.TAG, "filter: $e")
        }
    }

    private fun addDataToPlaceType(){
        val placeTypeArray = ArrayList<String>()
        placeTypeArray.add("")


        //activityPlaceTypeArray = ArrayAdapter(requireContext(), R.layout.item_select_spinner, placeTypeArray)
        //selectUserTrips.adapter = activityPlaceTypeArray
    }

    fun getTripPlanRouteListFromServer(){
        if (ReceiverMediator.USER_TOKEN.length > 8) {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                //.url("http://65.0.61.137/api/inventory/sample")
                //.url("http://65.0.61.137/api/expenses/agent")
                .url("http://65.0.61.137/api/application/routes/ids")
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
                            addRouteListToView(responBody)
                        }
                    }
                }
            })
        }
    }

    fun addRouteListToView(responBody: String){
        try{
            activity?.runOnUiThread(Runnable {
                val placeTypeArray = ArrayList<String>()
                //val data = ArrayList<TripTourPlanRouteList>()
                val gson = Gson()
                routes = gson.fromJson(responBody, Array<TripTourPlanRouteList>::class.java).toList().toTypedArray()
                for(route in routes){
                    placeTypeArray.add(route.route.toString())
                }

                if(routes.size >0) {
                    getTripPlanRouteStartEndListFromServer(routes[0].id!!.toInt())
                }
            })
        } catch (e: Exception){
            Log.d(TripTourPlanInstituteSelectionFragment.TAG, "addListToView: ${e.toString()}")
        }
    }

    fun getTripPlanRouteStartEndListFromServer(id: Int){
        if (ReceiverMediator.USER_TOKEN.length > 8) {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                //.url("http://65.0.61.137/api/inventory/sample")
                //.url("http://65.0.61.137/api/expenses/agent")
//                .url("http://65.0.61.137/api/application/routes/ids")
                .url("http://65.0.61.137/api/application/areas/ids?routeId=${id}")
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
                            addRouteStartEndListToView(responBody)
                        }
                    }
                }
            })
        }
    }

    fun addRouteStartEndListToView(responBody: String){
        try{
            activity?.runOnUiThread(Runnable {
                val placeToArray = ArrayList<String>()
                //val data = ArrayList<TripTourPlanRouteList>()
                val gson = Gson()

            })
        } catch (e: Exception){
            Log.d(TripTourPlanInstituteSelectionFragment.TAG, "addListToView: ${e.toString()}")
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
        val TAG = TripApproveFragment::class.java.name
    }

}

interface LeaveApproveRejectI {
}