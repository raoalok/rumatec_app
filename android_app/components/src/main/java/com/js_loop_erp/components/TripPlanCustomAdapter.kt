package com.js_loop_erp.components

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.fragments.TripPlanRecyclerViewItemClickListenerI
import  com.js_loop_erp.components.fragments.TripPlanViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TripPlanCustomAdapter (private val listener: TripPlanRecyclerViewItemClickListenerI, private var mList1: List <TripPlanViewModel>): RecyclerView.Adapter<TripPlanCustomAdapter.TripPlanCustomAdapterViewHolder>() {

    var onItemClick:((TripPlanViewModel)-> Unit)? = null
    private var mList: ArrayList<TripPlanViewModel> = mList1 as ArrayList<TripPlanViewModel>
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripPlanCustomAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_plan_fragment_card_layout,parent, false)
        return TripPlanCustomAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun filterList(filterlist: ArrayList<TripPlanViewModel>) {
        mList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TripPlanCustomAdapterViewHolder, position: Int) {
        val TripPlanViewModel = mList[position]

        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")  //2027-01-24T06:26:11.962Z
        val date = "<b>Date:</b> ${LocalDateTime.parse(TripPlanViewModel.date, pattern).toLocalDate().toString()}"
        holder.tripPlanDate.text =  Html.fromHtml(date, Html.FROM_HTML_MODE_LEGACY)

        val route = "<b>Route:</b> ${TripPlanViewModel.route}"
        val startPlace = "<b>Start Place:</b> ${TripPlanViewModel.fromArea}"
        val endPlace = "<b>End Place:</b>  ${TripPlanViewModel.toArea}."
        val travelMode = "<b>Travel Mode:</b> ${TripPlanViewModel.mode}"

        holder.tripPlanRoute.text =       Html.fromHtml(route, Html.FROM_HTML_MODE_LEGACY)
        holder.tripPlanStartPlace.text =  Html.fromHtml(startPlace, Html.FROM_HTML_MODE_LEGACY)
        holder.tripPlanEndPlace.text  =   Html.fromHtml(endPlace, Html.FROM_HTML_MODE_LEGACY)
        holder.tripPlanMode.text =        Html.fromHtml(travelMode, Html.FROM_HTML_MODE_LEGACY)

        if(TripPlanViewModel.approveAt != ""  &&  TripPlanViewModel.approveAt != null && TripPlanViewModel.approveBy != ""  &&  TripPlanViewModel.approveBy != null){
            holder.tripPlanEditButton.visibility = View.INVISIBLE
            holder.tripPlanDeleteButton.visibility = View.INVISIBLE
        }

    }

    inner class TripPlanCustomAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tripPlanDate:           TextView = itemView.findViewById(R.id.trip_plan_date)
        val tripPlanRoute:          TextView = itemView.findViewById(R.id.trip_plan_route)
        val tripPlanStartPlace:     TextView = itemView.findViewById(R.id.trip_plan_start_place)
        val tripPlanEndPlace:       TextView = itemView.findViewById(R.id.trip_plan_end_place)
        val tripPlanMode:           TextView = itemView.findViewById(R.id.trip_travel_mode)

        val tripPlanEditButton:     AppCompatImageView = itemView.findViewById(R.id.trip_plan_edit_button)
        val tripPlanDeleteButton:   AppCompatImageView = itemView.findViewById(R.id.trip_plan_delete_button)
        val tripPlanInfoButton:   AppCompatImageView = itemView.findViewById(R.id.trip_plan_info_button)

        init{

            tripPlanEditButton.setOnClickListener {
                listener.onItemClick(1, mList[adapterPosition].id!!.toInt())
            }


            tripPlanDeleteButton.setOnClickListener {
                listener.onItemClick(2, mList[adapterPosition].id!!.toInt())
            }

            tripPlanInfoButton.setOnClickListener{
                listener.onItemClick(3, mList[adapterPosition].id!!.toInt())
            }

            itemView.setOnClickListener{
            }
        }

    }
}