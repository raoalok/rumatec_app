package com.js_loop_erp.components.adapter

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanInstitute
import  com.js_loop_erp.components.fragments.access_controlled.TripApproveFragment
import  com.js_loop_erp.components.fragments.access_controlled.TripApproveI
import  com.js_loop_erp.components.fragments.access_controlled.TripApproveModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.log

class TripApproveAdapter (private val listener: TripApproveI, private val mList1: List<TripApproveModel>): RecyclerView.Adapter<TripApproveAdapter.ViewHolder>() {

    var onItemClick: ((TripApproveFragment)->Unit)?  = null

    private var mList: ArrayList<TripApproveModel> = mList1 as ArrayList<TripApproveModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_approve_reject_tour_plan, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position : Int){

        val ItemsViewModel = mList[position]

        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")  //2027-01-24T06:26:11.962Z
        val date = "<b>Date:</b> ${LocalDateTime.parse(ItemsViewModel.date, pattern).toLocalDate().toString()}"
        holder.tripPlanDate.text =  Html.fromHtml(date, Html.FROM_HTML_MODE_LEGACY)

        val route = "<b>Route:</b> ${ItemsViewModel.route}"
        val startPlace = "<b>Start Place:</b> ${ItemsViewModel.fromArea}"
        val endPlace = "<b>End Place:</b>  ${ItemsViewModel.toArea}."
        val travelMode = "<b>Travel Mode:</b>  ${ItemsViewModel.mode}"

        holder.tripPlanStartPlace.text =  Html.fromHtml(startPlace, Html.FROM_HTML_MODE_LEGACY)
        holder.tripPlanEndPlace.text  =   Html.fromHtml(endPlace, Html.FROM_HTML_MODE_LEGACY)
        holder.tripPlanMode.text =        Html.fromHtml(travelMode, Html.FROM_HTML_MODE_LEGACY)

        holder.tripPlanEditButton.isChecked = ItemsViewModel.isChecked ?: false

        /*if(ItemsViewModel.approveAt != ""  &&  ItemsViewModel.approveAt != null && ItemsViewModel.approveBy != ""  &&  ItemsViewModel.approveBy != null){
            holder.tripPlanEditButton.visibility = View.VISIBLE
        }*/

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun filterList(filterList: ArrayList<TripApproveModel>, position: Int) {
        notifyItemChanged(position)
    }

    fun getList(): ArrayList<TripApproveModel>{
        return mList1 as ArrayList<TripApproveModel>
    }


    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tripPlanDate: TextView = itemView.findViewById(R.id.tour_approve_visit_date)
        val tripPlanStartPlace: TextView = itemView.findViewById(R.id.tour_approve_start_place)
        val tripPlanEndPlace: TextView = itemView.findViewById(R.id.tour_approve_end_place)
        val tripPlanMode: TextView = itemView.findViewById(R.id.tour_approve_travel_mode)

        val tripPlanEditButton: AppCompatCheckBox = itemView.findViewById(R.id.tour_plan_update_check_field)
        init {
            tripPlanEditButton.setOnClickListener{
                if(mList[adapterPosition].isChecked == true){
                    mList[adapterPosition].isChecked = false
                    filterList(mList,adapterPosition)
                } else {
                    mList[adapterPosition].isChecked = true
                    filterList(mList,adapterPosition)
                }
                listener.onItemClick(mList1 as ArrayList<TripApproveModel>)
            }

            itemView.setOnClickListener {
                if(mList[adapterPosition].isChecked == true){
                    mList[adapterPosition].isChecked = false
                    filterList(mList, adapterPosition)
                } else {
                    mList[adapterPosition].isChecked = true
                    filterList(mList, adapterPosition)
                }
                listener.onItemClick(mList1 as ArrayList<TripApproveModel>)
            }

/*            tripPlanEditButton.setOnClickListener{
                itemView.setOnClickListener {
                    listener.onItemClick(mList1 as ArrayList<TripApproveModel>)
                    if(mList[adapterPosition].isChecked == true){
                        mList[adapterPosition].isChecked = false
                        filterList(mList, adapterPosition)
                    } else {
                        mList[adapterPosition].isChecked = true
                        filterList(mList, adapterPosition)
                    }
                }
            }*/

        }
    }

    companion object {
        val TAG = TripApproveAdapter::class.java.name
    }
}