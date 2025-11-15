package com.js_loop_erp.components.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R

class PresentScheduleAdapter(private val mList: List<PresentScheduleViewModel>): RecyclerView.Adapter<PresentScheduleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PresentScheduleAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.present_schedule_card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.productName.text = ItemsViewModel.product
        holder.productHsn.text = "HSN: " + ItemsViewModel.hsn
        holder.productStock.text ="No: "+ ItemsViewModel.stock.toString()
        holder.productId.text = "Id: " +ItemsViewModel.productId.toString()
        holder.productBatch.text = "Batch: " + ItemsViewModel.batch
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productHsn: TextView = itemView.findViewById(R.id.product_hsn)
        val productStock: TextView = itemView.findViewById(R.id.product_stock)
        val productId: TextView = itemView.findViewById(R.id.product_id)
        val productBatch: TextView = itemView.findViewById(R.id.product_batch)
    }
}