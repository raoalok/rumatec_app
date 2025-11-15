package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripTourPlanInstituteSelection

class TripAddFormInstituteAdapter(private val mList1: List<TripTourPlanInstituteSelection>): RecyclerView.Adapter<TripAddFormInstituteAdapter.ViewHolder>() {

    var onItemClick: ((TripAddFormInstituteAdapter) -> Unit)? = null
    private var mList: List<TripTourPlanInstituteSelection> = mList1 as List<TripTourPlanInstituteSelection>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.update_tour_plan_update_institute_card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val TripPlanEditViewModel = mList[position]
        holder.productId.text = TripPlanEditViewModel.name.toString()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun filterList(filterList: List<TripTourPlanInstituteSelection>) {
        mList = filterList
        notifyDataSetChanged()
    }

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val productId: TextView = itemView.findViewById(R.id.update_tour_plan_update_institute_name_field)

        init {
            itemView.setOnClickListener {
            }
        }
    }
}