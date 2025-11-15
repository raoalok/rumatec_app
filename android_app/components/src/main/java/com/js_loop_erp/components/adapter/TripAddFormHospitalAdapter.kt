package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripTourPlanHospitalSelection

class TripAddFormHospitalAdapter(private val mList1: List<TripTourPlanHospitalSelection>): RecyclerView.Adapter<TripAddFormHospitalAdapter.ViewHolder>() {

        var onItemClick:((TripAddFormHospitalAdapter)-> Unit)? = null
        private var mList: List<TripTourPlanHospitalSelection> = mList1 as List<TripTourPlanHospitalSelection>

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripAddFormHospitalAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.update_tour_plan_update_hospital_card_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val TripPlanEditViewModel = mList[position]
            holder.productId.text = TripPlanEditViewModel.name.toString()
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        fun filterList(filterList: List<TripTourPlanHospitalSelection>){
            mList = filterList
            notifyDataSetChanged()
        }

        inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
            val productId: TextView = itemView.findViewById(R.id.update_tour_plan_update_hospital_name_field)

            init{
                itemView.setOnClickListener{
                }
            }
        }
    }