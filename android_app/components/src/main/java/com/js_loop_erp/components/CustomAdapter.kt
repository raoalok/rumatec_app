package com.js_loop_erp.components

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.fragments.ItemsViewModel

class CustomAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventory_card_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        holder.productName.text = ItemsViewModel.product
        holder.productHsn.text = "HSN: " + ItemsViewModel.hsn
        holder.productStock.text ="No: "+ ItemsViewModel.stock.toString()
        holder.productId.text = "Id: " +ItemsViewModel.productId.toString()
        holder.productBatch.text = "Batch: " + ItemsViewModel.batch
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productHsn: TextView = itemView.findViewById(R.id.product_hsn)
        val productStock: TextView = itemView.findViewById(R.id.product_stock)
        val productId: TextView = itemView.findViewById(R.id.product_id)
        val productBatch: TextView = itemView.findViewById(R.id.product_batch)

    }
}
