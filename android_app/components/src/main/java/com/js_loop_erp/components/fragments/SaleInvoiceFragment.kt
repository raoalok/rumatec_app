package com.js_loop_erp.components.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.FragmentActivityI
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.SaleInvoiceCustomAdapter
import  com.js_loop_erp.components.databinding.SaleInvoiceFragmentBinding
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
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
import androidx.core.net.toUri


class SaleInvoiceFragment: DialogFragment(), SaleInvoiceRecyclerViewItemClickListenerI {

    private var _binding: SaleInvoiceFragmentBinding? = null
    private lateinit var fragmentActivityI:FragmentActivityI
    private lateinit var actionsViewModel: ActionsViewModel
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchButton : SearchView

    var responBody: String = ""
    var adapter = SaleInvoiceCustomAdapter(this, ArrayList<SaleInvoiceViewModel>())

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
        _binding = SaleInvoiceFragmentBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Sales Invoice")
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Set your animation style here
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is FragmentActivityI) {
            fragmentActivityI = context as FragmentActivityI
            Log.d(TAG, "onAttach: ${context} implemented MyListener")
            fragmentActivityI.showMessage("HIxxx")

        } else {
            Log.d(TAG, "onAttach: ${context} must implement MyListener")
        }
        this.fragmentActivityI = fragmentActivityI
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        actionsViewModel = ViewModelProvider(this@SaleInvoiceFragment).get(ActionsViewModel::class.java)
        actionsViewModel.openUri(Uri.parse("dsajdkhdhjh"))

        recyclerView = view.findViewById(R.id.sale_invoice_fragment_recycler_view)
        searchButton = view.findViewById(R.id.saleInvoiceActionSearch)

        recyclerView.layoutManager = LinearLayoutManager(context)
        val data = ArrayList<SaleInvoiceViewModel>()
        val itemDataList = getInventoryListFromServer()

        val adapter = SaleInvoiceCustomAdapter(this, data)

        searchButton.setOnClickListener{

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
        recyclerView.adapter = adapter
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
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/sales/cnf/agentwise")
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
            var data = ArrayList<SaleInvoiceViewModel>()
            val gson = Gson()
            val products = gson.fromJson(itemDataList, Array<SaleInvoiceViewModel>::class.java).toList()
            for(product in products){
                data.add(product)
            }
            adapter = SaleInvoiceCustomAdapter(this, data)

            recyclerView.adapter = adapter
            

        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    private fun filter(text: String) {
        try{

        var data = ArrayList<SaleInvoiceViewModel>()
        val gson = Gson()
        val products = gson.fromJson(responBody, Array<SaleInvoiceViewModel>::class.java).toList()

        activity?.runOnUiThread(Runnable {

            val filteredlist: ArrayList<SaleInvoiceViewModel> = ArrayList()

            for (product in products) {
                if (product.party?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true) {
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
        val TAG = SaleInvoiceFragment::class.java.simpleName
    }

    override fun onItemClick(position: Int) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Download PDF")
        //builder.setMessage("Device Location is Required.")
        builder.setPositiveButton("Ok") { _, _ ->
            val uri = "${ReceiverMediator.SERVER_SYNC_URI}/api/sales/cnf/${position}/pdf".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            try {
                startActivity(intent)
            } catch (_: Exception){
                Toast.makeText(context, "Error in opening link.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

        /*val uri = Uri.parse("http://65.0.61.137/api/sales/cnf/${position}/pdf")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(intent)
        } catch (_: Exception){
            Toast.makeText(context, "Error in opening link.", Toast.LENGTH_SHORT).show()
        }*/
    }
}

interface SaleInvoiceRecyclerViewItemClickListenerI {
    fun onItemClick(position: Int)
}