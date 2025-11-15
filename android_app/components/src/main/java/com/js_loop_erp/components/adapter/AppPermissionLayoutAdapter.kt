package com.js_loop_erp.components.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.js_loop_erp.components.R
import com.js_loop_erp.components.data_class.AppPermissionMenuItem
import com.js_loop_erp.components.fragments.IOnAppPermissionItemClickListener

/**
 * Adapter for displaying app permissions in a RecyclerView
 * Updated for Material 3 design with modern card styling
 */
class AppPermissionLayoutAdapter(
    private val items: List<AppPermissionMenuItem>,
    private val listener: IOnAppPermissionItemClickListener
) : RecyclerView.Adapter<AppPermissionLayoutAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.app_permission_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView as MaterialCardView
        private val icon: ImageView = itemView.findViewById(R.id.item_icon)
        private val title: TextView = itemView.findViewById(R.id.item_title)
        private val statusIcon: ImageView = itemView.findViewById(R.id.permission_status_icon)

        fun bind(item: AppPermissionMenuItem, listener: IOnAppPermissionItemClickListener) {
            val context = itemView.context
            
            // Set permission name
            title.text = item.menuItemName
            
            // Set permission icon
            icon.setImageResource(item.menuItemImage)
            
            // Update UI based on permission status
            if (item.isPermissionGranted) {
                // Permission granted - show success state
                icon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.success)
                )
                
                statusIcon.setImageResource(R.drawable.ic_check_circle)
                statusIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.success)
                )
                
                // Optional: Add subtle background tint for granted permissions
                card.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.surface)
                )
                card.strokeWidth = 0
                
            } else {
                // Permission not granted - show default/warning state
                icon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.warning)
                )
                
                statusIcon.setImageResource(R.drawable.ic_chevron_right)
                statusIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.text_tertiary)
                )
                
                // Optional: Add subtle border for permissions that need attention
                card.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.surface)
                )
                card.strokeWidth = context.resources.getDimensionPixelSize(R.dimen.divider_height)
                card.strokeColor = ContextCompat.getColor(context, R.color.warning)
            }
            
            // Set click listener
            itemView.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
}
