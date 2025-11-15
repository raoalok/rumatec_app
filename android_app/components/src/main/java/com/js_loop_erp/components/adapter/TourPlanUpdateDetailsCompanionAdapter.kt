package com.js_loop_erp.components.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TourPlanUpdateDoctor
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanCompanion
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdateDetailsCompanion
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdateDetailsCompanionItemClickListenerI
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdateDetailsDoctor
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdateDetailsProductItemClickListenerI
import kotlin.math.log

class TourPlanUpdateDetailsCompanionAdapter (private val listener: TourPlanUpdateDetailsCompanionItemClickListenerI, private var mList1: ArrayList<TripSubmitReportSelectedTourPlanCompanion>): RecyclerView.Adapter<TourPlanUpdateDetailsCompanionAdapter.ViewHolder>() {

    var onItemClick: ((TourPlanUpdateDetailsCompanion) -> Unit)? = null

    private var mList: ArrayList<TripSubmitReportSelectedTourPlanCompanion> = mList1 as ArrayList<TripSubmitReportSelectedTourPlanCompanion>

    private val checkedList: MutableList<TripSubmitReportSelectedTourPlanCompanion> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourPlanUpdateDetailsCompanionAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.tour_plan_update_companion_card_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.productName.text = ItemsViewModel.name
        holder.checkButton.isChecked = ItemsViewModel.isSelected == true
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun getCheckedItems(): List<TripSubmitReportSelectedTourPlanCompanion> {
        return mList
    }

    fun filterList(filterList: ArrayList<TripSubmitReportSelectedTourPlanCompanion>){
        mList = filterList
        notifyDataSetChanged()
    }

    fun filterList(position: Int) {
        notifyItemChanged(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tour_plan_update_name_field)
        val checkButton: AppCompatCheckBox = itemView.findViewById(R.id.tour_plan_update_check_field)


        init {
            checkButton.setOnClickListener {
            }

            checkButton.setOnCheckedChangeListener { buttonView, isChecked ->
                //Log.d("TAG", "addListToView: ${mList[adapterPosition].name} ")
                if (isChecked) {
                    mList[adapterPosition].isSelected = true
                    listener.onItemSelected(mList[adapterPosition], true)
                } else {
                    mList[adapterPosition].isSelected = false
                    listener.onItemSelected(mList[adapterPosition], false)
                }
            }

            itemView.setOnClickListener {
                if (mList[adapterPosition].isSelected == false) {
                    mList[adapterPosition].companionId?.let { it1 ->
                        mList[adapterPosition].isSelected = true
                        listener.onItemSelected(mList[adapterPosition], true)
                        filterList(adapterPosition)
                    }
                } else {
                    mList[adapterPosition].companionId?.let { it1 ->
                        mList[adapterPosition].isSelected = false
                        listener.onItemSelected(mList[adapterPosition], false)
                        filterList(adapterPosition)
                    }
                }
            }
        }
    }
}