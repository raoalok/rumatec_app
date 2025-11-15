package com.js_loop_erp.components.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.MainActivity
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.TripReportApprovedAdapter
import  com.js_loop_erp.components.data_class.TripReportApprovedModel
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlan
import  com.js_loop_erp.components.data_flow.TripTravelReportSubmitSharedFlow
import  com.js_loop_erp.components.databinding.TripReportFragmentBinding
import  com.js_loop_erp.components.fragments.tripReportSubmit.TripApprovedPlanDetails
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class TripReportFragment: DialogFragment(), TripReportApprovedI {
    private var _binding: TripReportFragmentBinding? = null

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    var responBody: String = ""

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TripReportFragmentBinding.inflate(inflater, container, false)
        dialog?.setTitle("Trip Report Update")

        return binding.root
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.trip_report_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data  = ArrayList<TripReportApprovedModel>()
        val adapter = TripReportApprovedAdapter(this, data)
        recyclerView.adapter = adapter
        getDataFromServer()
    }


    fun getDataFromServer(){
        //var responBody: String = " "
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
//                .url("http://65.0.61.137/api/inventory/sample")
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/approvedlist")
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
            val data = ArrayList<TripReportApprovedModel>()
            val gson = Gson()
            val products = gson.fromJson(itemDataList, Array<TripReportApprovedModel>::class.java).toList()
            for(product in products) {
                data.add(product)
            }

            val adapter = TripReportApprovedAdapter(this, data)

            recyclerView.adapter = adapter

        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    override fun onItemClick(item: TripReportApprovedModel) {
        //Toast.makeText(context,"...", Toast.LENGTH_LONG).show()
        TripTravelReportSubmitSharedFlow.updateTripTravelReportSubmitSharedFlow(item)
        TripTravelReportSubmitSharedFlow.resetCurrentTripTravelRoutePlanDetails()
        //Toast.makeText(context, item.toString(), Toast.LENGTH_LONG).show()
        val childDialog = TripApprovedPlanDetails()
        childDialog.show(childFragmentManager, "TripApprovedPlanDetails")
    }
    companion object {
        const val TAG = "TripReportFragment"
    }
}

interface TripReportApprovedI {
    fun onItemClick(item: TripReportApprovedModel)
}