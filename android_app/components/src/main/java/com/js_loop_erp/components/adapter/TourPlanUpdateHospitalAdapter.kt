package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TourPlanUpdateHospital
import  com.js_loop_erp.components.fragments.TourPlanUpdateDetailsHospital
import  com.js_loop_erp.components.fragments.TourPlanUpdateDetailsHospitalItemClickListenerI

class TourPlanUpdateHospitalAdapter (private val listener: TourPlanUpdateDetailsHospitalItemClickListenerI, private var mList1: ArrayList<TourPlanUpdateHospital>): RecyclerView.Adapter<TourPlanUpdateHospitalAdapter.ViewHolder>() {



    var onItemClick: ((TourPlanUpdateDetailsHospital) -> Unit)? = null

    private var mList: ArrayList<TourPlanUpdateHospital> = mList1 as ArrayList<TourPlanUpdateHospital>

    private val checkedList: MutableList<TourPlanUpdateHospital> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourPlanUpdateHospitalAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.tour_plan_update_companion_card_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.productName.text = ItemsViewModel.product
        holder.checkButton.isChecked = ItemsViewModel.isSelected == true
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun getCheckedItems(): List<TourPlanUpdateHospital> {
        //checkButtin
        //Log.d("TAG", "getCheckedItems1: ${checkedList.size}")
        return mList
    }

    fun filterList(filterList: ArrayList<TourPlanUpdateHospital>){
        mList = filterList
        notifyDataSetChanged()
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
                if (isChecked) {
                    mList[adapterPosition].id?.let { it1 ->
                      //  listener.onItemSelected(mList[adapterPosition], true)
                    }
                } else {
                    mList[adapterPosition].id?.let { it1 ->
                      //  listener.onItemSelected(mList[adapterPosition], false)
                    }
                }
            }

            itemView.setOnClickListener {

            }
        }


    }

}
