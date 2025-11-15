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
import  com.js_loop_erp.components.adapter.TripTourPlanCompanionSelectionAdapter
import  com.js_loop_erp.components.data_class.TripTourPlanCompanionSelection
import  com.js_loop_erp.components.data_flow.TripPlanFormSharedFlow
import  com.js_loop_erp.components.databinding.TripTourPlanCompanionSelectionFragmentBinding
import com.js_loop_erp.components.fragments.LoginFragment
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.security.SecureRandom
import java.util.Locale
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class TripTourPlanCompanionSelectionFragment : DialogFragment(), TripTourPlanCompanionSelectionFragmentItemsOnClickListenerI {

    private var _binding: TripTourPlanCompanionSelectionFragmentBinding? = null
    private val binding get() = _binding!!
    var argEdit:String= ""
    var responBody:String = ""

    var companions: Array<TripTourPlanCompanionSelection> = emptyArray<TripTourPlanCompanionSelection>()
    private lateinit var buttonSubmit: ExtendedFloatingActionButton
    private lateinit var recyclerView:RecyclerView
    private lateinit var searchButton: SearchView
    private lateinit var adapter: TripTourPlanCompanionSelectionAdapter

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
        _binding = TripTourPlanCompanionSelectionFragmentBinding.inflate(inflater, container,false)
        getDialog()?.setTitle("Trip Plan Select Companion")// ${argEdit}")
        return binding.root
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        searchButton = view.findViewById(R.id.trip_plan_companion_search_button)
        buttonSubmit = view.findViewById(R.id.tripTourPlanCompanionSection)

        recyclerView = view.findViewById(R.id.trip_tour_plan_companion_selection_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)


        val data = ArrayList<TripTourPlanCompanionSelection>()
        adapter = TripTourPlanCompanionSelectionAdapter(this, data)

        getTripPlanCompanionListFromServer()

        searchButton.setOnClickListener{

        }

        buttonSubmit.setOnClickListener{
            if(adapter.getCheckedItems().isNotEmpty()){
                val selectedCompanion = companions.filter{it.isSelected == true}
                //Toast.makeText(context,selectedCompanion.size.toString(),Toast.LENGTH_LONG).show()
                TripPlanFormSharedFlow.updateTripPlanFormCompanion(selectedCompanion.toTypedArray())
                dismiss()
            } else {
                TripPlanFormSharedFlow.updateTripPlanFormCompanion(emptyArray())
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
                filterCompanion(msg)
                return false
            }
        })
    }

    private fun getTripPlanCompanionListFromServer(){
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
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/users/ids")
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

    private fun addListToView(companionList: String){
        try{
            activity?.runOnUiThread(Runnable {
                val data = ArrayList<TripTourPlanCompanionSelection>()
                val gson = Gson()
                companions = gson.fromJson(companionList, Array<TripTourPlanCompanionSelection>::class.java).toList().toTypedArray()
                for(companion in companions){
                    data.add(companion)
                }
                adapter = TripTourPlanCompanionSelectionAdapter(this, data)
                recyclerView.adapter = adapter
            })
        } catch (e: Exception){
            Log.d(TAG, "addListToView: ${e.toString()}")
        }
    }

    private fun filterCompanion(text: String) {

        activity?.runOnUiThread {
            val filteredList: ArrayList<TripTourPlanCompanionSelection> = ArrayList()
            for (companion in companions) {
                if (companion.name?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true) {
                    filteredList.add(companion)
                }
            }
            if (filteredList.isEmpty()) {

            } else {
                adapter.filterList(filteredList)
            }
        }
    }

    override fun onItemSelected(item: TripTourPlanCompanionSelection, isChecked: Boolean) {
        val index = companions.indexOf(item)
        Log.d(TAG, "onItemSelected: ${item}")
        if(index != -1){
            val updatedItem = item.copy(isSelected = isChecked)
            companions[index] = updatedItem
        } else {

        }
    }

    companion object {
        val TAG: String = TripTourPlanCompanionSelectionFragment::class.java.name

        fun newInstance(arg1: String): TripTourPlanCompanionSelectionFragment {
            val fragment = TripTourPlanCompanionSelectionFragment()
            val args = Bundle()
            args.putString("ARG1_KEY",arg1)
            fragment.arguments = args
            return fragment
        }
    }
}

interface TripTourPlanCompanionSelectionFragmentItemsOnClickListenerI {
    fun onItemSelected(item: TripTourPlanCompanionSelection, isChecked: Boolean )
}