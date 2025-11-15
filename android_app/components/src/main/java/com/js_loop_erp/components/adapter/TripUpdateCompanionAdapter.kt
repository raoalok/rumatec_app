package com.js_loop_erp.components.adapter

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TourPlanUpdateCompanion
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanInstitute
import  com.js_loop_erp.components.fragments.TripPlanEditFragment
import  com.js_loop_erp.components.fragments.tripReportSubmit.TripApprovedPlanDetails

class TripUpdateCompanionAdapter (private val mList1: List<TripSubmitReportSelectedTourPlanInstitute>) : RecyclerView.Adapter<TripUpdateCompanionAdapter.ViewHolder>() {

    var onItemClick:((TripApprovedPlanDetails)-> Unit)? = null
    private var mList: List<TripSubmitReportSelectedTourPlanInstitute> = mList1 as List<TripSubmitReportSelectedTourPlanInstitute>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.update_tour_plan_update_doctor_card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripUpdateCompanionAdapter.ViewHolder, position: Int) {
        val TripPlanEditViewModel = mList[position]

        val SamplesList =TripPlanEditViewModel.samples.joinToString(", ") { item ->"${item.name}: ${item.quantity}"}
        val companionList =TripPlanEditViewModel.companionIds.joinToString(", ") { item ->"${item.name}"}
        val ProductList =TripPlanEditViewModel.products.joinToString(", ") { item ->"${item.name}"}

        val name = "<b>Name:</b> ${TripPlanEditViewModel.name}"
        val timestamp = "<b>Time:</b> ${TripPlanEditViewModel.timestamp}"
        val companionIds = "<b>Companions:</b> ${companionList}"
        val products = "<b>Products:</b>${ProductList}."
        val samples = "<b>Samples:</b> ${SamplesList}"

        val combinedText = """<br>
             $name<br><br>
             $timestamp<br><br>
            <b>Companions:</b> $companionList<br><br>
            <b>Products:</b> $ProductList<br><br>
            <b>Samples:</b> $SamplesList<br>
        """.trimIndent()

        holder.productId.text = Html.fromHtml(combinedText, Html.FROM_HTML_MODE_LEGACY)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun filterList(filterList: List<TripSubmitReportSelectedTourPlanInstitute>){
        mList = filterList
        notifyDataSetChanged()
    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val productId: TextView = itemView.findViewById(R.id.update_tour_plan_update_doctor_name_field)

        init{
            itemView.setOnClickListener{
            }
        }
    }
}