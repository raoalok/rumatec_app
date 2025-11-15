package com.js_loop_erp.components

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.js_loop_erp.components.fragments.InventoryEditViewModel
import com.js_loop_erp.components.fragments.LeaveUpdateViewModel
import com.js_loop_erp.components.fragments.SubmittedInventoryCheckEditFragment
import com.js_loop_erp.components.fragments.SubmittedLeaveItemClickListenerI
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class LeaveUpdateAdapter(private val listener: SubmittedLeaveItemClickListenerI, private val mList1: ArrayList<LeaveUpdateViewModel>) : RecyclerView.Adapter<LeaveUpdateAdapter.ViewHolder>(){

    var onItemClick: ((SubmittedInventoryCheckEditFragment) -> Unit)? = null
    // create new views
    private val mList: ArrayList<LeaveUpdateViewModel> = mList1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.leave_list_adapter_card, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        holder.leaveRemark.text = ItemsViewModel.remark
        holder.leaveType.text = "Type: " +ItemsViewModel.type.toString()
        holder.startDate.text = "From: " + convertIsoToDateOnly(ItemsViewModel.startDate.toString())
        holder.endDate.text = "To: " + convertIsoToDateOnly(ItemsViewModel.endDate.toString())
        /*if(ItemsViewModel.approvedBy != null || ItemsViewModel.rejectedBy != null){
            holder.deleteButton.visibility = View.INVISIBLE
        }*/

        if(ItemsViewModel.approvedBy != null){
            //holder.itemView.setBackgroundColor(Color.GREEN)
            /*val container = holder.itemView.findViewById<LinearLayout>(R.id.background_container)
            val drawable = container.background as LayerDrawable
            val colorLayer = drawable.findDrawableByLayerId(R.id.color_overlay) as GradientDrawable
            colorLayer.setColor("#e9facd".toColorInt()) // Light greenish color*/
            holder.itemView.findViewById<ImageButton>(R.id.button_delete_leave).setImageResource(R.drawable.check_in_svgrepo_com)

            val rotation = ObjectAnimator.ofFloat(holder.deleteButton, "rotationY", -70f, 70f).apply {
                duration = 3500
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = LinearInterpolator()
            }

            rotation.start()
        }
        //holder.itemView.findViewById<ImageButton>(R.id.button_delete_leave).setImageResource(R.drawable.check_in_svgrepo_com)


        if(ItemsViewModel.rejectedBy != null){
            //holder.itemView.setBackgroundColor(Color.RED)
            /*val container = holder.itemView.findViewById<LinearLayout>(R.id.background_container)
            val drawable = container.background as LayerDrawable
            val colorLayer = drawable.findDrawableByLayerId(R.id.color_overlay) as GradientDrawable
            colorLayer.setColor("#fca3a0".toColorInt()) // Light greenish color*/
            holder.itemView.findViewById<ImageButton>(R.id.button_delete_leave).setImageResource(R.drawable.delete_remove_uncheck_svgrepo_com)
        }

        /*val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 2000 // 2 seconds per rotation
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }
        holder.deleteButton.startAnimation(rotateAnimation)
        */

        /*val rotation = ObjectAnimator.ofFloat(holder.deleteButton, "rotationY", 0f, 10f).apply {
            duration = 2000 // total duration
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
        }

        rotation.start()*/

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun convertIsoToDateOnly(isoString: String): String {
        val parsedDate = OffsetDateTime.parse(isoString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        return parsedDate.format(formatter)
    }

    fun deleteItem(index: Int, list: List<InventoryEditViewModel>){
        notifyItemRemoved(index)
        mList.removeAt(index)
    }

    fun removeElementFromServer(){
        var responBody: String = " "
        val client = OkHttpClient.Builder()
            .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(20,java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("http://65.0.61.137/api/inventory/sample")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use{
                    if(!response.isSuccessful){

                    } else {
                        responBody = response.body?.string() ?:""
                        // println(responBody)
                        //addListToView(responBody)
                    }
                }
            }
        })
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val leaveRemark: TextView = itemView.findViewById(R.id.leave_remark)
        val leaveType: TextView = itemView.findViewById(R.id.leave_type)
        val startDate: TextView = itemView.findViewById(R.id.leave_start_date)
        val endDate: TextView = itemView.findViewById(R.id.leave_end_date)
        val deleteButton: ImageButton = itemView.findViewById(R.id.button_delete_leave)

        init {
            deleteButton.setOnClickListener{
                if(mList[adapterPosition].approvedBy == null && mList[adapterPosition].rejectedBy == null){
                    mList[adapterPosition].id?.let{
                            it1 -> listener.onItemClick(it1)
                    }
                }
            }
            itemView.setOnClickListener {

            }
        }

    }

    companion object {
        const val TAG = "LeaveUpdateAdapter"
    }
}