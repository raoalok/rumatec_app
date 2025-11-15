package com.js_loop_erp.components.fragments

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
import com.google.gson.Gson
import  com.js_loop_erp.components.CnfInventoryCustomAdapter
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.CnfFragmentBinding
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

class CnfFragment: DialogFragment() {
    private var _binding: CnfFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchButton: SearchView

    var responBody: String = ""
    var adapter = CnfInventoryCustomAdapter(ArrayList<CnfViewModel>())

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
        _binding = CnfFragmentBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Inventory")
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Set your animation style here
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.cnf_fragment_recycler_view)
        searchButton = view.findViewById(R.id.cnfActionSearch)

        recyclerView.layoutManager = LinearLayoutManager(context)
        val data = ArrayList<CnfViewModel>()
        val itemDataList = getInventoryLisFromServer()

        val adapter = CnfInventoryCustomAdapter(data)

        searchButton.setOnClickListener{

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
        recyclerView.adapter = adapter
    }

    private fun getInventoryLisFromServer(){
        if(ReceiverMediator.USER_TOKEN.length > 8) {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                //.url("http://65.0.61.137/api/inventory/sample")
                .url("http://65.0.61.137/api/inventory/cnf/saleable/inventory/")
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
            var data = ArrayList<CnfViewModel>()
            val gson = Gson()
            val products = gson.fromJson(itemDataList, Array<CnfViewModel>::class.java).toList()
            for(product in products){
                data.add(product)
            }
//            val adapter = InventoryFragmentCustomAdapter(data)
            adapter = CnfInventoryCustomAdapter(data)

            recyclerView.adapter = adapter
        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        try{

        var data = ArrayList<CnfViewModel>()
        val gson = Gson()
        val products = gson.fromJson(responBody, Array<CnfViewModel>::class.java).toList()

        activity?.runOnUiThread(Runnable {

            val filteredlist: ArrayList<CnfViewModel> = ArrayList()

            for (product in products) {
                if (product.product?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true) {
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
        const val TAG = "CnfFragment"
    }

}