package com.js_loop_erp.components

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        Log.d("AlarmReceiver", "Alarm triggered at ${System.currentTimeMillis()}")
        //checkLoginStatus()
        showNotification(context, "Rumatec Vetcare", "Gentle Reminder, update your today's schedule!")
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("daily_alarm", "Rumatec Vetcare", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "daily_alarm")
            .setSmallIcon(R.drawable.logo_rumatec_vetcare_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(13223, notification)
    }

    fun checkLoginStatus(){
        if (ReceiverMediator.USER_TOKEN.length > 8) {

            val unixTimeNow: String = System.currentTimeMillis().toString()

            val client = OkHttpClient.Builder()
                .connectTimeout(4, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(4, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(4, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            var responBody__: Response
            val request = Request.Builder()
                .url("http://65.0.61.137/api/application/gps-locations/status")
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.d(MainActivity.TAG, "Alarm Manager Error")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { it ->
                        responBody__ = it
                        Log.d(MainActivity.TAG, "pushLocationDataToServer:  ${it.code}  ...... ${it!!.toString()}")

                        if (!responBody__.isSuccessful) {
                            Log.d(MainActivity.TAG, "pushLocationDataToServer: Unexpected code ${responBody__.code} ....  ${responBody__.body!!.toString()}")
                            throw IOException(" pushLocationDataToServer: Unexpected code $responBody__")
                        }
                    }
                }
            })
        }
    }
}