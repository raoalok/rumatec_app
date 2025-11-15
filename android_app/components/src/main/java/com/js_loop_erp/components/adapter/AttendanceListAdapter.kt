package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.fragments.attendance.AttendanceList
import  com.js_loop_erp.components.fragments.attendance.AttendanceListI
import  com.js_loop_erp.components.fragments.attendance.AttendanceListModel

class AttendanceListAdapter(private val listener: AttendanceListI, private val mList1: List<AttendanceListModel>): RecyclerView.Adapter<AttendanceListAdapter.ViewHolder>() {
    var onItemClick:((AttendanceList)-> Unit)? = null
    private var mList: List<AttendanceListModel> = mList1 as List<AttendanceListModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.attendance_list_card_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val AttendanceList = mList[position]
        holder.attendanceDate.text = AttendanceList.date
        holder.attendanceActivity.text = AttendanceList.activity
        holder.attendanceRoute.text = AttendanceList.route
    }

    override fun getItemCount():Int{
        return mList.size
    }

    fun filterList(filterList: List<AttendanceListModel>){
        mList = filterList
        notifyDataSetChanged()
    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView){
        val attendanceDate: TextView = itemView.findViewById(R.id.attendance_card_date)
        val attendanceActivity: TextView = itemView.findViewById(R.id.attendance_activity_name_card)
        val attendanceRoute: TextView = itemView.findViewById(R.id.attendance_route_card)
        val deleteActivity:ImageButton = itemView.findViewById(R.id.button_delete_attedance_activity)

        init{
            itemView.setOnClickListener{
                //val attendanceListModel: AttendanceListModel = mList[adapterPosition]
                //listener.onItemLongClick(attendanceListModel)

            }

            itemView.setOnLongClickListener {
                val attendanceListModel: AttendanceListModel = mList[adapterPosition]
                listener.onItemLongClick(attendanceListModel)
                true
            }

            deleteActivity.setOnClickListener{
                mList[adapterPosition].id?.let{it1 -> listener.onItemClick(mList[adapterPosition].id!!.toInt())}
            }

        }
    }
}