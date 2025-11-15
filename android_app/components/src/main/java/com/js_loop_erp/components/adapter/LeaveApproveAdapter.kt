package com.js_loop_erp.components.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.fragments.access_controlled.LeaveApproveReject
import  com.js_loop_erp.components.fragments.access_controlled.LeaveApproveRejectI
import  com.js_loop_erp.components.fragments.access_controlled.LeaveApproveModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LeaveApproveAdapter (private val listener: LeaveApproveRejectI, private val mList1: List<LeaveApproveModel>): RecyclerView.Adapter<LeaveApproveAdapter.ViewHolder>() {

    var onItemClick: ((LeaveApproveReject)->Unit)?  = null

    private val mList: ArrayList<LeaveApproveModel> = mList1 as ArrayList<LeaveApproveModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.leave_approve_reject_card_layout, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position : Int){

        val ItemsViewModel = mList[position]

        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")  //2027-01-24T06:26:11.962Z
        val date = "<b>Date:</b> ${LocalDateTime.parse(ItemsViewModel.date, pattern).toLocalDate().toString()}"
        holder.tripPlanDate.text =  Html.fromHtml(date, Html.FROM_HTML_MODE_LEGACY)

        val route = "<b>Route:</b> " //${ItemsViewModel.route}"
        val startPlace = "<b>Start Place:</b> " //${ItemsViewModel.fromArea}"
        val endPlace = "<b>End Place:</b> " // ${ItemsViewModel.toArea}."
        val travelMode = "<b>Travel Mode:</b> " // ${ItemsViewModel.mode}"

        holder.tripPlanStartPlace.text =  Html.fromHtml(startPlace, Html.FROM_HTML_MODE_LEGACY)

        if(ItemsViewModel.approveAt != ""  &&  ItemsViewModel.approveAt != null && ItemsViewModel.approveBy != ""  &&  ItemsViewModel.approveBy != null){
            holder.tripPlanEditButton.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tripPlanDate: TextView = itemView.findViewById(R.id.leave_approve_date)
        val tripPlanStartPlace: TextView = itemView.findViewById(R.id.leave_approve_type)

        val tripPlanEditButton: AppCompatCheckBox = itemView.findViewById(R.id.leave_approve_list_check_field)
        init {
            tripPlanEditButton.setOnClickListener{
            }
            itemView.setOnClickListener {
                //listener.onItemClick(mList[adapterPosition])
            }
        }
    }
}