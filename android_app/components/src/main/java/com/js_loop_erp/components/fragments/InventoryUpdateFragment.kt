package com.js_loop_erp.components.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.InventoryUpdateBinding
import  com.js_loop_erp.components.fragments.InventoryPagination.Companion.INVENTORY_UPDATE_FRAGMENT
import  com.js_loop_erp.components.fragments.InventoryPagination.Companion.INVENTORY_UPDATE_FRAGMENT_DISMISS
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response


class InventoryUpdateFragment: DialogFragment() {
    private var _binding: InventoryUpdateBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    private lateinit var spinner_: AppCompatSpinner
    private lateinit var dataAdapter: ArrayAdapter<String>

    private lateinit var spinnerDoctorSelection: AppCompatSpinner
    private lateinit var dataDoctorNameSelection: ArrayAdapter<String>

    private lateinit var submitInventoryButton: Button
    private lateinit var quantityDistributed: TextInputEditText
    private lateinit var handedOverToName: TextInputEditText
    private lateinit var remarkProductDistributed: TextInputEditText

    private lateinit var  popupContainer: FrameLayout

    private lateinit var customView: CustomView
    private lateinit var popupbutton: Button


    var data = ArrayList<ItemsViewModel>()
    var data_ = ArrayList<String>()
    
    var products = ArrayList<ItemsViewModel>()
    var dataDoctors: Array<DoctorsViewModel> = emptyArray()

    var dataSampleDeliveryCategory = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
        _binding = InventoryUpdateBinding.inflate(inflater, container, false)

        getDialog()?.setTitle("Update Stock")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        spinner_ = view.findViewById<AppCompatSpinner>(R.id.item_stock_spinner)
        dataAdapter = ArrayAdapter(requireContext(), R.layout.item_select_spinner)

        submitInventoryButton = view.findViewById<Button>(R.id.submit_updated_inventory)
        quantityDistributed = view.findViewById<TextInputEditText>(R.id.update_item_stock)
        handedOverToName = view.findViewById<TextInputEditText>(R.id.inventory_handed_over_name)
        remarkProductDistributed = view.findViewById<TextInputEditText>(R.id.update_item_stock_reason)

        spinnerDoctorSelection =  view.findViewById<AppCompatSpinner>(R.id.item_doctor_name_spinner)
        dataDoctorNameSelection = ArrayAdapter(requireContext(), R.layout.item_select_spinner)


/*        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Alert Dialog Title")
            .setMessage("This is an alert dialog message")
            .create()

        builder.setPositiveButton("OK") { dialog, id ->

            dialog.dismiss()
        }


        builder.setNegativeButton("Cancel") { dialog, id ->

            dialog.dismiss()
        }*/


        submitInventoryButton.setOnClickListener{
            Log.d(TAG, "onViewCreated: aksd;fkk")
            val itemProduct = spinner_.selectedItemPosition
            val itemDoctorName = spinnerDoctorSelection.selectedItemPosition
            if(itemProduct != -1 && itemDoctorName != -1){
                Log.d(TAG, "onViewCreated: ${products.sortedBy{it.product}[itemProduct].productId.toString()}")
                Log.d(TAG, "onViewCreated: ${products.sortedBy{it.product}[itemProduct].product.toString()}")
                Log.d(TAG, "onViewCreated: ${dataDoctors[itemDoctorName].name.toString()}")

                if(quantityDistributed.text.toString().toInt() > 0){
                    //pushInventoryDistributionDataToServer(0,0)
                    val builder = android.app.AlertDialog.Builder(requireContext())
                    builder.setTitle("Submit Inventory")
                    //builder.setMessage("Device Location is Required.")
                    builder.setPositiveButton("Ok") { _, _ ->
                        submitInventoryButton.isClickable = false
                        pushInventoryDistributionDataToServer(0, 0)
                    }
                    builder.setNegativeButton("Cancel") { _, _ ->
                    }
                    builder.setCancelable(false)
                    val dialog = builder.create()
                    dialog.show()
                    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                } else {
                    Toast.makeText(context, "Error: Check added params", Toast.LENGTH_LONG).show()
                }
            }

            //dismiss()
            var bundle = Bundle().apply{
                putInt(INVENTORY_UPDATE_FRAGMENT_DISMISS, 1)
            }

        }

