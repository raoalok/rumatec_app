package com.js_loop_erp.components.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.TripPlanCustomAdapter
import  com.js_loop_erp.components.fragments.TripPlanRecyclerViewItemClickListenerI
import  com.js_loop_erp.components.fragments.TripTourViewModel
import  com.js_loop_erp.components.fragments.tripReportSubmit.TripTourRecyclerViewItemClickListenerI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TripTourUpdateCustomAdapter (private val listener: TripTourRecyclerViewItemClickListenerI, private var mList1: List <TripTourViewModel>): RecyclerView.Adapter<TripTourUpdateCustomAdapter.TripTourCustomAdapterViewHolder>() {

    var onItemClick:((TripTourViewModel)-> Unit)? = null
    private var mList: ArrayList<TripTourViewModel> = mList1 as ArrayList<TripTourViewModel>
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripTourCustomAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_tour_report_fragment_card_layout,parent, false)
        return TripTourCustomAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun filterList(filterlist: ArrayList<TripTourViewModel>) {
        mList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TripTourCustomAdapterViewHolder, position: Int) {
        val TripTourViewModel = mList[position]

        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")  //2027-01-24T06:26:11.962Z
        val date = "<b>Date:</b> ${LocalDateTime.parse(TripTourViewModel.date, pattern).toLocalDate().toString()}"
        holder.tripPlanDate.text =  Html.fromHtml(date, Html.FROM_HTML_MODE_LEGACY)

        val route = "<b>Route:</b> ${TripTourViewModel.route}"
        val startPlace = "<b>Start Place:</b> ${TripTourViewModel.fromArea}"
        val endPlace = "<b>End Place:</b>  ${TripTourViewModel.toArea}."
        val travelMode = "<b>Travel Mode:</b> ${TripTourViewModel.mode}"

        holder.tripPlanRoute.text =       Html.fromHtml(route, Html.FROM_HTML_MODE_LEGACY)
        holder.tripPlanStartPlace.text =  Html.fromHtml(startPlace, Html.FROM_HTML_MODE_LEGACY)
        holder.tripPlanEndPlace.text  =   Html.fromHtml(endPlace, Html.FROM_HTML_MODE_LEGACY)
        holder.tripPlanMode.text =        Html.fromHtml(travelMode, Html.FROM_HTML_MODE_LEGACY)

        if(TripTourViewModel.approveAt != ""  &&  TripTourViewModel.approveAt != null && TripTourViewModel.approveBy != ""  &&  TripTourViewModel.approveBy != null){
            holder.tripPlanEditButton.visibility = View.INVISIBLE
            holder.tripPlanDeleteButton.visibility = View.INVISIBLE
        }

    }

    inner class TripTourCustomAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tripPlanDate: TextView = itemView.findViewById(R.id.trip_tour_date)
        val tripPlanRoute: TextView = itemView.findViewById(R.id.trip_tour_route)
        val tripPlanStartPlace: TextView = itemView.findViewById(R.id.trip_tour_start_place)
        val tripPlanEndPlace: TextView = itemView.findViewById(R.id.trip_tour_end_place)
        val tripPlanMode: TextView = itemView.findViewById(R.id.trip_travel_mode)

        val tripPlanEditButton: AppCompatImageView = itemView.findViewById(R.id.trip_tour_edit_button)
        val tripPlanDeleteButton: AppCompatImageView = itemView.findViewById(R.id.trip_tour_delete_button)

        init{

            tripPlanEditButton.setOnClickListener {
                listener.onItemClick(1, mList[adapterPosition].id!!.toInt())
            }

            tripPlanDeleteButton.setOnClickListener {
                listener.onItemClick(2, mList[adapterPosition].id!!.toInt())
            }

            itemView.setOnLongClickListener {
                listener.onItemClick(3, mList[adapterPosition].id!!.toInt())
                true
            }

            itemView.setOnClickListener{
            }
        }

    }
}