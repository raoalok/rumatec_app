package com.js_loop_erp.components.adapter


import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.js_loop_erp.components.R
import com.js_loop_erp.components.data_class.AppPermissionMenuItem
import com.js_loop_erp.components.fragments.IOnAppPermissionItemClickListener

class AppPermissionLayoutAdapter(
    private val items: List<AppPermissionMenuItem>,
    private val listener: IOnAppPermissionItemClickListener
) : RecyclerView.Adapter<AppPermissionLayoutAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_permission_card, parent, false)

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
        holder.title.text = item.menuItemName
        holder.icon.setImageResource(item.menuItemImage)

        if(item.isPermissionGranted == true){
            val container = holder.itemView.findViewById<LinearLayout>(R.id.app_permission_card_container)
            val drawable = container.background as LayerDrawable
            val colorLayer = drawable.findDrawableByLayerId(R.id.color_overlay) as GradientDrawable
            colorLayer.setColor("#0ff0ad".toColorInt())
        } else {
            val container = holder.itemView.findViewById<LinearLayout>(R.id.app_permission_card_container)
            val drawable = container.background as LayerDrawable
            val colorLayer = drawable.findDrawableByLayerId(R.id.color_overlay) as GradientDrawable
            colorLayer.setColor(Color.WHITE)
        }

        holder.bind(item, listener)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.item_icon)
        val title: TextView = itemView.findViewById(R.id.item_title)

        fun bind(item: AppPermissionMenuItem, listener: IOnAppPermissionItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
}
