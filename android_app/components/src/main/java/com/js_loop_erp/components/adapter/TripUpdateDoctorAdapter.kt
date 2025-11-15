package com.js_loop_erp.components.adapter

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.data_class.TourPlanUpdateDoctor
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanDoctor
import  com.js_loop_erp.components.fragments.TripPlanEditFragment

class TripUpdateDoctorAdapter (private val mList1: List<TripSubmitReportSelectedTourPlanDoctor>) : RecyclerView.Adapter<TripUpdateDoctorAdapter.ViewHolder>() {

    var onItemClick:((TripPlanEditFragment)-> Unit)? = null
    private var mList: List<TripSubmitReportSelectedTourPlanDoctor> = mList1 as List<TripSubmitReportSelectedTourPlanDoctor>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.update_tour_plan_update_doctor_card_layout, parent, false)
        return ViewHolder(view)
    }


    /*data class TripSubmitReportSelectedTourPlanDoctor(
    val id: Int? = null,
    val tourId: Int? = null,
    val doctorId: Int? = null,
    var timestamp: String? = null,
    val companionIds: MutableList<SelectedCompanion> = mutableListOf(),
    val name: String? = null,
    val products: MutableList<Int> = mutableListOf(),
    val samples: MutableList<SamplesUpdate> = mutableListOf()
)*/

    //doctor.samples.joinToString(";") { sample ->"${sample.name}:${sample.productId}"}

    /*val combinedText = """
                            <b>Date:</b> ${LocalDateTime.parse(trip.date, pattern).toLocalDate().toString()}<br><br>
                            <b>From:</b> ${trip.fromArea}<br><br>
                            <b>To:</b> ${trip.toArea}<br><br>
                            <b>Mode:</b> ${trip.mode}<br><br>
                            <b>Companions:</b> ${companionList.joinToString(separator = ", ")}<br><br>
                            <b>Doctors:</b> ${doctorsList.joinToString(separator = ", ")}<br><br>
                            <b>Parties:</b> ${partiesList.joinToString(separator = ", ")}<br><br>
                            <b>Hospitals:</b> ${hospitalList.joinToString(separator = ", ")}<br><br>
                            <b>Pet Shops:</b> ${petShopsList.joinToString(separator = ", ")}<br><br>
                            <b>Institutes:</b> ${institutesList.joinToString(separator = ", ")}<br><br>
                            <b>Remark:</b> ${trip.remark}
                            """.trimIndent()*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

    fun filterList(filterList: List<TripSubmitReportSelectedTourPlanDoctor>){
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