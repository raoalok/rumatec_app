package com.js_loop_erp.components.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import  com.js_loop_erp.components.MainActivity
import  com.js_loop_erp.components.R
import com.js_loop_erp.components.adapter.ActivityListAdapter.Companion.TAG
import  com.js_loop_erp.components.fragments.InventoryEditViewModel
import  com.js_loop_erp.components.fragments.daily_activity.ActivityUpdateModel
import  com.js_loop_erp.components.fragments.daily_activity.SubmittedActivityItemClickListenerI
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class ActivityUpdateAdapter (private val listener: SubmittedActivityItemClickListenerI, private val mList1: ArrayList<ActivityUpdateModel>) : RecyclerView.Adapter<ActivityUpdateAdapter.ViewHolder>(){

    var onItemClick: ((SubmittedActivityItemClickListenerI) -> Unit)? = null
    // create new views
    private val mList: ArrayList<ActivityUpdateModel> = mList1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_list_card_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        holder.activityDate.text = convertIsoToDateOnly(ItemsViewModel.startDate)
        holder.activityName.text = ItemsViewModel.type
        holder.routeName.text = ItemsViewModel.category
        holder.activityKms.text = ItemsViewModel.kilometers.toString()


        if(ItemsViewModel.approvedBy != null){
            //holder.itemView.setBackgroundColor(Color.GREEN)
            /*val container = holder.itemView.findViewById<LinearLayout>(R.id.background_container)
            val drawable = container.background as LayerDrawable
            val colorLayer = drawable.findDrawableByLayerId(R.id.color_overlay) as GradientDrawable
            colorLayer.setColor("#e9facd".toColorInt()) // Light greenish color*/
            holder.itemView.findViewById<ImageButton>(R.id.button_delete_attedance_activity).setImageResource(R.drawable.check_in_svgrepo_com)

            val rotation = ObjectAnimator.ofFloat(holder.deleteButton, "rotationY", -70f, 70f).apply {
                duration = 3500
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = LinearInterpolator()
            }

            rotation.start()
        }

        Log.d(TAG, "onBindViewHolder: ...... ${ItemsViewModel.approvedBy}")

        if(ItemsViewModel.rejectedBy != null){
            //holder.itemView.setBackgroundColor(Color.RED)
            /*val container = holder.itemView.findViewById<LinearLayout>(R.id.background_container)
            val drawable = container.background as LayerDrawable
            val colorLayer = drawable.findDrawableByLayerId(R.id.color_overlay) as GradientDrawable
            colorLayer.setColor("#fca3a0".toColorInt()) // Light greenish color*/
            holder.itemView.findViewById<ImageButton>(R.id.button_delete_attedance_activity).setImageResource(R.drawable.delete_remove_uncheck_svgrepo_com)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun deleteItem(index: Int, list: List<InventoryEditViewModel>){
        notifyItemRemoved(index)
        mList.removeAt(index)
    }

    fun convertIsoToDateOnly(isoString: String): String {
        val parsedDate = OffsetDateTime.parse(isoString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        return parsedDate.format(formatter)
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
        val activityDate: TextView =    itemView.findViewById(R.id.activity_card_date)
        val activityName: TextView =    itemView.findViewById(R.id.activity_activity_name_card)
        val routeName: TextView =        itemView.findViewById(R.id.activity_route_card)
        val activityKms: TextView =     itemView.findViewById(R.id.activity_route_km)
        val deleteButton: ImageButton = itemView.findViewById(R.id.button_delete_attedance_activity)
        //val deleteButton: ImageButton = itemView.findViewById(R.id.button_delete)

        init {
            deleteButton.setOnClickListener{
                if(mList[adapterPosition].approvedBy == null && mList[adapterPosition].rejectedBy == null){
                    mList[adapterPosition].id.let{ it1 -> listener.onItemClick(it1)}
                }
            }
            itemView.setOnClickListener {

            }
        }

        /*       init {
                   itemView.setOnClickListener {
                       SubmittedInventoryCheckEditFragment().deleteFromServer()
                   }
               }*/

    }
}