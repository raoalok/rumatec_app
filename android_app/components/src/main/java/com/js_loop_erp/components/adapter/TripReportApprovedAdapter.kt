package com.js_loop_erp.components.adapter

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripReportApprovedModel
import  com.js_loop_erp.components.fragments.InventoryEditViewModel
import  com.js_loop_erp.components.fragments.TripReportApprovedI
import  com.js_loop_erp.components.fragments.TripReportFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TripReportApprovedAdapter(private val listener: TripReportApprovedI, private val mList1: List<TripReportApprovedModel>): RecyclerView.Adapter<TripReportApprovedAdapter.ViewHolder>() {

    var onItemClick: ((TripReportFragment)->Unit)?  = null

    private val mList: ArrayList<TripReportApprovedModel> = mList1 as ArrayList<TripReportApprovedModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_report_card_layout, parent,false)
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
        val travelMode = "<b>Travel Mode:</b> ${ItemsViewModel.mode}"

        holder.tripPlanRoute.text =       Html.fromHtml(route, Html.FROM_HTML_MODE_LEGACY)
        holder.tripPlanStartPlace.text =  Html.fromHtml(startPlace, Html.FROM_HTML_MODE_LEGACY)
        holder.tripPlanEndPlace.text  =   Html.fromHtml(endPlace, Html.FROM_HTML_MODE_LEGACY)
        holder.tripPlanMode.text =        Html.fromHtml(travelMode, Html.FROM_HTML_MODE_LEGACY)

        if(ItemsViewModel.approveAt != ""  &&  ItemsViewModel.approveAt != null && ItemsViewModel.approveBy != ""  &&  ItemsViewModel.approveBy != null){
            holder.tripPlanEditButton.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

/*    fun deleteItem(index: Int, list: List<TripReportApprovedModel>){
        Log.d("SubmittedInventoryCheckEditFragment", "onViewCreated: ${mList[index].productId}  ${mList[index].product}  ${mList[index].id}  ")
        notifyItemRemoved(index)
        mList.removeAt(index)
    }*/

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tripPlanDate:           TextView = itemView.findViewById(R.id.trip_report_date)
        val tripPlanRoute:          TextView = itemView.findViewById(R.id.trip_report_route)
        val tripPlanStartPlace:     TextView = itemView.findViewById(R.id.trip_report_start_place)
        val tripPlanEndPlace:       TextView = itemView.findViewById(R.id.trip_report_end_place)
        val tripPlanMode:           TextView = itemView.findViewById(R.id.trip_travel_mode)

        val tripPlanEditButton: AppCompatImageView = itemView.findViewById(R.id.trip_report_edit_button)
        init {
            tripPlanEditButton.setOnClickListener{
//                Toast.makeText(this@ViewHolder,"Hixx", Toast.LENGTH_LONG).show()
                //mList[adapterPosition].id?.let{it1 -> listener.onItemClick(it1)}
            }
            itemView.setOnClickListener {
                listener.onItemClick(mList[adapterPosition])
            }
        }
    }
}