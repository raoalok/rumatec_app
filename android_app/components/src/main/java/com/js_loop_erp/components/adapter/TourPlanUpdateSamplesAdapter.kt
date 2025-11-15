package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.ProductListSamples
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdateDetailsDoctor
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdateDetailsSamples
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdateDetailsSamplesItemClickListenerI
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourPlanUpdateDoctorDetailsItemClickListenerI

class TourPlanUpdateSamplesAdapter (private val listener: TourPlanUpdateDetailsSamplesItemClickListenerI, private var mList1: ArrayList<ProductListSamples>): RecyclerView.Adapter<TourPlanUpdateSamplesAdapter.ViewHolder>() {

    var onItemClick: ((TourPlanUpdateDetailsSamples) -> Unit)? = null

    private var mList: ArrayList<ProductListSamples> = mList1 as ArrayList<ProductListSamples>

    private val checkedList: MutableList<ProductListSamples> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourPlanUpdateSamplesAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.tour_plan_update_companion_card_layout, parent, false)

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

    fun getCheckedItems(): List<ProductListSamples> {
        return mList
    }

    fun filterList(filterList: ArrayList<ProductListSamples>){
        mList = filterList
        notifyDataSetChanged()
    }

    fun filterList(position: Int) {
        notifyItemChanged(position)
    }

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tour_plan_update_name_field)
        val checkButton: AppCompatCheckBox = itemView.findViewById(R.id.tour_plan_update_check_field)


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
}
