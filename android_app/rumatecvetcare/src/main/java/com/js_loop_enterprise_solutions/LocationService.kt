package com.js_loop_enterprise_solutions

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.js_loop_erp.components.MainActivity
import com.js_loop_erp.components.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


class LocationService: Service() {

    private lateinit var notificationManager : NotificationManager
    private var isStarted = false
    var locationData: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var callBack_: LocationCallback? = null

    // FIXED: Made intent parameter nullable to handle Android service restarts
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle null intent case (service restart after being killed)
        if (intent == null) {
            Log.w(TAG, "Service restarted with null intent")
            // Still need to make foreground and continue location updates
            makeForeground()
            if (!isStarted) {
                isStarted = true
                location_update()
            }
            return START_STICKY
        }

        Toast.makeText(this@LocationService, "Login Success", Toast.LENGTH_LONG).show()
        //start_delay()
        makeForeground()
        if(isStarted == false){
            isStarted = true
            location_update()
        }

        return START_STICKY
    }

    override fun onDestroy() {
//        unregisterComponentCallbacks(callback_)
        callBack_?.let { fusedLocationClient.removeLocationUpdates(it) }
        super.onDestroy()
    }

    override fun onCreate(){
        super.onCreate()
        // Toast.makeText(this@LocationService, "Service onCreate", Toast.LENGTH_LONG).show()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@LocationService)

    }

    override fun onBind(p0: Intent?): IBinder? {
        //TODO("Not yet implemented")
        return null
    }

    @SuppressLint("MissingPermission")
    private fun location_update() {
        /*val locationRequest by lazy {
            LocationRequest.create().apply{
                interval = TimeUnit.SECONDS.toMillis(UPDATE_INTERVAL_SECS)
                fastestInterval = TimeUnit.SECONDS.toMillis(FASTEST_UPDATE_INTERVAL_SECS)
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        }*/

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(UPDATE_INTERVAL_SECS))
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(TimeUnit.SECONDS.toMillis(FASTEST_UPDATE_INTERVAL_SECS * 2))
            .setMaxUpdateDelayMillis(TimeUnit.SECONDS.toMillis(FASTEST_UPDATE_INTERVAL_SECS))
            .build()

        callBack_ = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                Log.d(TAG, "onLocationResult: sanjay_location_7 ${location.toString()}")

                if(LocationService.SERVICE_RUNNING == true){
                    val intent = Intent("LOCATION_TELEMETRY")
                    val locatioData = "Lat: ${location?.latitude}, Lon: ${location?.longitude}"
                    intent.putExtra("LOCATION_TELEMETRY_", locationData.toString())
                    val b = Bundle()
                    b.putParcelable("LOCATION_TELEMETRY_", location)
                    intent.putExtra("LOCATION_TELEMETRY_", b)
                    LocalBroadcastManager.getInstance(this@LocationService).sendBroadcast(intent)

                    val currentTime = LocalTime.now()
                    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                    currentTime.format(formatter)

                    val pendingIntent = PendingIntent.getActivity(this@LocationService, 4370, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)


                    val notificationBuilder = NotificationCompat.Builder(this@LocationService, CHANNEL_ID)
                        .setContentTitle("Rumatec Vetcare")
                        .setContentText("Session Running: ${currentTime.format(formatter)}")
                        .setSmallIcon(R.drawable.logo_rumatec_vetcare_foreground)
                        .setContentIntent(pendingIntent)
                        .setSilent(true)
                        .setOnlyAlertOnce(true)
                        .setOngoing(true)

                    notificationManager.notify(ONGOING_NOTIFICATION_ID, notificationBuilder.build())

                } else {
                    Log.d(TAG, "onLocationResult: Cannot sent location LOCATION_SERVICE RUNNING is False")
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, callBack_ as LocationCallback, Looper.getMainLooper())
    }

    fun stopServiceCallback(){
        callBack_?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    private fun makeForeground() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this@LocationService, 4370, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        createServiceNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Rumatec Vetcare")
            .setContentText("Session Running")
            .setSmallIcon(com.js_loop_erp.rumatec_vetcare_erp.R.drawable.logo_rumatec_vetcare_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun createServiceNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID,"Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }


    private fun  start_delay() {
        val handler: Handler = Handler()
        var runnable: Runnable? = null
        val delay = 6000

        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            //To-Do
        }.also{ runnable = it }, delay.toLong())
    }

    companion object Initializer {

        val TAG = LocationService::class.java.name

        val LOCATION_TELEMETRY: String? = "bbb"
        private const val ONGOING_NOTIFICATION_ID = 12322
        private const val CHANNEL_ID = "1001"
        private const val EXTRA_DEMO = "EXTRA_DEMO"

        var SERVICE_RUNNING: Boolean = false

        const val UPDATE_INTERVAL_SECS = 60L
        const val FASTEST_UPDATE_INTERVAL_SECS = 60L

        fun startService(context: Context, messageString: String, foregroundServiceType: Int = 0) {
            val intent = Intent(context, LocationService::class.java)
            intent.putExtra(EXTRA_DEMO, messageString)
            SERVICE_RUNNING = true

            // Check permission for foreground service location (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (context.checkSelfPermission(android.Manifest.permission.FOREGROUND_SERVICE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.w(TAG, "FOREGROUND_SERVICE_LOCATION permission not granted!")
                    // You may want to request permission from the user before starting the service
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // startForegroundService with optional foregroundServiceType
                val component = ComponentName(context, LocationService::class.java)
                if (foregroundServiceType != 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.startForegroundService(intent.apply {
                        putExtra("FOREGROUND_SERVICE_TYPE", foregroundServiceType)
                    })
                } else {
                    context.startForegroundService(intent)
                }
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, LocationService::class.java)
            SERVICE_RUNNING = false
            context.stopService(intent)
        }
    }


}