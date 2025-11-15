package com.js_loop_erp.components.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanInstitute
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanParty
import  com.js_loop_erp.components.fragments.TourPlanUpdateDetailsStockist
import  com.js_loop_erp.components.fragments.TourPlanUpdateStockistDetailsItemClickListenerI
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdateDetailsParty
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdatePartyDetailsItemClickListenerI

class TourPlanUpdateStockistAdapter (private val listener: TourPlanUpdateStockistDetailsItemClickListenerI, private var mList1: ArrayList<TripSubmitReportSelectedTourPlanInstitute>): RecyclerView.Adapter<TourPlanUpdateStockistAdapter.ViewHolder>() {


    var onItemClick: ((TourPlanUpdateDetailsStockist) -> Unit)? = null

    private var mList: ArrayList<TripSubmitReportSelectedTourPlanInstitute> =
        mList1 as ArrayList<TripSubmitReportSelectedTourPlanInstitute>

    private val checkedList: MutableList<TripSubmitReportSelectedTourPlanInstitute> = mutableListOf()
    var selectedPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TourPlanUpdateStockistAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_doctor_visit_submit_card_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        //Log.d("TAG", "filterList: addListToView 6 ${ItemsViewModel.toString()}")

        holder.productName.text = ItemsViewModel.name
        holder.checkButton.isChecked = position == selectedPosition

        holder.checkButton.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun getCheckedItems(): List<TripSubmitReportSelectedTourPlanInstitute> {
        //checkButtin
        //Log.d("TAG", "getCheckedItems1: ${checkedList.size}")
        return mList
    }

    fun getSelected(): TripSubmitReportSelectedTourPlanInstitute? {
        if (selectedPosition >= 0) {
            return mList[selectedPosition]
        } else {
            return null
        }
    }

    fun filterList(filterList: ArrayList<TripSubmitReportSelectedTourPlanInstitute>) {
        //Log.d("TAG", "filterList: addListToView 5 ${filterList[0].name}")
        mList = filterList
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tour_plan_update_name_field)
        val checkButton: AppCompatRadioButton =
            itemView.findViewById(R.id.tour_plan_update_check_field)


        init {
            checkButton.setOnClickListener {
                //mList[adapterPosition].id?.let{it1 -> listener.onItemClick(it1)}

            }
            /*checkButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    checkedList.add(mList[adapterPosition])
                    Log.d("TAG", "getCheckedItems2: ${checkedList.size}")

                } else {
                    val iterator = checkedList.iterator()
                    while (iterator.hasNext()) {
                        val item = iterator.next()
                        if (item == mList[adapterPosition]) {
                            iterator.remove()
                            mList[adapterPosition].id?.let{it1 -> listener.onItemClick(it1)}
                        }
                    }
                }
            }*/

            checkButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    /* mList[adapterPosition].id?.let { it1 ->
                         listener.onItemSelected(mList[adapterPosition], true)
                     }*/
                } else {
                    /*mList[adapterPosition].id?.let { it1 ->
                        listener.onItemSelected(mList[adapterPosition], false)
                    }*/
                }
            }

            itemView.setOnClickListener {
                listener.onItemSelected(mList[adapterPosition], false)
                Log.d("TAG addListToView", ": ${mList[adapterPosition]} ")
            }
        }


    }
}