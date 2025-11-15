package com.js_loop_erp.components.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import  com.js_loop_erp.components.CustomAdapter
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.NotificationsFragmentBinding
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class NotificationsFragment : DialogFragment() {

    private var _binding: NotificationsFragmentBinding? = null

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = NotificationsFragmentBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Inventories List")
        Log.d(TAG, "onCreateView: hixxxxxxxxxxxxxxxxxxxxxx")

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById( R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = ArrayList<ItemsViewModel>()
        val itemDataList = getInventoryListFromServer()
        val adapter = CustomAdapter(data)

        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

fun getInventoryListFromServer():String{

        var returnValue = ""
        var responBody :String =""

        if(ReceiverMediator.USER_TOKEN.length > 8){

            val client = OkHttpClient.Builder()
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
//                .url("http://65.0.61.137/api/sample/reduces")
                .url("http://65.0.61.137/api/inventory/sample")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful){

                        } else {
                            responBody = response.body?.string() ?: ""
                            //println(responBody)
                            addListToView(responBody)
                        }
                    }
                }
            })
        }
        return responBody

    }

    fun addListToView(itemDataList: String) {
        try {
        activity?.runOnUiThread(Runnable {
            var data = ArrayList<ItemsViewModel>()
            Log.d(TAG, "addListToView: ${ReceiverMediator.USER_TOKEN}")
            val gson = Gson()
            val products = gson.fromJson(itemDataList, Array<ItemsViewModel>::class.java).toList()
            for (product in products) {
                data.add(product)
            }
            val adapter = CustomAdapter(data)
            recyclerView.adapter = adapter
        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }


    companion object {
        // val TAG = LoginFragment::class.java.simpleName
        const val TAG = "NotificationsFragmnet"


    }
}