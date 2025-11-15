package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanCompanion
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourVisitDoctorListInit


class TripSubmitCompanionAdapter (private val mList1: List<TripSubmitReportSelectedTourPlanCompanion>) : RecyclerView.Adapter<TripSubmitCompanionAdapter.ViewHolder>() {

    var onItemClick:((TourVisitDoctorListInit)-> Unit)? = null
    private var mList: List<TripSubmitReportSelectedTourPlanCompanion> = mList1 as List<TripSubmitReportSelectedTourPlanCompanion>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.update_tour_plan_update_doctor_card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val TripPlanEditViewModel = mList[position]
        holder.productId.text = TripPlanEditViewModel.name.toString()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun filterList(filterList: List<TripSubmitReportSelectedTourPlanCompanion>?){
        mList = filterList ?: emptyList<TripSubmitReportSelectedTourPlanCompanion>()
        notifyDataSetChanged()
    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val productId: TextView = itemView.findViewById(R.id.update_tour_plan_update_doctor_name_field)

        init{
            itemView.setOnClickListener{
            }
        }
    }
}