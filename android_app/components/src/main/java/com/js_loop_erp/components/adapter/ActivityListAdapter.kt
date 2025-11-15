package com.js_loop_erp.components.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import com.js_loop_erp.components.fragments.ItemsViewModel
import  com.js_loop_erp.components.fragments.attendance.AttendanceListModel
import  com.js_loop_erp.components.fragments.daily_activity.ActivityListAdd
import  com.js_loop_erp.components.fragments.daily_activity.ActivityListI
import  com.js_loop_erp.components.fragments.daily_activity.ActivityListModel

class ActivityListAdapter(private val listener: ActivityListI, private val mList1: List<ActivityListModel>): RecyclerView.Adapter<ActivityListAdapter.ViewHolder>() {
    var onItemClick:((ActivityListAdd)-> Unit)? = null
    private var mList: List<ActivityListModel> = mList1 as List<ActivityListModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_list_card_view,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val ActivityList = mList[position]
        holder.activityDate.text = ActivityList.date
        holder.activityName.text = ActivityList.activity
        holder.routeName.text = ActivityList.category
        holder.activityKms.text = ActivityList.activityKms.toString()

    }

    override fun getItemCount():Int{
        return mList.size
    }

    fun filterList(filterList: List<ActivityListModel>){
        mList = filterList
        notifyDataSetChanged()
    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView){
        val activityDate: TextView =    itemView.findViewById(R.id.activity_card_date)
        val activityName: TextView =    itemView.findViewById(R.id.activity_activity_name_card)
        val routeName: TextView =        itemView.findViewById(R.id.activity_route_card)
        val activityKms: TextView =     itemView.findViewById(R.id.activity_route_km)
        val activityDeleteButton: ImageButton = itemView.findViewById(R.id.button_delete_attedance_activity)

        init{
            itemView.setOnClickListener{

            }

            itemView.setOnLongClickListener {
                val attendanceListModel: ActivityListModel = mList[adapterPosition]
                listener.onItemLongClick(attendanceListModel)
                true
            }

            activityDeleteButton.setOnClickListener{
                mList[adapterPosition].id?.let{it1 -> listener.onItemClick(mList[adapterPosition].id!!.toInt())}
            }
        }
    }

    companion object {
        const val TAG = "ActivityListAdapter"
    }
}