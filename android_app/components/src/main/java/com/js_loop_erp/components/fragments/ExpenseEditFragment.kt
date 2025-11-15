package com.js_loop_erp.components.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.ExpenseEditBinding
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ExpenseEditFragment : DialogFragment(), RecyclerViewItemClickListener {

    private var _binding: ExpenseEditBinding? = null
    var responBody: String = " "

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Set your animation style here
        return dialog
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExpenseEditBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Edit Expense")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.fullfilled_expense_inventory_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = ArrayList<ExpenseItemUpdated>()

        getExpenseListFromServer()
        val adapter = ExpenseEditAdapter(this, data)
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getExpenseListFromServer() {
        var responBody: String = " "

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
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/expenses/list")
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
                            addListToView(responBody)
                        }
                    }
                }
            })
        }
    }

    fun deleteFromServer(position: Int, itemPosition: Int) {
        Log.d(TAG, "deleteFromServer: ${position} ")
        var responBody: String = " "
        val mediaType = "text/plain".toMediaType()
        val body = "".toRequestBody(mediaType)

        // lifecycleScope.launch {

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
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/expenses/${position}")
                //.addHeader("Content-Type", "application/x-www-form-urlencoded")
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
                            Log.d(TAG, "deleteFromServer: failure ${response} ")

                        } else {
                            //getExpenseListFromServer()
                            Log.d(TAG, "deleteFromServer: success ${response} ")
                            getExpenseListFromServer()
                            //recyclerView.adapter?.notifyItemRemoved(itemPosition)
                            //recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            })
        }
        //   }
    }

    fun addListToView(expenseDataList: String) {
        try{
        activity?.runOnUiThread(Runnable {
            val data = ArrayList<ExpenseItemUpdated>()
            val gson = Gson()
            Log.d(TAG, "addListToView: ${expenseDataList.toString()} ")
            val products = gson.fromJson(expenseDataList, Array<ExpenseItemUpdated>::class.java).toList()
            for (product in products) {
                data.add(product)
            }
            val adapter = ExpenseEditAdapter(this, data)

            recyclerView.adapter = adapter
        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    companion object {
        val TAG: String = ExpenseEditFragment::class.java.name
    }

    override fun onItemClick(position: Int) {
        // TODO("Not yet implemented")
        //deleteFromServer(position, 0)
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Delete submitted expense?")
        //builder.setMessage("Device Location is Required.")
        builder.setPositiveButton("Ok") { _, _ ->
            deleteFromServer(position, 0)
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

}

interface RecyclerViewItemClickListener {
    fun onItemClick(position: Int)
}