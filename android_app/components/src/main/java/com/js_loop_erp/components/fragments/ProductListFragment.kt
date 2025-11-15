package com.js_loop_erp.components.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.ProductListCustomAdapter
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.ProductList
import  com.js_loop_erp.components.databinding.ProductListBinding
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import com.js_loop_erp.components.data_flow.LoginDetails
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ProductListFragment: DialogFragment() {
    private var _binding: ProductListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchButton: SearchView

    var responBody: String = ""
    var adapter = ProductListCustomAdapter(ArrayList<ProductList>())

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogThemeNoTitle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProductListBinding.inflate(inflater, container, false)
        //getDialog()?.setTitle("Product List")
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Set your animation style here
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.product_list_fragment_recycler_view)
        searchButton = view.findViewById(R.id.product_list_action_search)

        recyclerView.layoutManager = LinearLayoutManager(context)
        val data = ArrayList<ProductList>()
        val itemDataList = getInventoryLisFromServer()
        val adapter = ProductListCustomAdapter(data)
        searchButton.setOnClickListener{

        }

        searchButton.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
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
        recyclerView.adapter = adapter
    }

    private fun getInventoryLisFromServer(){
        if(ReceiverMediator.USER_TOKEN.length > 8) {
            var client = OkHttpClient()

            if(BuildConfig.DEBUG_ONLY_BUILD){

                Log.d(LoginFragment.TAG, "Debug Build Bypass the SSL check")

                val trustAllCerts = arrayOf<TrustManager>(
                    @SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkClientTrusted(
                            chain: Array<out X509Certificate>?,
                            authType: String?
                        ) {}
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkServerTrusted(
                            chain: Array<out X509Certificate>?,
                            authType: String?
                        ) {}
                        override fun getAcceptedIssuers(): Array<out X509Certificate> = arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .build()
            } else {
                client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()
            }

            val request = Request.Builder()
                //.url("http://65.0.61.137/api/products/ids")
                //.url("http://65.0.61.137/api/inventory/cnf/saleable/inventory/")
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/products/list/")
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
                            Log.d(TAG, "onResponse..: ${it.toString()}")
                        } else {
                            responBody= response.body?.string() ?: ""
                             println(responBody)
                            addListToView(responBody)
                        }
                    }
                }
            })
        } else {
            Toast.makeText(context,"Login Token Error ${LoginDetails.loginToken.value.length}",Toast.LENGTH_LONG).show()
        }
    }

    fun addListToView(itemDataList: String) {
        try{
        activity?.runOnUiThread(Runnable{
            var data = ArrayList<ProductList>()
            val gson = Gson()
            val products = gson.fromJson(itemDataList, Array<ProductList>::class.java).toList()
            for(product in products){
                data.add(product)
            }
//            val adapter = InventoryFragmentCustomAdapter(data)
            adapter = ProductListCustomAdapter(data)

            recyclerView.adapter = adapter
        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    private fun filter(text: String) {
        try{

        var data = ArrayList<ProductList>()
        val gson = Gson()
        val products = gson.fromJson(responBody, Array<ProductList>::class.java).toList()

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
            }

        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }
    companion object {
         val TAG: String = ProductListFragment::class.java.name
    }

}