package com.js_loop_erp.components

import android.content.ClipData.Item
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.fragments.SaleInvoiceFragment
import  com.js_loop_erp.components.fragments.SaleInvoiceRecyclerViewItemClickListenerI
import  com.js_loop_erp.components.fragments.SaleInvoiceViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri

class SaleInvoiceCustomAdapter(private val listener: SaleInvoiceRecyclerViewItemClickListenerI, private var mList: List<SaleInvoiceViewModel>): RecyclerView.Adapter<SaleInvoiceCustomAdapter.ViewHolder>() {

    private val mListOnClick: ArrayList<SaleInvoiceViewModel> = mList as ArrayList<SaleInvoiceViewModel>

    private var fragmentActivityI:FragmentActivityI? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
        ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sale_invoice_fragment_card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount():Int {
        return mList.size
    }

    fun filterList(filterList: ArrayList<SaleInvoiceViewModel>){
        mList = filterList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int){
        val SaleInvoiceViewModel = mList[position]
        holder.productSale.text = SaleInvoiceViewModel.saleNo
        holder.productParty.text = "Party: " + SaleInvoiceViewModel.party
        holder.productCnf.text = "CNF: " + SaleInvoiceViewModel.cnf.toString()
        holder.productTotal.text = "\u20B9: " + SaleInvoiceViewModel.total
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")  //2027-01-24T06:26:11.962Z
        if (SaleInvoiceViewModel.saleDate != null) {
            holder.productDate.text =
                buildString {
                    append(
                        LocalDateTime.parse(SaleInvoiceViewModel.saleDate, pattern).month.toString()
                            .substring(0, 3)
                    )
                    append(" ")
                    append(
                        LocalDateTime.parse(
                            SaleInvoiceViewModel.saleDate,
                            pattern
                        ).year.toString()
                    )
                }
        }
    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView){
        val productSale: TextView = itemView.findViewById(R.id.sale_invoice_fragment_product_sale_no)
        val productParty: TextView = itemView.findViewById(R.id.sale_invoice_fragment_product_party)
        val productCnf: TextView = itemView.findViewById(R.id.sale_invoice_fragment_product_cnf)
        val productTotal: TextView = itemView.findViewById(R.id.sale_invoice_fragment_product_total)
        val productDate: TextView = itemView.findViewById(R.id.sale_invoice_fragment_product_date)
        val productDownLoad: ImageButton = itemView.findViewById(R.id.sale_invoice_fragment_product_download)


        init {
            productSale.setOnClickListener{
                // Toast.makeText(this@ViewHolder,"Hixx", Toast.LENGTH_LONG).show()
                // Log.d("SaleInvoiceCustomAdapter", "onViewCreated:")
                // downloadItem(adapterPosition, mListOnClick)
            }
            productDownLoad.setOnClickListener{
                // Toast.makeText(this@ViewHolder,"Hixx", Toast.LENGTH_LONG).show()
                // Log.d("SaleInvoiceCustomAdapter", "onViewCreated:")
                // downloadItem(adapterPosition, mListOnClick)
                //val uri = Uri.parse( "http://65.0.61.137/api/sales/cnf/${mListOnClick[adapterPosition].id}/pdf")
                //SaleInvoiceFragment().downloadFromServer(uri)
                //MainActivity().showToast()
                //MainActivity().openUri(uri)
                mList[adapterPosition].id?.let { it1 -> listener.onItemClick(it1) }
            }
            itemView.setOnClickListener {
                //downloadItem(adapterPosition, mListOnClick)
            }
        }

    }

}