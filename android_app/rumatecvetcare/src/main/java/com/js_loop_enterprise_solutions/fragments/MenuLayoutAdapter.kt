package com.js_loop_enterprise_solutions.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.js_loop_erp.rumatec_vetcare_erp.R

class MenuLayoutAdapter(
    private val items: List<MainMenuItem>,
    private val listener: IOnMenuItemClickListener
) : RecyclerView.Adapter<MenuLayoutAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_recycler_view_card, parent, false)

        // Calculate and set the width dynamically
        //val screenWidth = parent.context.resources.displayMetrics.widthPixels
        //val cardWidth = (screenWidth / 2) - 32 // Divide by 2 columns and account for margins/padding (16dp each side)

//        val layoutParams = view.layoutParams
//        layoutParams.width = cardWidth
//        view.layoutParams = layoutParams

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        //holder.bind(item, listener)
        holder.title.text = item.menuItemName
        holder.icon.setImageResource(item.menuItemImage)
        //holder.itemView.isVisible = item.isMenuItemAdmin

        holder.bind(item, listener)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.item_icon)
        val title: TextView = itemView.findViewById(R.id.item_title)

        fun bind(item: MainMenuItem, listener: IOnMenuItemClickListener) {
            /*icon.setImageResource(item.iconResId)
            title.text = item.name*/

            itemView.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
}
