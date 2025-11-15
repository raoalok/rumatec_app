package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TripTourPlanStockistSelection

class TripAddFormStockistAdapter(private val mList1: List<TripTourPlanStockistSelection>): RecyclerView.Adapter<TripAddFormStockistAdapter.ViewHolder>() {


        var onItemClick:((TripAddFormStockistAdapter)-> Unit)? = null
        private var mList: List<TripTourPlanStockistSelection> = mList1 as List<TripTourPlanStockistSelection>

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripAddFormStockistAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.update_tour_plan_update_stockist_card_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val TripPlanEditViewModel = mList[position]
            holder.productId.text = TripPlanEditViewModel.name.toString()
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        fun filterList(filterList: List<TripTourPlanStockistSelection>){
            mList = filterList
            notifyDataSetChanged()
        }

        inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
            val productId: TextView = itemView.findViewById(R.id.update_tour_plan_update_stockist_name_field)

            init{
                itemView.setOnClickListener{
                }
            }
        }
    }