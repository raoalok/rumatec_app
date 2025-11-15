package com.js_loop_erp.components.fragments.tripReportSubmit

import android.annotation.SuppressLint
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
import  com.js_loop_erp.components.adapter.TourPlanUpdateDetailsProductAdapter
import  com.js_loop_erp.components.data_class.ProductList
import  com.js_loop_erp.components.data_flow.TripTravelReportSubmitSharedFlow
import  com.js_loop_erp.components.databinding.TourPlanUpdateDetailsProductsBinding
import  com.js_loop_erp.components.fragments.InventoryFragment
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

class TourPlanUpdateDetailsProduct: DialogFragment(),TourPlanUpdateDetailsProductItemClickListenerI {

    private var _binding: TourPlanUpdateDetailsProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TourPlanUpdateDetailsProductAdapter


    private lateinit var recyclerView: RecyclerView
    //    private lateinit var buttonSubmit: AppCompatButton
    private lateinit var buttonSubmit: ExtendedFloatingActionButton
    private lateinit var searchButton: SearchView
    var responBody: String = ""
    var products: Array<ProductList> = emptyArray<ProductList>()

    var argEdit: String = " "

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogGreyTheme)
        argEdit = arguments?.getString("ARG1_KEY").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TourPlanUpdateDetailsProductsBinding.inflate(inflater, container, false)
//        getDialog()?.setTitle("Tour Visit Update ${argEdit}")
        getDialog()?.setTitle("Tour Visit Add Products")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        buttonSubmit = view.findViewById(R.id.button_get_tour_plan_checked_doctor_items)
        searchButton= view.findViewById(R.id.tourUpdateActionSearchDoctor)



        recyclerView = view.findViewById(R.id.trip_plan_update_doctor_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = ArrayList<ProductList>()
        adapter = TourPlanUpdateDetailsProductAdapter(this, data)
        getDataFromServer()



        buttonSubmit.setOnClickListener {
            //adapter.onItemClick
            if(adapter.getCheckedItems().isNotEmpty()){
                //Log.d(TAG, "onViewCreated: addListToView 111 ${products.size}")
                val selectedUpdates = adapter.getCheckedItems().filter { it.isSelected == true }
                TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportProducts(selectedUpdates.toTypedArray())
                dismiss()
            } else {
                TripTravelReportSubmitSharedFlow.updatePlanReportTripPlanReportProducts(emptyArray())
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
    fun getDataFromServer(){

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
                //.url("http://65.0.61.137/api/samples")
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/products/ids")
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
                val data = ArrayList<ProductList>()
                val gson = Gson()
                products = gson.fromJson(itemDataList, Array<ProductList>::class.java).toList().toTypedArray()
                for(product in products) {
                    data.add(product)
                }

                adapter = TourPlanUpdateDetailsProductAdapter(this, data)

                recyclerView.adapter = adapter

            })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    override fun onItemSelected(item: ProductList, isChecked: Boolean) {
//        Toast.makeText(context, "Source $item", Toast.LENGTH_SHORT).show()

        val index = products.indexOf(item)
        if (index != -1) {
            val updatedItem = item.copy(isSelected = isChecked)
            products[index] = updatedItem
        } else {
            // Handle item not found
            //println("Item not found in the list")
        }
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.

        var data = ArrayList<ProductList>()
        val gson = Gson()
        //val products = gson.fromJson(responBody, Array<TourPlanUpdate>::class.java).toList()

        activity?.runOnUiThread(Runnable {

            val filteredlist: ArrayList<ProductList> = ArrayList()

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
        val TAG: String = TourPlanUpdateDetailsProduct::class.java.name

        fun newInstance(arg1: String): TourPlanUpdateDetailsProduct {
            val fragment = TourPlanUpdateDetailsProduct()
            val args = Bundle()
            args.putString("ARG1_KEY", arg1)
            fragment.arguments = args
            return fragment
        }
    }
}
interface TourPlanUpdateDetailsProductItemClickListenerI {
    fun onItemSelected(item: ProductList, isChecked: Boolean)
}