package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.fragments.access_controlled.TripApproveFragment
import  com.js_loop_erp.components.fragments.access_controlled.TourApprovalSelectUserData

class TripApproveUserListAdapter (private val mList1: List<TourApprovalSelectUserData>): RecyclerView.Adapter<TripApproveUserListAdapter.ViewHolder>() {


    var onItemClick:((TripApproveFragment)-> Unit)? = null
    private var mList: List<TourApprovalSelectUserData> = mList1 as List<TourApprovalSelectUserData>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.approve_tour_user_card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val TripPlanEditViewModel = mList[position]
        holder.productId.text = TripPlanEditViewModel.name.toString()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun filterList(filterList: List<TourApprovalSelectUserData>){
        mList = filterList
        notifyDataSetChanged()
    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val productId: TextView = itemView.findViewById(R.id.user_approve_list_text)

        init{
            itemView.setOnClickListener{
            }
        }
    }
}