        //addDataToSpinnerDoctorSelection()
        getInventoryFromServer()
        getDoctorNameFromServer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getInventoryFromServer(){

        var responBody : String = ""

        if(ReceiverMediator.USER_TOKEN.length > 0){
            val client = OkHttpClient.Builder()
                .connectTimeout(20,java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("http://65.0.61.137/api/inventory/sample")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("Authorization","Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()
            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call, e: java.io.IOException){
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response){
                    response.use {
                        if (!response.isSuccessful){

                        } else {
                            responBody = response.body?.string() ?: ""
                           // println(responBody)

                            addDataToSpinner(responBody)
                        }
                    }
                }
            })
        }
    }

    fun addDataToSpinner(itemDataList: String){
        try{
        activity?.runOnUiThread(Runnable{

            val gson = Gson()
            products = gson.fromJson(itemDataList,Array<ItemsViewModel>::class.java).toList() as ArrayList<ItemsViewModel>

            for(product in products.sortedBy{ it.product}) {
                val product_ = " "+ product.product.toString().format(20) + ", " + "Batch: " + product.batch.toString()
                data_.add(product_)
            }

            dataAdapter = ArrayAdapter(requireContext(),R.layout.item_select_spinner,data_)
            spinner_.setAdapter(dataAdapter)

            Log.d(TAG, "addDataToSpinner: ${data_[0].toString()}")
            //spinner_.setSelection(0)
            dataAdapter.setDropDownViewResource(R.layout.select_spinner_)

        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    fun getDoctorNameFromServer() {
        var responBody : String = ""

        if(ReceiverMediator.USER_TOKEN.length > 0){
            val client = OkHttpClient.Builder()
                .connectTimeout(20,java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("http://65.0.61.137/api/doctors/ids")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("Authorization","Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()
            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call, e: java.io.IOException){
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response){
                    response.use {
                        if (!response.isSuccessful){

                        } else {
                            responBody = response.body?.string() ?: ""
                            // println(responBody)
                            //addDataToSpinner(responBody)
                            addDataToSpinnerDoctorSelection(responBody)
                        }
                    }
                }
            })
        }
    }

    fun addDataToSpinnerDoctorSelection(itemDataList: String) {
        try{
        activity?.runOnUiThread(Runnable {

            val dataDoctorsDropDown = ArrayList<String>()
            Log.d(TAG, "addDataToSpinnerDoctorSelection: ${itemDataList.toString()}")
            val gson = Gson()
            dataDoctors= gson.fromJson(itemDataList.trimIndent(), Array<DoctorsViewModel>::class.java) //.toList() as ArrayList<DoctorsViewModel>
            Log.d(TAG, "addDataToSpinnerDoctorSelection: ${dataDoctors.toString()}")
            for (doctor in dataDoctors) {
                doctor.name?.let { dataDoctorsDropDown.add(it) }
            }

            dataDoctorNameSelection = ArrayAdapter(requireContext(), R.layout.item_select_spinner, dataDoctorsDropDown)
            spinnerDoctorSelection.adapter = dataDoctorNameSelection
            dataDoctorNameSelection.setDropDownViewResource(R.layout.select_spinner_)
        })
        } catch (e: Exception){
            Log.d(TAG, "filter: $e")
        }
    }

    fun pushInventoryDistributionDataToServer(doctorIndex: Int, productIndex: Int ){

        var responBody : String = ""

        val itemProduct = spinner_.selectedItemPosition
        val itemDoctorPosition = spinnerDoctorSelection.selectedItemPosition
        val quantityDist = quantityDistributed.text.toString().toIntOrNull()
        val remarkProductDist  = remarkProductDistributed.text.toString()
        val handedOverTo = handedOverToName.text.toString()

        var detailsOfIstributedInventory:InventorySubmitted = InventorySubmitted()

        val productSorted: List<ItemsViewModel> = products.sortedBy{it.product}

        if(itemProduct != -1 && itemDoctorPosition != -1 && quantityDist != null && quantityDist > 0) {

            detailsOfIstributedInventory = InventorySubmitted(
                productSorted[itemProduct].productId,
                productSorted[itemProduct].batch,
                dataDoctors[itemDoctorPosition].id,
                quantityDist,
                handedOverTo,
                remarkProductDist
            )

            Log.d(TAG, "onViewCreated: ${products.sortedBy { it.product }[itemProduct].productId.toString()}")
            Log.d(TAG, "onViewCreated: ${dataDoctors[itemDoctorPosition].name.toString()}")


            val gson = Gson()
            val jsonString = gson.toJson(detailsOfIstributedInventory)


            val mediaType = "application/json".toMediaType()
            //val body = "{\r\n    \"productId\": ${},\r\n    \"batch\": \"${}\",\r\n    \"doctorId\": ${},\r\n    \"quantity\": ${},\r\n    \"person\": \"${}\",\r\n    \"remark:\"${}\":\r\n}".toRequestBody(mediaType)
            val body = jsonString.toRequestBody(mediaType)

            if (ReceiverMediator.USER_TOKEN.length > 0) {
                val client = OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url("http://65.0.61.137/api/samples")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: java.io.IOException) {
                        submitInventoryButton.isClickable = true
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, "Error: ${e.toString()}", Toast.LENGTH_LONG).show()
                        }
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) {
                                responBody = response.body?.string() ?: ""
                                Log.d(TAG, "onResponse: failure ${responBody}")
                                submitInventoryButton.isClickable = true
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(context, "Error: ${response.toString()}", Toast.LENGTH_LONG).show()
                                }

                            } else {
                                responBody = response.body?.string() ?: ""
                                // println(responBody)
                                //addDataToSpinner(responBody)
                                Log.d(TAG, "onResponse:  success ${responBody}")
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(context, "Successfully submitted", Toast.LENGTH_LONG).show()
                                }
                                var bundle = Bundle().apply{
                                    putInt(INVENTORY_UPDATE_FRAGMENT_DISMISS, 1)
                                }
                                setFragmentResult(INVENTORY_UPDATE_FRAGMENT,bundle)                            }
                        }
                    }
                })
            }
        } else {
            Log.d(TAG, "pushInventoryDistributionDataToServer: Check input params")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "Enter all the parameters", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val TAG = "InventoryUpdateFragment"
    }
}

data class InventorySubmitted(
    val productId: Int? = 0,
    val batch: String? = " ",
    val doctorId: Int? = 0,
    val quantity: Int? = 0,
    val person: String? = " ",
    val remark: String? = " "
) {

}