package com.js_loop_erp.components.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.InventoryEditAdapter
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.SubmittedInventoryCheckEditFragmentBinding
import com.google.gson.Gson
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

class SubmittedInventoryCheckEditFragment: DialogFragment(), SubmittedInventoryRecyclerViewItemClickListenerI {
    private var _binding: SubmittedInventoryCheckEditFragmentBinding? = null

    var responBody: String = " "

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
        _binding = SubmittedInventoryCheckEditFragmentBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Fullfilled Inventory")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.fullfilled_inventory_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = ArrayList<InventoryEditViewModel>()

        getInventoryListFromServer()

        val adapter = InventoryEditAdapter(this, data)

        recyclerView.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getInventoryListFromServer() {

        var responBody: String = " "

        if (ReceiverMediator.USER_TOKEN.length > 8) {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                //.url("http://65.0.61.137/api/inventory/sample")
                .url("http://65.0.61.137/api/samples")
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

    fun deleteFromServer(position: Int) {

        Log.d(ExpenseEditFragment.TAG, "deleteFromServer: ${position} ")
        var responBody: String = " "
        val mediaType = "text/plain".toMediaType()
        val body = "".toRequestBody(mediaType)

        if (ReceiverMediator.USER_TOKEN.length > 8) {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                //.url("http://65.0.61.137/api/inventory/sample")
                //.url("http://65.0.61.137/api/expenses/agent")
                .url("http://65.0.61.137/api/samples/${position}")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            Log.d(ExpenseEditFragment.TAG, "deleteFromServer: failure ${response} ")

                        } else {
                            //getExpenseListFromServer()
                            Log.d(ExpenseEditFragment.TAG, "deleteFromServer: success ${response} ")
                            getInventoryListFromServer()
                            //recyclerView.adapter?.notifyItemRemoved(itemPosition)
                            //recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            })
        }
    }


    fun addListToView(itemDataList: String){
        try{
        activity?.runOnUiThread(Runnable{
            val data = ArrayList<InventoryEditViewModel>()
            val gson = Gson()
            val products = gson.fromJson(itemDataList, Array<InventoryEditViewModel>::class.java).toList()
            for(product in products) {
                data.add(product)
            }

            val adapter = InventoryEditAdapter(this, data)

            recyclerView.adapter = adapter

        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    override fun onItemClick(position: Int) {

        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Delete submitted entry?")
        builder.setPositiveButton("Ok") { _, _ ->
            deleteFromServer(position)
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

    companion object {
        const val TAG = "SubmittedInventoryCheckEditFragment"
    }
}

interface SubmittedInventoryRecyclerViewItemClickListenerI {
    fun onItemClick(position: Int)
}