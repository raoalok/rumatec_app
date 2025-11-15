package com.js_loop_erp.components.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.InventoryFragmentCustomAdapter
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.InventoryFragmentBinding
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


class InventoryFragment : DialogFragment() , View.OnKeyListener {

    private var _binding: InventoryFragmentBinding? = null

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    private lateinit var searchButton: SearchView

    var responBody: String = ""

    var adapter =  InventoryFragmentCustomAdapter(ArrayList<ItemsViewModel>())
    val actionsViewModel: ActionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInsatnceState: Bundle?
    ): View {
        _binding = InventoryFragmentBinding.inflate(inflater, container,false)
        getDialog()?.setTitle("Inventory List")

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInsatanceState: Bundle?) {
        super.onViewCreated(view, savedInsatanceState)

        recyclerView = view.findViewById(R.id.inventory_fragment_recycler_view)
        searchButton = view.findViewById(R.id.actionSearch)
        
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = ArrayList<ItemsViewModel>()
        val itemDataList = getInventoryListFromServer()

        val adapter = InventoryFragmentCustomAdapter(data)
        
        searchButton.setOnClickListener {
            Log.d(TAG, "onViewCreated: clicked clicked clicked")
        }

        searchButton.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                Log.d(TAG, "onQueryTextChange: this this this ")
                filter(msg)
                return false
            }
        })

        recyclerView.adapter = adapter

        actionsViewModel._backPressed_.observe(this@InventoryFragment, Observer {
            Handler(Looper.getMainLooper()).post {
                Log.d(TAG, "onStart: BackPressed1")
            }
        })
    }

    private fun getInventoryListFromServer(){
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
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/inventory/sample")
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

    fun addListToView(itemDataList: String) {
        try{
        activity?.runOnUiThread(Runnable{
            var data = ArrayList<ItemsViewModel>()
            val gson = Gson()
            val products = gson.fromJson(itemDataList, Array<ItemsViewModel>::class.java).toList()
            for(product in products){
                data.add(product)
            }
            adapter = InventoryFragmentCustomAdapter(data)

            recyclerView.adapter = adapter

            view?.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                    Log.d(TAG, "onKey: kdslfkasl")
                    return true
                }
            })
        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.

        try {
            var data = ArrayList<ItemsViewModel>()
            val gson = Gson()
            val products = gson.fromJson(responBody, Array<ItemsViewModel>::class.java).toList()

            activity?.runOnUiThread(Runnable {

                val filteredlist: ArrayList<ItemsViewModel> = ArrayList()
                for (product in products) {
                    if (product.product?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true) {

                        filteredlist.add(product)
                    }
                }
                if (filteredlist.isEmpty()) {
//            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
                } else {
                    adapter.filterList(filteredlist)
                }

            })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }
    companion object {
        const val TAG = "InventoryFragment"
    }

    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
       // TODO("Not yet implemented")
        Log.d(TAG, "onKey: dkfsdjfskdjfd")
        return false
    }
}