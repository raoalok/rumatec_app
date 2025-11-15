package com.js_loop_erp.components.fragments.tripTourPlan

import android.annotation.SuppressLint
import android.app.Dialog
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
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.TripTourPlanHospitalSelectionAdapter
import  com.js_loop_erp.components.data_class.TripTourPlanHospitalSelection
import  com.js_loop_erp.components.data_flow.TripPlanFormSharedFlow
import  com.js_loop_erp.components.databinding.TripTourPlanHospitalSelectionFragmentBinding
import com.js_loop_erp.components.fragments.LoginFragment
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.security.SecureRandom
import java.util.Locale
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class TripTourPlanHospitalSelectionFragment  : DialogFragment(), TripTourPlanHospitalSelectionFragmentItemsOnClickListenerI {

    private var _binding: TripTourPlanHospitalSelectionFragmentBinding? = null
    private val binding get() = _binding!!
    var argEdit:String= ""
    var responBody:String = ""

    var hospitals: Array<TripTourPlanHospitalSelection> = emptyArray<TripTourPlanHospitalSelection>()
    private lateinit var buttonSubmit: ExtendedFloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchButton: SearchView
    private lateinit var adapter: TripTourPlanHospitalSelectionAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
        argEdit = arguments?.getString("ARG1_KEY").toString()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TripTourPlanHospitalSelectionFragmentBinding.inflate(inflater, container,false)
        getDialog()?.setTitle("Trip Plan Select Hospital") // ${argEdit}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        searchButton = view.findViewById(R.id.trip_plan_hospital_search_button)
        buttonSubmit = view.findViewById(R.id.tripTourPlanHospitalSection)

        recyclerView = view.findViewById(R.id.trip_tour_plan_hospital_selection_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)


        val data = ArrayList<TripTourPlanHospitalSelection>()
        adapter = TripTourPlanHospitalSelectionAdapter(this, data)

        getTripPlanHospitalListFromServer()

        searchButton.setOnClickListener{

        }

        buttonSubmit.setOnClickListener{
            if(adapter.getCheckedItems().isNotEmpty()){
                val selectedHospital = hospitals.filter{it.isSelected == true}
                //Toast.makeText(context,selectedHospital.size.toString(),Toast.LENGTH_LONG).show()
                TripPlanFormSharedFlow.updateTripPlanFormHospital(selectedHospital.toTypedArray())
                dismiss()
            } else {
                TripPlanFormSharedFlow.updateTripPlanFormHospital(emptyArray())
                dismiss()
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
                Log.d(TAG, "onQueryTextChange: this this this ")
                filterHospital(msg)
                return false
            }
        })
    }

    private fun getTripPlanHospitalListFromServer(){
        if (ReceiverMediator.USER_TOKEN.length > 8) {
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
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/hospitals/ids?routeId=${TripPlanFormFragmnet.ROUTE_ID}")
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

    private fun addListToView(hospitalList: String){
        try{
            activity?.runOnUiThread(Runnable {
                val data = ArrayList<TripTourPlanHospitalSelection>()
                val gson = Gson()
                hospitals = gson.fromJson(hospitalList, Array<TripTourPlanHospitalSelection>::class.java).toList().toTypedArray()
                for(hospital in hospitals){
                    data.add(hospital)
                }
                adapter = TripTourPlanHospitalSelectionAdapter(this, data)
                recyclerView.adapter = adapter
            })
        } catch (e: Exception){
            Log.d(TAG, "addListToView: ${e.toString()}")
        }
    }

    private fun filterHospital(text: String) {

        activity?.runOnUiThread {
            val filteredList: ArrayList<TripTourPlanHospitalSelection> = ArrayList()
            for (hospital in hospitals) {
                if (hospital.name?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true) {
                    filteredList.add(hospital)
                }
            }
            if (filteredList.isEmpty()) {

            } else {
                adapter.filterList(filteredList)
            }
        }
    }

    override fun onItemSelected(item: TripTourPlanHospitalSelection, isChecked: Boolean) {
        val index = hospitals.indexOf(item)
        Log.d( TAG, "onItemSelected: ${item}")
        if(index != -1){
            val updatedItem = item.copy(isSelected = isChecked)
            hospitals[index] = updatedItem
        } else {

        }
    }

    companion object {
        val TAG: String = TripTourPlanHospitalSelectionFragment::class.java.name

        fun newInstance(arg1: String): TripTourPlanHospitalSelectionFragment {
            val fragment = TripTourPlanHospitalSelectionFragment()
            val args = Bundle()
            args.putString("ARG1_KEY",arg1)
            fragment.arguments = args
            return fragment
        }
    }
}

interface TripTourPlanHospitalSelectionFragmentItemsOnClickListenerI {
    fun onItemSelected(item:TripTourPlanHospitalSelection, isChecked: Boolean )
}