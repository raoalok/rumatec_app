package com.js_loop_erp.components.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.MainActivity
import  com.js_loop_erp.components.R
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.util.ArrayList

class TripPlanEditAdapter(private val listener: TripPlanEditItemClickListenerI, private val mList1: List<TripPlanEditViewModel>) : RecyclerView.Adapter<TripPlanEditAdapter.ViewHolder>() {
    
    var onItemClick:((TripPlanEditFragment)-> Unit)? = null
    private var mList: ArrayList<TripPlanEditViewModel> = mList1 as ArrayList<TripPlanEditViewModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_plan_edit_card_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val TripPlanEditViewModel = mList[position]

        holder.productId.text = TripPlanEditViewModel.name.toString()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun filterList(filterList: ArrayList<TripPlanEditViewModel>){
        mList = filterList
        notifyDataSetChanged()
    }

    fun deleteItem(index: Int, list: List<TripPlanEditViewModel>){
        list[index].id?.let {
            removeElementFromServer(it)
        }
    }

    fun removeElementFromServer(position: Int){

        var returnServerStatus: Boolean
        var responBody: String = " "
        val mediaType = "text/plain".toMediaType()
        val body = "".toRequestBody(mediaType)

        if(ReceiverMediator.USER_TOKEN.length > 8){
            val client = OkHttpClient.Builder()
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20,java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                //.url("http://65.0.61.137/api/inventory/sample")
                //.url("http://65.0.61.137/api/expenses/agent")
                .url("http://65.0.61.137/api/expenses/${position}")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException){
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use{
                        if(!response.isSuccessful){

                        } else {
                            responBody = response.body?.string() ?:""
                            //println(responBody)
                        }
                    }
                }
            })
        }
    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val productId: TextView = itemView.findViewById(R.id.expense_product_id)
        val productBatch: TextView = itemView.findViewById(R.id.expense_product_batch)
        val expenseDeleteButton: ImageButton = itemView.findViewById(R.id.trip_edit_select)

        init{
            expenseDeleteButton.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
            itemView.setOnClickListener{

            }
        }
    }
}
