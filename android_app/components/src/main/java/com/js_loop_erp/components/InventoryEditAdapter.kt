package com.js_loop_erp.components

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.fragments.InventoryEditViewModel
import  com.js_loop_erp.components.fragments.SubmittedInventoryCheckEditFragment
import  com.js_loop_erp.components.fragments.SubmittedInventoryRecyclerViewItemClickListenerI
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.notify
import okio.IOException

class InventoryEditAdapter(private val listener: SubmittedInventoryRecyclerViewItemClickListenerI, private val mList1: List<InventoryEditViewModel>) : RecyclerView.Adapter<InventoryEditAdapter.ViewHolder>(){

    var onItemClick: ((SubmittedInventoryCheckEditFragment) -> Unit)? = null
    // create new views
    private val mList: ArrayList<InventoryEditViewModel> = mList1 as ArrayList<InventoryEditViewModel>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.edit_inventory_card_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        holder.productName.text = ItemsViewModel.product
//        holder.productHsn.text = "HSN: " + ItemsViewModel.hsn
        holder.productStock.text ="No: "+ ItemsViewModel.quantity.toString()
        holder.productId.text = "Id: " +ItemsViewModel.productId.toString()
        holder.productBatch.text = "Batch: " + ItemsViewModel.batch
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun deleteItem(index: Int, list: List<InventoryEditViewModel>){
        Log.d("SubmittedInventoryCheckEditFragment", "onViewCreated: ${mList[index].productId}  ${mList[index].product}  ${mList[index].id}  ")
        notifyItemRemoved(index)
        mList.removeAt(index)
    }

    fun removeElementFromServer(){
        var responBody: String = " "
        val client = OkHttpClient.Builder()
            .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(20,java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("http://65.0.61.137/api/inventory/sample")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
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
                        // println(responBody)
                        //addListToView(responBody)
                    }
                }
            }
        })
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        //        val productHsn: TextView = itemView.findViewById(R.id.product_hsn)
        val productStock: TextView = itemView.findViewById(R.id.product_stock)
        val productId: TextView = itemView.findViewById(R.id.product_id)
        val productBatch: TextView = itemView.findViewById(R.id.product_batch)
        val deleteButton: ImageButton = itemView.findViewById(R.id.button_delete)

        init {
            deleteButton.setOnClickListener{
//                Toast.makeText(this@ViewHolder,"Hixx", Toast.LENGTH_LONG).show()

                mList[adapterPosition].id?.let{it1 -> listener.onItemClick(it1)}
            }
            itemView.setOnClickListener {

            }
        }
    }
}