package com.js_loop_erp.components.fragments.tripReportSubmit

import android.annotation.SuppressLint
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
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.TourPlanUpdatePartyAdapter
import  com.js_loop_erp.components.data_class.TourPlanUpdateDoctor
import  com.js_loop_erp.components.data_class.TripReportApprovedModel
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanParty
import  com.js_loop_erp.components.data_flow.TripTravelReportSubmitSharedFlow
import  com.js_loop_erp.components.databinding.TourPlanUpdateDoctorDetailsBinding
import  com.js_loop_erp.components.fragments.InventoryFragment
import com.js_loop_erp.components.fragments.LoginFragment
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class TourPlanUpdateDetailsParty: DialogFragment(), TourPlanUpdatePartyDetailsItemClickListenerI {

    private var _binding: TourPlanUpdateDoctorDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TourPlanUpdatePartyAdapter


    private lateinit var recyclerView: RecyclerView
//    private lateinit var buttonSubmit: AppCompatButton
    private lateinit var buttonSubmit: ExtendedFloatingActionButton
    private lateinit var searchButton: SearchView
    var responBody: String = ""
    var products: Array<TourPlanUpdateDoctor> = emptyArray<TourPlanUpdateDoctor>()

    var argEdit: String = " "


//    val tripTravelReportSubmitViewModel: TripTravelReportSubmitViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogGreyTheme)
        argEdit = arguments?.getString("ARG1_KEY").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TourPlanUpdateDoctorDetailsBinding.inflate(inflater, container, false)
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
        val data: ArrayList<TripSubmitReportSelectedTourPlanParty> = ArrayList()
        adapter = TourPlanUpdatePartyAdapter(this, data)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            TripTravelReportSubmitSharedFlow.currentTripTravelRoutePlanDetails.collect { updates ->
                activity?.runOnUiThread(Runnable {
                    //Log.d(TAG, "Received trip details: addListToView 332  ${updates.toString()}")
                    val data3: ArrayList<TripSubmitReportSelectedTourPlanParty> = ArrayList(updates.parties)
                    if (data3.size > 0) {
                        adapter.filterList(data3)
                    }
                })
            }
        }

        buttonSubmit.setOnClickListener {

            /* lifecycleScope.launch {
                 // Example of some long-running operation
                 withContext(Dispatchers.Main) {*/
            if(adapter.getSelected() != null && adapter.getSelected()!!.id!! > 0){
                TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportParty(adapter.getSelected()!!)
                dismiss()
            } else {
                Toast.makeText(context,"Select Doctor", Toast.LENGTH_LONG).show()
            }
            //Log.d(TAG, "onViewCreated: addListToView ${adapter.getSelected()} ")
            // Update UI or perform other actions based on item count
            /*  }
          }*/
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
                //filter(msg)
                return false
            }
        })

    }

    fun getDataFromServer(){

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
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/view/${tripDetails.id}")
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

    override fun onItemSelected(item: TripSubmitReportSelectedTourPlanParty, isChecked: Boolean) {
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

    companion object {
        val TAG: String = TourPlanUpdateDetailsParty::class.java.name

        fun newInstance(arg1: String): TourPlanUpdateDetailsParty {
            val fragment = TourPlanUpdateDetailsParty()
            val args = Bundle()
            args.putString("ARG1_KEY", arg1)
            fragment.arguments = args
            return fragment
        }
    }
}
interface TourPlanUpdatePartyDetailsItemClickListenerI {
    fun onItemSelected(item: TripSubmitReportSelectedTourPlanParty, isChecked: Boolean)
}