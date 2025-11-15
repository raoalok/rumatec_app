package com.js_loop_erp.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import  com.js_loop_erp.components.data_class.ProductList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProductListCustomAdapter (private var mList: List <ProductList>): RecyclerView.Adapter<ProductListCustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_list_fragment_card_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount():Int {
        return mList.size
    }

    fun filterList(filterList: ArrayList<ProductList>){
        mList = filterList
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val productList = mList[position]
        holder.productName.text = productList.name
        holder.productHsn.text = "HSN: ${productList.hsn.toString()}"
        holder.productStock.text = "Stock: ${productList.maximumStock.toString()}"

        Glide.with(holder.itemView.context)
            .load(/*"https://picsum.photos/200/300"*/ R.drawable.logo_rumatec_vetcare)
            //.placeholder(R.drawable.ic_dashboard_black_24dp)
            .placeholder(R.drawable.logo_rumatec_vetcare)
            //.error(R.drawable.error_placeholder)
            .centerCrop()
            .into(holder.productImage)

    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView){
        val productName: TextView = itemView.findViewById(R.id.product_list_fragment_product_name)
        val productHsn: TextView = itemView.findViewById(R.id.product_list_fragment_product_hsn)
        val productStock: TextView = itemView.findViewById(R.id.product_list_fragment_product_stock)
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
    }

}