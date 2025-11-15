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
import  com.js_loop_erp.components.data_class.TourPlanUpdateChemist
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanParty
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanPetshop
import  com.js_loop_erp.components.fragments.TourPlanUpdateChemistDetailsItemClickListenerI
import  com.js_loop_erp.components.fragments.TourPlanUpdateDetailsChemist
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdateDetailsParty
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdatePartyDetailsItemClickListenerI

class TourPlanUpdateChemistAdapter (private val listener: TourPlanUpdateChemistDetailsItemClickListenerI, private var mList1: ArrayList<TripSubmitReportSelectedTourPlanPetshop>): RecyclerView.Adapter<TourPlanUpdateChemistAdapter.ViewHolder>() {

    var onItemClick: ((TourPlanUpdateDetailsChemist) -> Unit)? = null

    private var mList: ArrayList<TripSubmitReportSelectedTourPlanPetshop> = mList1 as ArrayList<TripSubmitReportSelectedTourPlanPetshop>

    private val checkedList: MutableList<TripSubmitReportSelectedTourPlanPetshop> = mutableListOf()
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourPlanUpdateChemistAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_doctor_visit_submit_card_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val ItemsViewModel = mList[position].parties
        val ItemsViewModel = mList[position]

        //Log.d("TAG", "filterList: addListToView 6 ${ItemsViewModel.toString()}")

        //holder.productName.text = ItemsViewModel.date
        //       for (i in 1..5) {
        // Your code here
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

        //       }
        /*for (item in ItemsViewModel) {
            holder.productName.text = item.name
            //holder.checkButton.isChecked = ItemsViewModel.isSelected == true
            //holder.container.addView(partyView)
        }*/
//
//        holder.productName.text = ItemsViewModel.product
//        holder.checkButton.isChecked = ItemsViewModel.isSelected == true
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun getCheckedItems(): List<TripSubmitReportSelectedTourPlanPetshop> {
        //checkButtin
        //Log.d("TAG", "getCheckedItems1: ${checkedList.size}")
        return mList
    }

    fun getSelected(): TripSubmitReportSelectedTourPlanPetshop? {
        if(selectedPosition >= 0){
            return mList[selectedPosition]
        } else {
            return null
        }
    }

    fun filterList(filterList: ArrayList<TripSubmitReportSelectedTourPlanPetshop>){
        //Log.d("TAG", "filterList: addListToView 5 ${filterList[0].name}")
        mList = filterList
        notifyDataSetChanged()
    }


    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tour_plan_update_name_field)
        val checkButton: AppCompatRadioButton = itemView.findViewById(R.id.tour_plan_update_check_field)


        init {
            checkButton.setOnClickListener{
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
