package com.js_loop_erp.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.fragments.CnfViewModel

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CnfInventoryCustomAdapter(private var mList: List <CnfViewModel>): RecyclerView.Adapter<CnfInventoryCustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cnf_fragment_card_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount():Int {
        return mList.size
    }

    fun filterList(filterList: ArrayList<CnfViewModel>){
        mList = filterList
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val CnfViewModel = mList[position]
        holder.productName.text = CnfViewModel.product
        holder.cnfFragmentCnfname.text = CnfViewModel.cnf
        //holder.productHsn.text = "Date: " + CnfViewModel.hsn
        holder.productStock.text = "Stock: " + CnfViewModel.stock.toString()
        holder.productBatch.text = "Batch: " + CnfViewModel.batch
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")  //2027-01-24T06:26:11.962Z
        if(CnfViewModel.expDate != null) {
            holder.cnfExpiryDate.text =
                "Exp Date: " + LocalDateTime.parse(CnfViewModel.expDate, pattern).month.toString()
                    .substring(0, 3) + " " + LocalDateTime.parse(
                    CnfViewModel.expDate,
                    pattern
                ).year.toString()
        }
    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView){
        val productName: TextView = itemView.findViewById(R.id.cnf_fragment_product_name)
        //val productHsn: TextView = itemView.findViewById(R.id.cnf_fragment_product_hsn)
        val productStock: TextView = itemView.findViewById(R.id.cnf_fragment_product_stock)
        val productBatch: TextView = itemView.findViewById(R.id.cnf_fragment_product_batch)
        val cnfExpiryDate: TextView = itemView.findViewById(R.id.cnf_fragment_exp_date)
        val cnfFragmentCnfname: TextView = itemView.findViewById(R.id.cnf_fragment_cnf_name)
    }

}