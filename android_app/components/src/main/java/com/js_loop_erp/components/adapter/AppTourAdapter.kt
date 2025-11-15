package com.js_loop_erp.components.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.js_loop_erp.components.R
import org.w3c.dom.Text

class AppTourAdapter(private val items: List<TourItem>) : RecyclerView.Adapter<AppTourAdapter.TourViewHolder>() {

    data class TourItem(val imageResId: Int, val textTitle: String, val textDescription: String, val textWarning: String)

    inner class TourViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.image)
        val title = view.findViewById<TextView>(R.id.title)
        val desc  =  view.findViewById<TextView>(R.id.description)
        val warn  =  view.findViewById<TextView>(R.id.drawback_of_permission_denial)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tour_page_item, parent, false)
        return TourViewHolder(view)
    }

    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        val item = items[position]
        holder.image.setImageResource(item.imageResId)
        holder.title.text = item.textTitle
        holder.desc.text  = item.textDescription
        holder.warn.text  = item.textWarning
    }

    override fun getItemCount(): Int = items.size
}
