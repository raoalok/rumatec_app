package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripTourPlanDoctorSelection

class TripAddFormDoctorAdapter(private val mList1: List<TripTourPlanDoctorSelection>): RecyclerView.Adapter<TripAddFormDoctorAdapter.ViewHolder>() {


        var onItemClick:((TripAddFormDoctorAdapter)-> Unit)? = null
        private var mList: List<TripTourPlanDoctorSelection> = mList1 as List<TripTourPlanDoctorSelection>

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.tour_plan_add_doctor_card_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val TripPlanEditViewModel = mList[position]
            holder.productId.text = TripPlanEditViewModel.name.toString()
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        fun filterList(filterList: List<TripTourPlanDoctorSelection>){
            mList = filterList
            notifyDataSetChanged()
        }

        inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
            val productId: TextView = itemView.findViewById(R.id.tour_plan_doctor_add_name_field)

            init{
                itemView.setOnClickListener{
                }
            }
        }
    }
