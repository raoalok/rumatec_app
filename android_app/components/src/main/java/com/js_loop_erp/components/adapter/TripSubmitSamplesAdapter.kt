package com.js_loop_erp.components.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.ProductListSamples
import  com.js_loop_erp.components.fragments.tripReportSubmit.TourVisitDoctorListInit

class TripSubmitSamplesAdapter(private val mList1: List<ProductListSamples>) : RecyclerView.Adapter<TripSubmitSamplesAdapter.ViewHolder>() {

    var onItemClick:((TourVisitDoctorListInit)-> Unit)? = null
    private var mList: List<ProductListSamples> = mList1 as List<ProductListSamples>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.update_tour_plan_samples_card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val TripPlanEditViewModel = mList[position]
        holder.productId.text = TripPlanEditViewModel.name.toString()
//        holder.quantity.setText(0)

        holder.quantity.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if(s.length >0 && s.toString().toInt() > 0){
                        TripPlanEditViewModel.quantity = s.toString().toInt()
                    } else if(s.length == 0){
                        TripPlanEditViewModel.quantity = 0
                    }
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun getItems():List<ProductListSamples>{
        return mList
    }

    fun filterList(filterList: List<ProductListSamples>?){
        mList = filterList ?: emptyList<ProductListSamples>()
        notifyDataSetChanged()
    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val productId: TextView = itemView.findViewById(R.id.update_tour_plan_update_doctor_name_field)
        val quantity: EditText = itemView.findViewById(R.id.editText)

        init{
            itemView.setOnClickListener{
            }

        }
    }
}