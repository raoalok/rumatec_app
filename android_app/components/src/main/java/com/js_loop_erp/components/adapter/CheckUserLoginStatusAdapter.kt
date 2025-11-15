package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.fragments.access_controlled.CheckUserLoginStatus
import  com.js_loop_erp.components.fragments.access_controlled.CheckUserLoginStatusData
import  com.js_loop_erp.components.fragments.access_controlled.CheckUserLoginStatusI

class CheckUserLoginStatusAdapter  (private val listener: CheckUserLoginStatusI, private var mList1: ArrayList<CheckUserLoginStatusData>): RecyclerView.Adapter<CheckUserLoginStatusAdapter.ViewHolder>() {

    var onItemClick: ((CheckUserLoginStatus) -> Unit)? = null

    private var mList: ArrayList<CheckUserLoginStatusData> = mList1 as ArrayList<CheckUserLoginStatusData>

    private val checkedList: MutableList<CheckUserLoginStatusData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckUserLoginStatusAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_login_status_check, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.productName.text = ItemsViewModel.name
        //holder.checkButton.isChecked = ItemsViewModel.isSelected == true
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun getCheckedItems(): List<CheckUserLoginStatusData> {
        return mList
    }

    fun filterList(filterList: ArrayList<CheckUserLoginStatusData>){
        mList = filterList
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.user_login_status_name)
        //val checkButton: AppCompatCheckBox = itemView.findViewById(R.id.tour_approve_user_update_check_field)


        init {
            /*checkButton.setOnClickListener {
            }

            checkButton.setOnCheckedChangeListener { buttonView, isChecked ->
                //Log.d("TAG", "addListToView: ${mList[adapterPosition].name} ")
                if (isChecked) {
                    mList[adapterPosition].isSelected = true
                    listener.onItemSelected(mList[adapterPosition], true)
                } else {
                    mList[adapterPosition].isSelected = false
                    listener.onItemSelected(mList[adapterPosition], false)
                }
            }

            itemView.setOnClickListener {

            }*/
        }


    }

}