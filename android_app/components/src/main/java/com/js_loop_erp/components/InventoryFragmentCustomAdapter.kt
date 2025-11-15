package com.js_loop_erp.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.fragments.ItemsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InventoryFragmentCustomAdapter(private var mList: List <ItemsViewModel>): RecyclerView.Adapter<InventoryFragmentCustomAdapter.InventoryFragmentCustomAdapterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InventoryFragmentCustomAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventory_fragment_card_layout,parent, false)
        return InventoryFragmentCustomAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun filterList(filterlist: ArrayList<ItemsViewModel>) {
         mList = filterlist
        notifyDataSetChanged()
    }



    override fun onBindViewHolder(holder: InventoryFragmentCustomAdapterViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.productName.text = ItemsViewModel.product
        //holder.productHsn.text = "HSN: " + ItemsViewModel.hsn
        holder.productStock.text = "No: " + ItemsViewModel.stock.toString()
        //holder.productId.text = "Id: " + ItemsViewModel.productId.toString()
        holder.productBatch.text = "Batch: " + ItemsViewModel.batch
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")  //2027-01-24T06:26:11.962Z
        if(ItemsViewModel.expDate != null) {
            holder.productExpDate.text =
                "Exp Date: " + LocalDateTime.parse(ItemsViewModel.expDate, pattern).month.toString()
                    .substring(0, 3) + " " + LocalDateTime.parse(
                    ItemsViewModel.expDate,
                    pattern
                ).year.toString()
        }
    }

    class InventoryFragmentCustomAdapterViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val productName: TextView = itemView.findViewById(R.id.inventory_fragment_product_name)
        //val productHsn: TextView = itemView.findViewById(R.id.inventory_fragment_product_hsn)
        val productStock: TextView = itemView.findViewById(R.id.inventory_fragment_product_stock)
//        val productId: TextView = itemView.findViewById(R.id.inventory_fragment_product_id)
        val productExpDate: TextView = itemView.findViewById(R.id.inventory_fragment_exp_date)
        val productBatch: TextView = itemView.findViewById(R.id.inventory_fragment_product_batch)
    }
}