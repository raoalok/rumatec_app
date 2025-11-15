package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripTourPlanChemistSelection

class TripAddFormChemistAdapter(private val mList1: List<TripTourPlanChemistSelection>): RecyclerView.Adapter<TripAddFormChemistAdapter.ViewHolder>() {


    var onItemClick: ((TripAddFormChemistAdapter) -> Unit)? = null
    private var mList: List<TripTourPlanChemistSelection> = mList1 as List<TripTourPlanChemistSelection>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.update_tour_plan_update_chemist_card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val TripPlanEditViewModel = mList[position]
        holder.productId.text = TripPlanEditViewModel.name.toString()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun filterList(filterList: List<TripTourPlanChemistSelection>) {
        mList = filterList
        notifyDataSetChanged()
    }

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val productId: TextView = itemView.findViewById(R.id.update_tour_plan_update_chemist_name_field)

        init {
            itemView.setOnClickListener {
            }
        }
    }
}