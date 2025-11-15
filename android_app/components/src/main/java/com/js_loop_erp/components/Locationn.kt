package com.js_loop_erp.components

import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices


class Locationn : Service() {

    // declaring object of MediaPlayer

    private lateinit var notificationManager: NotificationManager
    // onStartCommand can be called multiple times, so we keep track of "started" state manually
    private var isStarted = false
    var locationData : Location? = null

    // execution of service will start
    // on calling this method
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // creating a media player which
        // will play the audio of Default
        // ringtone in android device

        Toast.makeText(this@Locationn,"Hixx", Toast.LENGTH_LONG).show()

        Log.d(TAG, "onStartCommand: sanjay_location_4")

        // providing the boolean
        // value as true to play
        // the audio on loop

        // starting the process
        //player.start()

        location_update()

        makeForeground()

        //LocationRequester(this@Locationn).observeLocation(1000L,1.00F)

        // returns the status
        // of the program
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        // initialize dependencies here (e.g. perform dependency injection)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //textView = view.findViewById(R.id.sample_text)

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    private fun location_update() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@Locationn)
        try {
            Log.d(TAG, "location_update: ${fusedLocationClient.getLastLocation().toString()}")
        } catch (e: Exception) {
            Log.d(TAG, "location_update: ")
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            Log.d(TAG, "location_update: sanjay_location_2 ${location.toString()}")
        }

        val handler: Handler = Handler()
        var runnable: Runnable? = null
        val delay = 6000

        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
//---------------------------------Last Location -------------------------------------------
//            var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@Locationn)
//            if (fusedLocationClient != null) {
//                try {
//                    fusedLocationClient.getLastLocation()
//                } catch (e: Exception) {
//                    Log.d(TAG, "location_update: ")
//                }
//            }


            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                Log.d(TAG, "location_update: sanjay_location_3 ${location.toString()}")
//                CoroutineScope(Dispatchers.Main).launch {
//                    locationData = LocationRequester(this@Locationn).getLastLocation()!!
////                    Log.d(TAG, "location_update: sanjay_location_3 ${LocationRequester(this@Locationn).getLastLocation().toString()}")
//                    LocationRequester(this@Locationn).getLastLocation()?.let {
//                        MainActivity().update_ui(it)
//                    }
//                }

              //  locationData?.let { MainActivity().update_ui(it) }

            }

//------------------------------ Last Location ------------------------------------------------------

//------------------------------Current Location ----------------------------------------------------

//------------------------------Current Location ----------------------------------------------------

        }.also{ runnable = it }, delay.toLong())
    }


    private fun makeForeground() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this@Locationn, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        // before calling startForeground, we must create a notification and a corresponding
        // notification channel

        createServiceNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Foreground Service test")
            .setSmallIcon(R.drawable.arrow_up_float)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
        //startForegroundService(ONGOING_NOTIFICATION_ID,notification)
    }


    private fun createServiceNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) {
            return // notification channels were added in API 26
        }
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
    }


    companion object {

        private const val ONGOING_NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "1001"
        private const val EXTRA_DEMO = "EXTRA_DEMO"

        fun startService(context: Context, demoString: String) {
            val intent = Intent(context, Locationn::class.java)
            intent.putExtra(EXTRA_DEMO, demoString)
            Log.d(TAG, "startService: sanjay_foreground_started")
            context.startForegroundService(intent)
        }
        fun stopService(context: Context) {
            val intent = Intent(context, Locationn::class.java)
            context.stopService(intent)
        }
    }


}