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
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import kotlin.random.Random

class ExpenseEditAdapter(private val listener: RecyclerViewItemClickListener, private val mList1: List<ExpenseItemUpdated>): RecyclerView.Adapter<ExpenseEditAdapter.ViewHolder>() {
    var onItemClick:((ExpenseEditFragment)-> Unit)? = null
    private val mList: ArrayList<ExpenseItemUpdated> = mList1 as ArrayList<ExpenseItemUpdated>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.edit_expense_card_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ExpenseItemUpdated = mList[position]

        holder.productId.text = ExpenseItemUpdated.description.toString()
//        holder.productSalesPersonId.text = ExpenseItemUpdated.salesPersonId.toString()
        holder.productBatch.text = "₹: " + ExpenseItemUpdated.amount.toString()

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun deleteItem(index: Int, list: List<ExpenseItemUpdated>){
        /*notifyItemRemoved(index)
        mList.removeAt(index)*/
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

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val productId: TextView = itemView.findViewById(R.id.expense_product_id)
//        val productSalesPersonId: TextView = itemView.findViewById(R.id.expense_sales_person_id)
        val productBatch: TextView = itemView.findViewById(R.id.expense_product_batch)
        val expenseDeleteButton: ImageButton = itemView.findViewById(R.id.expense_button_delete)

        init{
            expenseDeleteButton.setOnClickListener{
                //deleteItem(adapterPosition, mList)
                //mList[adapterPosition].id?.let { it1 -> ExpenseEditFragment().deleteFromServer(it1, adapterPosition) }
                mList[adapterPosition].id?.let { it1 -> listener.onItemClick(it1) }
            }
        }
    }
}