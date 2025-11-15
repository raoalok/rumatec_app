package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripTourPlanDoctorSelection
import  com.js_loop_erp.components.fragments.tripTourPlan.TripTourPlanDoctorSelectionFragmentItemsOnCLickListenerI

class TripTourPlanDoctorSelectionAdapter(private val listener: TripTourPlanDoctorSelectionFragmentItemsOnCLickListenerI, private var mList1: ArrayList<TripTourPlanDoctorSelection>): RecyclerView.Adapter<TripTourPlanDoctorSelectionAdapter.ViewHolder>() {

    var onItemClick:((TripTourPlanDoctorSelectionAdapter)-> Unit)? = null
    private var mList: ArrayList<TripTourPlanDoctorSelection> = mList1 as ArrayList<TripTourPlanDoctorSelection>
    private val checkedList: MutableList<TripTourPlanDoctorSelection> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tour_plan_add_companion_card_layout, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.productName.text = ItemsViewModel.name
        holder.checkButton.isChecked = ItemsViewModel.isSelected == true
    }

    override fun getItemCount(): Int {
        return mList.size
    }



    fun getCheckedItems():List<TripTourPlanDoctorSelection>{
        return mList
    }

    fun filterList(filterList: ArrayList<TripTourPlanDoctorSelection>){
        mList = filterList
        notifyDataSetChanged()
    }

    fun filterList(position: Int) {
        notifyItemChanged(position)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tour_plan_companion_add_name_field)
        val checkButton: AppCompatCheckBox = itemView.findViewById(R.id.tour_plan_companion_add_check_field)

        init {
            checkButton.setOnClickListener {
            }

            checkButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    mList[adapterPosition].id?.let { it1 ->
                        mList[adapterPosition].isSelected = true
                        listener.onItemSelected(mList[adapterPosition], true)
                    }
                } else {
                    mList[adapterPosition].id?.let { it1 ->
                        mList[adapterPosition].isSelected = false
                        listener.onItemSelected(mList[adapterPosition], false)
                    }
                }
            }

            itemView.setOnClickListener {
                if (mList[adapterPosition].isSelected == false) {
                    mList[adapterPosition].id?.let { it1 ->
                        mList[adapterPosition].isSelected = true
                        listener.onItemSelected(mList[adapterPosition], true)
                        filterList(adapterPosition)
                    }
                } else {
                    mList[adapterPosition].id?.let { it1 ->
                        mList[adapterPosition].isSelected = false
                        listener.onItemSelected(mList[adapterPosition], false)
                        filterList(adapterPosition)
                    }
                }
            }
        }
    }

    companion object {
        val TAG: String = TripTourPlanDoctorSelectionAdapter::class.java.name
    }

}