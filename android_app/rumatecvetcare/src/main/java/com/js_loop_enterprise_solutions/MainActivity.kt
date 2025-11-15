package com.js_loop_enterprise_solutions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.js_loop_erp.components.fragments.LoginFragment
import com.js_loop_erp.components.fragments.HomeViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.js_loop_enterprise_solutions.fragments.LeftMenuDialogFragment
import com.js_loop_enterprise_solutions.fragments.MenuItemClick
import com.js_loop_enterprise_solutions.mediator.MediatorProvider
import com.js_loop_erp.components.AlarmReceiver
import com.js_loop_erp.components.ApplicationDb
import com.js_loop_erp.components.FragmentActivityI
import com.js_loop_erp.components.PermissionResolver
import com.js_loop_erp.components.data_class.LoginResponseRumatec
import com.js_loop_erp.components.fragments.ActionsViewModel
import com.js_loop_erp.components.fragments.EmpLocationData
import com.js_loop_erp.components.fragments.SaleInvoiceFragment
import com.js_loop_erp.components.fragments.ScheduleFragment
import com.js_loop_erp.components.fragments.SignInOutFragment
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import com.js_loop_erp.rumatec_vetcare_erp.R
import com.js_loop_erp.rumatec_vetcare_erp.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.io.File
import androidx.core.content.edit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.gms.location.Priority
import com.js_loop_enterprise_solutions.fragments.IOnMenuItemClickListener
import com.js_loop_enterprise_solutions.fragments.MainMenuItem
import com.js_loop_enterprise_solutions.fragments.MenuLayoutAdapter
import com.js_loop_erp.components.BuildConfig
import com.js_loop_erp.components.data_class.UserSession
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import androidx.core.net.toUri
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.js_loop_erp.components.fragments.settings.AppTourDialogFragment
import java.net.URL
import java.util.regex.Pattern
import kotlin.math.log

class MainActivity : AppCompatActivity(), FragmentActivityI, IOnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var notificationManager: NotificationManager
    private lateinit var permissionResolver: PermissionResolver

    private var processRunning: Boolean = false

    private val homeViewModel: HomeViewModel by viewModels()
    private val actionsViewModel: ActionsViewModel by viewModels()

    private lateinit var navView: BottomNavigationView

    private lateinit var applicationDb: ApplicationDb

    private lateinit var menuItemVisibility: Menu

    private lateinit var batteryLevelReceiver: BroadcastReceiver
    private var isReceiverRegistered = false

    private lateinit var recyclerViewMenuGrid: RecyclerView

    private var dialogSplashScreen: Dialog? = null
    private var typingHandler: Handler? = null
    private var typingRunnableList = mutableListOf<Runnable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = ""

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(null)
        supportActionBar?.setCustomView(R.layout.custom_action_bar)

        navView = binding.navView.findViewById<BottomNavigationView>(R.id.nav_view)

        val menu = navView.menu
        val leaveApproveRejectItem = menu.findItem(R.id.leave_approve_reject)
        leaveApproveRejectItem?.isVisible = true

        applicationDb = ApplicationDb(this@MainActivity)

        val cachedFile = File(this@MainActivity.cacheDir, "profile_image.jpg")
        if (cachedFile.exists()) {
            binding.profileImage.setImageURI(Uri.fromFile(cachedFile))
        }

        recyclerViewMenuGrid = binding.menuItemRecyclerView

        recyclerViewMenuGrid.layoutManager = GridLayoutManager(this@MainActivity, 3)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.action_update_quantity -> {
                    showProductsFragment()
                }

                R.id.action_update_expense -> {
                    showExpenseUpdateFragment()
                }

                R.id.action_update_cnf -> {
                    showCnfFragment()
                }

                R.id.action_update_sales -> {
                    showSaleInvoiceFragment()
                }

                R.id.action_schedule_planning -> {
                    showProductListFragment()
                }
            }
            true
        }

        binding.settingsButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        if (!processRunning && !LocationService.SERVICE_RUNNING) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(mbroadcastReceiver, IntentFilter("LOCATION_TELEMETRY"))
            registerReceiver(deviceGpsStateListener, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        }

        //scheduleDailyAlarm()

        batteryLevelReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val batteryPct = level * 100 / scale.toFloat()
                    Log.d("BatteryLevel", "Current battery level: $batteryPct%")
                }
            }
        }

        registerBatteryLevelReceiver()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        menuItemVisibility = menu!!

        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {

        return true
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()

        permissionResolver = PermissionResolver(this)

        checkForLogin()

        checkForLocation()

        homeViewModel.userSessionLiveData.observe(this@MainActivity) { session ->
            Log.d(TAG, "onStart: ${session.userName}")
            binding.nameText.text = session.userName
            binding.userIdText.text = "Id: ${session.userId}"
        }

        if (!isHandlerRunning) {
            startHandlerToPushLocationData()
            startHandlerToPushBatteryPercentage()
        }

        /*if (!homeViewModel._isLocationServiceRunning_.hasObservers()) {
            homeViewModel._isLocationServiceRunning_.observe(this@MainActivity, { item ->
                Handler(Looper.getMainLooper()).post {

                }
            })
        }

        if (!homeViewModel._logInLogOffButtonStatus_.hasObservers()) {
            homeViewModel._logInLogOffButtonStatus_.observe(this@MainActivity, { item ->
                Handler(Looper.getMainLooper()).post {

                }
            })
        }

        if (!actionsViewModel._openUri_.hasObservers()) {
            actionsViewModel._openUri_.observe(this@MainActivity, { item ->

            })
        }*/

        with(supportFragmentManager) {
            setFragmentResultListener(LoginFragment.TAG, this@MainActivity) { _, bundle ->
                val loginResponse = bundle.getInt(LoginFragment.LOGIN_RESULT.toString(), 0)
                if (loginResponse != 0 && loginResponse != 200) {
                    messageToast("Error: ${loginResponse}, Please check your inputs.")
                } else {
                    Handler(Looper.getMainLooper()).post {
                        checkApplicationPermissions()
                    }
                    USER_EMAIL = bundle.getString(LoginFragment.USER_EMAIL, "..")
                    storeLoginSuccessInSharedPreference(LoginFragment.RESPONSE_STRING)
                    //Log.d(TAG, "onStart: filter storeLoginSuccessInSharedPreference store login ${LoginFragment.RESPONSE_STRING}")
                }
            }

            setFragmentResultListener(SaleInvoiceFragment.TAG, this@MainActivity) { _, bundle ->
                val uriPdf = bundle.getString(uri.toString(), null)
                if (uriPdf != null) {
                    Log.d(TAG, "onStart:..... $uriPdf")
                }
            }

            setFragmentResultListener(SignInOutFragment.TAG, this@MainActivity){_, bundle ->
                val uriSignInOut = bundle.getString(SignInOutFragment.SIGN_IN_OUT,"null")
                Log.d(TAG, "onStart: Bundle $uriSignInOut")
                if(uriSignInOut != "null"){
                    if(uriSignInOut == "Log In"){
                        startForegroundService() // check this for battery saving disable
                        homeViewModel.loginButtonListener(2)
                        homeViewModel.isLocationServiceRunning("Status Online")
                        homeViewModel.logInLogOffButtonStatus("Log Off")
                        Log.d(TAG, "onStart: Bundle 1")
                        LOGIN_LOG_OFF_BUTTON = "Log Off"
                        LOGIN_STATUS_ONLINE_OFFLINE = "Status Online"
                    } else {
                        stopForegroundService()
                        homeViewModel.loginButtonListener(1)
                        homeViewModel.isLocationServiceRunning("Status Offline")
                        homeViewModel.logInLogOffButtonStatus("Log In")
                        Log.d(TAG, "onStart: Bundle 2")
                        LOGIN_LOG_OFF_BUTTON = "Log In"
                        LOGIN_STATUS_ONLINE_OFFLINE = "Status Offline"
                    }
                }
            }

            setFragmentResultListener(LeftMenuDialogFragment.TAG, this@MainActivity) { _, bundle ->
                val menuItemClick = bundle.getInt(LeftMenuDialogFragment.MENU_ITEM_CLICKED, -1)
                if (menuItemClick != -1) {
                    when(menuItemClick){
                        MenuItemClick.ATTENDANCE.ordinal->{
                            val mediator = MediatorProvider()
                            val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                            mediator.registerReceiver(receiverComponent )
                            mediator.sendMessage("ATTENDANCE_PAGINATION")
                            mediator.unregisterReceiver(receiverComponent)
                        }
                        MenuItemClick.ACTIVITY.ordinal->{
                            val mediator = MediatorProvider()
                            val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                            mediator.registerReceiver(receiverComponent )
                            mediator.sendMessage("ACTIVITY_PAGINATION")
                            mediator.unregisterReceiver(receiverComponent)
                        }
                        MenuItemClick.LEAVE.ordinal->{
                            val mediator = MediatorProvider()
                            val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                            mediator.registerReceiver(receiverComponent )
                            mediator.sendMessage("LEAVE_PAGINATION")
                            mediator.unregisterReceiver(receiverComponent)
                        }
                        MenuItemClick.TRIP_PLAN.ordinal->{
                            val mediator = MediatorProvider()
                            val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                            mediator.registerReceiver(receiverComponent )
                            mediator.sendMessage("TRIP_PLAN_FRAGMENT")
                            mediator.unregisterReceiver(receiverComponent)
                        }
                        MenuItemClick.REPORTING.ordinal->{
                            val mediator = MediatorProvider()
                            val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                            mediator.registerReceiver(receiverComponent )
                            mediator.sendMessage("TRIP_REPORTING_FRAGMENT")
                            mediator.unregisterReceiver(receiverComponent)
                        }
                        MenuItemClick.APPROVE_TRIP.ordinal->{
                            val mediator = MediatorProvider()
                            val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                            mediator.registerReceiver(receiverComponent )
                            mediator.sendMessage("TRIP_APPROVE_FRAGMENT")
                            mediator.unregisterReceiver(receiverComponent)
                        }
                        MenuItemClick.APPROVE_LEAVE.ordinal->{
                            val mediator = MediatorProvider()
                            val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                            mediator.registerReceiver(receiverComponent )
                            mediator.sendMessage("LEAVE_APPROVE_REJECT")
                            mediator.unregisterReceiver(receiverComponent)
                        }
                        MenuItemClick.LOGIN_STATUS.ordinal->{
                            val mediator = MediatorProvider()
                            val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                            mediator.registerReceiver(receiverComponent )
                            mediator.sendMessage("CHECK_USER_LOGIN_STATUS")
                        }

                        MenuItemClick.SETTINGS.ordinal->{
                            val mediator = MediatorProvider()
                            val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                            mediator.registerReceiver(receiverComponent )
                            mediator.sendMessage("SETTINGS_FRAGMENT")
                        }

                        MenuItemClick.SIGN_OUT.ordinal->{
                            checkForSignOutPrompt()
                        }

                        else->{
                            Log.d(TAG, "onStart: InvalidMenuItemClick")
                        }
                    }
                }
            }
            setFragmentResultListener(AppTourDialogFragment.TAG, this@MainActivity) { _, bundle ->
                LoginFragment().show(supportFragmentManager, LoginFragment.TAG)
            }
        }
        //refreshTodaySchedule()
        pushGrantedPermissionToCLoud()
        //startForegroundService()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            123342 -> {
                val permission = android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                val locationRequestCode = 32232 // You can use any integer value
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(this, arrayOf(permission), locationRequestCode)
                } else {
                    // Permission has been granted
                }
            }
            32232 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
                    val intent = Intent()
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = ("package:" + packageName).toUri()
                    startActivity(intent)
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startHandlerToPushLocationData() {
        val handler: Handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null
        val delay = 10000
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())

            isHandlerRunning = true
            val localLocationDataLength: Int = applicationDb.getTotalEntries()
            if (localLocationDataLength > 100) {
                GlobalScope.launch(Dispatchers.IO) {
                    pushLocationDataToServer(applicationDb.getLastEntries(100))
                }
            } else if (localLocationDataLength > 0) {
                GlobalScope.launch(Dispatchers.IO) {
                    pushLocationDataToServer(applicationDb.getLastEntries(localLocationDataLength))
                }
            }

        }.also { runnable = it }, delay.toLong())
    }

    private fun startHandlerToPushBatteryPercentage(){
        val handler: Handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null
        val delay = 1000 * 60 * 10
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())

            isHandlerRunning = true
            val batteryLevel = getBatteryPercentage(this@MainActivity)
            Log.d(TAG, "Current battery level: $batteryLevel%")

        }.also { runnable = it }, delay.toLong())
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
    }
    
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        Log.d(TAG, "onBackPressed:  backPressed")

        return when (keyCode) {
            else -> super.onKeyUp(keyCode, event)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val intent = Intent("SOME_TAG").apply { putExtra("KEY_CODE", keyCode) }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        return super.onKeyDown(keyCode, event)
    }


    override fun onResume() {
        super.onResume()

        if (!LocationService.SERVICE_RUNNING) {
            homeViewModel.isLocationServiceRunning("Status Offline")
            homeViewModel.logInLogOffButtonStatus("Log In")
        } else {
            homeViewModel.isLocationServiceRunning("Status Online")
            homeViewModel.logInLogOffButtonStatus("Log Off")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBatteryLevelReceiver()
    }

    private fun checkForLogin() {

        val sharedPreferences = getSharedPreferences("LoginDetails", MODE_PRIVATE)
        val loginStatus: Boolean = sharedPreferences.contains("LOGIN")

        if (!loginStatus) {
            //LoginFragment().show(supportFragmentManager, LoginFragment.TAG)
            showSplashScreen()
        } else {
            val userName = sharedPreferences.getString("Name", " ")
            val address = sharedPreferences.getString("ADDRESS", " ")
            val userId = sharedPreferences.getString("USER_ID", " ")
            val token = sharedPreferences.getString("TOKEN", " ")
            val adminPermission = sharedPreferences.getBoolean("PERMISSIONS",false)

            if (userName != null) {
                //homeViewModel.userName(userName)
                val newSession = UserSession(
                    userName = userName,
                    time = "00:00 AM",
                    userId = userId ?: "0",
                    login = true,
                    token = token ?: " ",
                    role = " ",
                    email = "vaibhav@example.com",
                    adminPermission = true//adminPermission
                )

                homeViewModel.updateUserSession(newSession)
                homeViewModel.userId("Employee ID: " + userId)
                homeViewModel.address("Address: " + address)
                Log.d(TAG, "checkForLogin: $userName $userId $address ")
            }

            if (token?.length!! > 8) {
                USER_TOKEN = token
                USER_ID = userId ?: ""
                Log.d(TAG, " storeLoginSuccessInSharedPreference checkForLogin2: ${USER_TOKEN.length} ")
            }

            val permission: Boolean = sharedPreferences.getBoolean("PERMISSIONS", false)

            USER_PERMISSION = true//permission
            Log.d(TAG, "checkForLogin USER_PERMISSION: ${USER_PERMISSION}")
            addItemsInMenu()

            try {
                val sharedPrefs = EncryptedSharedPreferences.create(
                    this@MainActivity,
                    "secure_prefs",
                    MasterKey.Builder(this@MainActivity)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                val tokenSecure = sharedPrefs.getString("TOKEN_SECURE", " ")
                Log.d(TAG, "checkForLogin: tokenSecure ${tokenSecure!!.length} ")

            } catch (e: Exception){
                Log.d(TAG, "checkForLogin USER_PERMISSION Error: ${e.toString()}")
            }
        }
    }

    private fun checkForLocation() {
        Log.d(TAG, "onReceive: three_times_location_pop_up ....1  ")

        val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Checking GPS is enabled
        val mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!mGPS) {
            //startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            var networkEnabled = false
            val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                networkEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (ex: Exception) {
                Log.d(TAG, "checkForLogin: ${ex.printStackTrace()}")
            }
            Log.d(TAG, "onReceive: three_times_location_pop_up ....2 ")

            showSystemLocationAlert()
            //enableLocationSettings()
        } else {
            SYSTEM_LOCATION_ENABLE_DIALOG_ACTIVE = false
        }
    }

    private fun enableLocationSettings() {
        Log.d(TAG, "onReceive: three_times_location_pop_up ....4 ")

        /*val locationRequest: LocationRequest = LocationRequest.create()
            .setInterval(10 * 1000)
            .setFastestInterval(2 * 1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        LocationServices
            .getSettingsClient(this)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener(this) { response: LocationSettingsResponse? -> }
            .addOnFailureListener(this) { ex ->
                if (ex is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(ex.resolution).build()
                        resolutionForResult.launch(intentSenderRequest)
                    } catch (exception: java.lang.Exception) {
                        Log.d(TAG, "enableLocationSettings: $exception")
                    }
                }
            }*/

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_INTERVAL).apply { setMinUpdateIntervalMillis(LOCATION_UPDATE_INTERVAL) }.build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        LocationServices.getSettingsClient(this)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener(this) { response: LocationSettingsResponse? ->
                // Location settings are satisfied
            }
            .addOnFailureListener(this) { ex ->
                if (ex is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(ex.resolution).build()
                        resolutionForResult.launch(intentSenderRequest)
                    } catch (e: Exception) {
                        Log.d(TAG, "enableLocationSettings: $e")
                    }
                }
            }
    }

    private var resolutionForResult: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, ": resolutionForResult PASS")
            } else {
                Log.d(TAG, ": resolutionForResult FAIL")
            }
        }


    private var mbroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val intentAction = intent.action
            Log.d(TAG, "onReceive: onLocationResult: sanjay_location_6 666")
            val b = intent.getBundleExtra("LOCATION_TELEMETRY_")
            if (intentAction.toString().contains("LOCATION_TELEMETRY")) {
                val lastKnownLoc = b!!.getParcelable<Parcelable>("LOCATION_TELEMETRY_") as Location?
                if (lastKnownLoc != null) {
                    //homeViewModel.time("Lat: ${lastKnownLoc.latitude} Lon:${lastKnownLoc.longitude}")
                    pushLocationDataToServer(lastKnownLoc)
                } else {
                    Log.d(TAG, "onReceive: last_location_is_null")
                }
            }
        }
    }

    private var deviceGpsStateListener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            val parseIntentExtraParams = intent.action
            if (intent.action!!.matches(LocationManager.PROVIDERS_CHANGED_ACTION.toRegex())) {
                //checkForLocation()
                val handler: Handler = Handler(Looper.getMainLooper())
                val delay = 100
                handler.postDelayed( {
                    if (!SYSTEM_LOCATION_ENABLE_DIALOG_ACTIVE) {
                        SYSTEM_LOCATION_ENABLE_DIALOG_ACTIVE = true
                        //showSystemLocationAlert()
                        Log.d(TAG, "onReceive: three_times_location_pop_up ....  ${intent.action}")
                        checkForLocation()
                    }
                }, delay.toLong())

                Log.d(TAG, "onReceive: three_times_location_pop_up   ${intent.action}")
            }
        }
    }

    private fun showSystemLocationAlert() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location is Disabled")
        builder.setMessage("Device Location is Required.")
        builder.setPositiveButton("Enable", { _, _ ->
            SYSTEM_LOCATION_ENABLE_DIALOG_ACTIVE = false
            enableLocationSettings()
        })
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
    }

//// check this for battery saving disable
    private fun startForegroundService() {
        if (!processRunning) {
            processRunning = true
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //startService(Intent(applicationContext, LocationService::class.java))
            Log.d(TAG, "startForegroundService:....1")

            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                LocationService.startService(this@MainActivity, "startForegroundService()", ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)

            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION
                    ),
                    REQUEST_LOCATION_PERMISSION
                )
            }

//            LocationService.startService(this@MainActivity, "startForegroundService()")
        }
    }

    private fun stopForegroundService() {
        processRunning = false
        LocationService().stopServiceCallback()
        LocationService.stopService(this@MainActivity)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkApplicationPermissions() {
        permissionResolver.checkPermissions()
    }

    private fun messageToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun storeLoginSuccessInSharedPreference(message: String) {

        val gson = Gson()
        lifecycleScope.launch {
            try {
                val user = gson.fromJson(message, LoginResponseRumatec::class.java)

                if (user.token?.length!! > 7) {
                    Log.d(TAG, "storeLoginSuccessInSharedPreference:1 ${USER_TOKEN.length} ")
                    USER_TOKEN = user.token!!
                }

                Log.d(TAG, "storeLoginSuccessInSharedPreference: ${user.userPermissions?.authorizeTours}")

                USER_PERMISSION = true //user.userPermissions?.authorizeTours == true

                val userName: String? = user.user?.name
                val time: String = ""
                val login: Boolean = true
                val token: String = user.token!!
                // var role: String = user.user.role
                val permissions: Boolean = USER_PERMISSION //user.permissions?.authorizeTours == true
                val email: String = USER_EMAIL
                var address: String? = user.user?.address
                val userId: String = user.user?.id.toString()

                val sharedPreferences = getSharedPreferences("LoginDetails", MODE_PRIVATE)

                sharedPreferences.edit() {

                    putString("Name", userName)
                    putString("TIME", time)
                    putBoolean("LOGIN", login)
                    putString("TOKEN", token)
                    //userInfo.putString("ROLE", role)
                    putBoolean("PERMISSIONS", permissions)
                    putString("EMAIL", email)

                    //userInfo.putString("ADDRESS", address)
                    putString("USER_ID", userId)
                }
                Log.d(TAG, "storeLoginSuccessInSharedPreference: filterError shared pref done")

                val masterKey = MasterKey.Builder(this@MainActivity)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val sharedPrefs = EncryptedSharedPreferences.create(
                    this@MainActivity,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                val tokenSecure: String = user.token ?: ""
                sharedPrefs.edit().putString("TOKEN_SECURE", tokenSecure).apply()

                val newSession = UserSession(
                    userName = userName.toString(),
                    time = "00:00 AM",
                    userId = userId,
                    login = true,
                    token = " ",
                    role = " ",
                    email = " ",
                    adminPermission = true//permissions
                )

                homeViewModel.updateUserSession(newSession)

                addItemsInMenu()

            } catch (e: Exception) {
                Log.d(TAG, "filterError: $e")
            }
        }
    }

    private fun checkForSignOutPrompt(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign Out.")
        builder.setMessage("Do you want to Sign Out?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            signOutFragment()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
    }

    private fun signOutFragment(){
        val sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            clear()
        }
        USER_TOKEN = ""
        USER_PERMISSION = true//false

        val newSession = UserSession(
            userName = "",
            time = "00:00 AM",
            userId = "",
            login = false,
            token = " ",
            role = " ",
            email = " "
        )

        File(this.cacheDir, "profile_image.jpg")
            .takeIf { it.exists() }
            ?.delete()

        homeViewModel.updateUserSession(newSession)

        LoginFragment().show(supportFragmentManager, LoginFragment.TAG)
    }

    private fun showProductsFragment() {
        val mediator = MediatorProvider()
        val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
        mediator.registerReceiver(receiverComponent )
        mediator.sendMessage("INVENTORY_FRAGMENT")
        mediator.unregisterReceiver(receiverComponent)
    }

    private fun showExpenseUpdateFragment() {
        val mediator = MediatorProvider()
        val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
        mediator.registerReceiver(receiverComponent )
        mediator.sendMessage("EXPENSE_PAGINATION")
        mediator.unregisterReceiver(receiverComponent)
    }

    private fun showCnfFragment() {
        //CnfFragment().show(supportFragmentManager, null)
        val mediator = MediatorProvider()
        val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
        mediator.registerReceiver(receiverComponent )
        mediator.sendMessage("CNF_FRAGMENT")
        mediator.unregisterReceiver(receiverComponent)
    }

    private fun showSaleInvoiceFragment() {
        //SaleInvoiceFragment().show(supportFragmentManager, null)
        val mediator = MediatorProvider()
        val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
        mediator.registerReceiver(receiverComponent )
        mediator.sendMessage("SCHEDULE_INVOICE_FRAGMENT")
        mediator.unregisterReceiver(receiverComponent)
    }

    fun showScheduleFragment() {
        ScheduleFragment().show(supportFragmentManager, null)
    }

    private fun showProductListFragment() {
        //ProductListFragment().show(supportFragmentManager, null)
        val mediator = MediatorProvider()
        Log.d(TAG, "storeLoginSuccessInSharedPreference showProductListFragment: ${USER_TOKEN.length} ")
        val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
        mediator.registerReceiver(receiverComponent )
        mediator.sendMessage("PRODUCT_LIST_FRAGMENT")
        mediator.unregisterReceiver(receiverComponent)
    }


    fun pushLocationDataToServer(location: Location) {

        val results = FloatArray(1)
        val distance = Location.distanceBetween(
            START_LATITUDE,
            START_LONGITUDE,
            location.latitude,
            location.longitude,
            results
        )

        Log.d(TAG, "pushLocationDataToServer: locationDistance: ${results[0]} ___ $distance ")

        if (LocationService.SERVICE_RUNNING && results[0] > 0.1 && results[0] < 100000.00 && location.latitude != START_LATITUDE && USER_TOKEN.length > 8) {

            START_LATITUDE = location.latitude
            START_LONGITUDE = location.longitude

            Log.d(TAG, "pushLocationDataToServer: _locationDistance: ${results[0]}  $distance ")

            val authToken = USER_TOKEN
            val unixTimeNow: String = System.currentTimeMillis().toString()

            var client = OkHttpClient()
            if(BuildConfig.DEBUG_ONLY_BUILD){

                Log.d(SignInOutFragment.TAG, "Debug Build Bypass the SSL check")
                Log.d(SignInOutFragment.TAG, "uploadImagesToCloud: ${ReceiverMediator.USER_TOKEN} ")

                val trustAllCerts = arrayOf<TrustManager>(
                    @SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkClientTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {}
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkServerTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {}
                        override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> = arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                client = OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .build()
            } else {
                client = OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            }

            /*val client = OkHttpClient.Builder()
                .connectTimeout(4, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(4, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(4, java.util.concurrent.TimeUnit.SECONDS)
                .build()*/
            val mediaType = "application/json".toMediaType()

            val locationDataList = listOf(
                EmpLocationData(
                    salesPersonId = 4,
                    lat = location.latitude.toString(),
                    long = location.longitude.toString(),
                    et = "0.0",
                    alt = location.altitude.toString(),
                    vel = location.speedAccuracyMetersPerSecond.toString(),
                    hAcc = "0.0",
                    vAcc = location.verticalAccuracyMeters.toString(),
                    remark = "0.0",
                    date = System.currentTimeMillis().toString()
                )
            )
            val gson = Gson()
            val jsonLocationDataList = gson.toJson(locationDataList)
            /*            val salesData = listOf (
                            LocationData(
                            salesPersonId = 4,
                            lat = "0.0",
                            long = "0.0",
                            et = "0.0",
                            alt = "0.0",
                            vel = "0.0",
                            hAcc = "0.0",
                            vAcc = "0.0",
                            remark = "0.0",
                            date = "1695821259")
                        )

                        // Convert the SalesData object to JSON
                        val gson = Gson()
                        val json = gson.toJson(salesData)*/

            var responseBody: Response
            val request = Request.Builder()
                //.url("http://65.0.61.137/api/gps-locations")
                //.url("${ReceiverMediator.SERVER_SYNC_URI}/api/gps-locations")
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/gps-locations")
                //.post(body)
                .post(jsonLocationDataList.toRequestBody(mediaType))
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $authToken")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.d(TAG, "pushLocationDataToServer: fail " + unixTimeNow)
                    applicationDb.insertSalesData(locationDataList[0])

                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { it ->
                        responseBody = it
                        Log.d(TAG, "pushLocationDataToServer:  ${it.code}  ...... $it")

                        if (!responseBody.isSuccessful) {
                            Log.d(TAG, "pushLocationDataToServer: Unexpected code ${responseBody.code} ....  ${responseBody.body}")
                            applicationDb.insertSalesData(locationDataList[0])
                            throw IOException(" pushLocationDataToServer: Unexpected code $responseBody")
                        }
                    }
                }
            })

        } else {
            Log.d(TAG, "pushLocationDataToServer: ... $results")
            START_LATITUDE = location.latitude
            START_LONGITUDE = location.longitude
        }
    }


    override fun showMessage(myString: String?): Int {
        Log.d("image_image_image", " + size.toString()....")
        return 0
    }

    override fun openUri(url: Uri) {
        Log.d("image_image_image", " + size.toString() $url")
        showToast()
    }

    private fun showToast() {
        Log.d("image_image_image", " + size.toString()")
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, "showToast()", Toast.LENGTH_LONG).show()
        }
    }

    private fun pushLocationDataToServer(listOfLocationDataFromDb: List<EmpLocationData>) {
        if (USER_TOKEN.length > 8) {
            val unixTimeNow: String = System.currentTimeMillis().toString()
            var client = OkHttpClient()
            if(BuildConfig.DEBUG_ONLY_BUILD){

                val trustAllCerts = arrayOf<TrustManager>(
                    @SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkClientTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {}
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkServerTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {}
                        override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> = arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                client = OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .build()
            } else {
                client = OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            }

            val mediaType = "application/json".toMediaType()

            val gson = Gson()
            val jsonLocationDataList = gson.toJson(listOfLocationDataFromDb)

            var responseBody: Response
            val request = Request.Builder()
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/gps-locations")
                .post(jsonLocationDataList.toRequestBody(mediaType))
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $USER_TOKEN")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.d(TAG, "pushLocationDataToServer: fail " + unixTimeNow)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { it ->
                        responseBody = it
                        Log.d(TAG, "pushLocationDataToServer:  ${it.code}  ...... $it")
                        applicationDb.deleteLastEntries(listOfLocationDataFromDb.size)

                        if (!responseBody.isSuccessful) {
                            Log.d(TAG, "pushLocationDataToServer: Unexpected code ${responseBody.code} ....  ${responseBody.body!!}")
                            throw IOException(" pushLocationDataToServer: Unexpected code $responseBody")
                        }
                    }
                }
            })
        }

    }

    @SuppressLint("ServiceCast")
    private fun scheduleDailyAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val requestCode = 0 // Use a unique code for this alarm
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        // Check if the alarm is already set
        if (pendingIntent != null) {
            Log.d("MainActivity", "Alarm already scheduled")
            return
        }

        // Create a new PendingIntent since one doesn't exist
        val newPendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm to start at 21:00
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // If the scheduled time is in the past, move it to the next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intervalMillis = 1 * 60 * 1000 // 15 minutes in milliseconds
        // Schedule the alarm to repeat every day
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_HOUR,
            newPendingIntent
        )

        /*alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis.toLong(),
            newPendingIntent
        )*/

        Log.d("MainActivity", "Alarm scheduled for ${calendar.time}")
    }


    private fun registerBatteryLevelReceiver() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            registerReceiver(batteryLevelReceiver, filter)
            isReceiverRegistered = true
            Log.d("BatteryLevel", "Receiver registered")
        } else {
            Log.d("BatteryLevel", "Receiver already registered")
        }
    }

    private fun unregisterBatteryLevelReceiver() {
        if (isReceiverRegistered) {
            unregisterReceiver(batteryLevelReceiver)
            isReceiverRegistered = false
            Log.d("BatteryLevel", "Receiver unregistered")
        } else {
            Log.d("BatteryLevel", "Receiver not registered, no need to unregister")
        }
    }

    private fun getBatteryPercentage(context: Context): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun pushGrantedPermissionToCLoud() {
        Log.d(TAG, "pushGrantedPermissionToCLoud: ${permissionResolver.isPermissionGranted()}")
        //permissionResolver.isPermissionGranted()
        Log.d(TAG, "pushGrantedPermissionToCLoud: ${checkForPermissionDetails()}")
        val gson = Gson()
        val jsonString = gson.toJson(checkForPermissionDetails())
        Log.d(TAG, "pushGrantedPermissionToCLoud: $jsonString")
    }

    private fun checkForPermissionDetails(): ApplicationPermissions{
        return ApplicationPermissions(
            accessFineLocation = checkPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION),
            postNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                true // Not required for Android 12 and below
            },
            foregroundService = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                checkPermission(this, android.Manifest.permission.FOREGROUND_SERVICE)
            } else {
                true // Not required for Android 8 and below
            },
            readMediaImages = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> checkPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                true -> checkPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                else -> true // For API levels below 23, this permission is granted at install time
            },
            accessCamera = checkPermission(this, android.Manifest.permission.CAMERA),
            accessBackGroundLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { checkPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                checkPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            },
            deviceModel = Build.MODEL,
            deviceSDK = Build.VERSION.SDK_INT.toString()
        )
    }

    private fun checkPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onItemClick(item: MainMenuItem) {
        when(item.menuItemName){
            "Log In/Off"->{
                val mediator = MediatorProvider()
                val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                mediator.registerReceiver(receiverComponent )
                mediator.sendMessage("SIGN_IN_OUT_FRAGMENT")
                mediator.unregisterReceiver(receiverComponent)

                //checkForUpdate()
            }
            "Activity"->{
                val mediator = MediatorProvider()
                val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                mediator.registerReceiver(receiverComponent )
                mediator.sendMessage("ACTIVITY_PAGINATION")
                mediator.unregisterReceiver(receiverComponent)
            }
            "Leave"->{
                val mediator = MediatorProvider()
                val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                mediator.registerReceiver(receiverComponent )
                mediator.sendMessage("LEAVE_PAGINATION")
                mediator.unregisterReceiver(receiverComponent)
            }
            "Trip Plan"->{
                val mediator = MediatorProvider()
                val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                mediator.registerReceiver(receiverComponent )
                mediator.sendMessage("TRIP_PLAN_FRAGMENT")
                mediator.unregisterReceiver(receiverComponent)
            }
            "Reporting"->{
                val mediator = MediatorProvider()
                val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                mediator.registerReceiver(receiverComponent )
                mediator.sendMessage("TRIP_REPORTING_FRAGMENT")
                mediator.unregisterReceiver(receiverComponent)
            }
            "Approve Trip"->{
                val mediator = MediatorProvider()
                val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                mediator.registerReceiver(receiverComponent )
                mediator.sendMessage("TRIP_APPROVE_FRAGMENT")
                mediator.unregisterReceiver(receiverComponent)
            }
            "Approve Leave"->{
                val mediator = MediatorProvider()
                val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                mediator.registerReceiver(receiverComponent )
                mediator.sendMessage("LEAVE_APPROVE_REJECT")
                mediator.unregisterReceiver(receiverComponent)
            }
            "Login Status"->{
                val mediator = MediatorProvider()
                val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                mediator.registerReceiver(receiverComponent )
                mediator.sendMessage("CHECK_USER_LOGIN_STATUS")
            }
            "Sign Out"->{
                checkForSignOutPrompt()
            }
            "Products"->{
                showProductListFragment()
            }
            "Inventory"->{
                showProductsFragment()
            }
            "Expense"->{
                showExpenseUpdateFragment()
            }
            "Sales"->{
                showSaleInvoiceFragment()
            }
            "Settings"->{
                val mediator = MediatorProvider()
                val receiverComponent = ReceiverMediator(supportFragmentManager, USER_TOKEN)
                mediator.registerReceiver(receiverComponent )
                mediator.sendMessage("SETTINGS_FRAGMENT")
            }

            else->{
                Log.d(LeftMenuDialogFragment.TAG, "onStart: InvalidMenuItemClick")
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.profileImage.setImageURI(it)
            saveImageToCache(it)
        }
    }

    private fun saveImageToCache(imageUri: Uri) {
        val inputStream = this.contentResolver.openInputStream(imageUri)
        val file = File(this.cacheDir, "profile_image.jpg")
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        Log.d("ProfileDialog", "Saved image to cache: ${file.absolutePath}")
    }

    private fun showSplashScreen(){

        val dialogSplash = Dialog(this@MainActivity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(com.js_loop_erp.components.R.layout.splash_screen_layout, null)

        dialogSplash.setContentView(dialogView)
        dialogSplash.setCancelable(false)

        // Optional: make background transparent if your layout needs it
        dialogSplash.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Ensure it truly goes edge to edge
        dialogSplash.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Handle manual dismissal cleanup
        dialogSplash.setOnDismissListener {
            typingRunnableList.forEach { typingHandler?.removeCallbacks(it) }
            typingRunnableList.clear()
            typingHandler = null
        }

        dialogSplash.show()
        dialogSplashScreen = dialogSplash

        val dialogTitle: TextView = dialogView.findViewById(com.js_loop_erp.components.R.id.dialogTitleSplashScreen)
        val baseText = "Welcome To "
        val fullText = "Rumatec Vetcare"
        val fullDescription = "Rumatec Vetcare was born from a pharmacist’s passion to improve cattle health and support Pet Owners with effective nutrition and treatments..."
        val typingDelay: Long = 100
        val typingDelayDesc: Long = 20
        //splash_screen_button_parent_layout

        dialogTitle.text = baseText
        dialogSplashScreen?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        typingHandler = Handler(Looper.getMainLooper())


        for (i in 1..fullText.length) {
            val runnable = Runnable {
                dialogTitle.text = baseText + fullText.substring(0, i)
                if (i == fullText.length) {
                    typingHandler?.postDelayed({
                        //dialog.dismiss()
                        dialogSplash.findViewById<LinearLayout>(com.js_loop_erp.components.R.id.splash_screen_button_parent_layout).visibility = View.VISIBLE
                    }, 3000)
                }
            }
            typingHandler?.postDelayed(runnable, i * typingDelay)
            typingRunnableList.add(runnable)
        }

        for (i in 1..fullDescription.length) {
            val runnable = Runnable {
                dialogSplash.findViewById<TextView>(com.js_loop_erp.components.R.id.messageText).text = fullDescription.substring(0, i)
                if (i == fullText.length) {
                    typingHandler?.postDelayed({
                        //dialog.dismiss()
                    }, 10)
                }
            }
            typingHandler?.postDelayed(runnable, i * typingDelay)
            typingRunnableList.add(runnable)
        }

        dialogSplashScreen!!.findViewById<AppCompatButton>(com.js_loop_erp.components.R.id.button_app_tour).setOnClickListener {
            openAppTour()
//            dialogSplashScreen!!.dismiss()
            //openAppTour()
            val rootView = dialogSplashScreen?.window?.decorView?.findViewById<View>(android.R.id.content)

            val fadeOut = AlphaAnimation(1.0f, 0.0f).apply {
                duration = 500
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        dialogSplashScreen?.dismiss()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }

            rootView?.startAnimation(fadeOut)
        }

        dialogSplashScreen!!.findViewById<AppCompatButton>(com.js_loop_erp.components.R.id.button_skip_app_tour).setOnClickListener {
            //dialogSplashScreen!!.dismiss()
            val rootView = dialogSplashScreen?.window?.decorView?.findViewById<View>(android.R.id.content)

            val fadeOut = AlphaAnimation(1.0f, 0.0f).apply {
                duration = 500
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        dialogSplashScreen?.dismiss()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }

            rootView?.startAnimation(fadeOut)
            LoginFragment().show(supportFragmentManager, LoginFragment.TAG)
        }
    }

    private fun show_splash_once(){
        val builder = AlertDialog.Builder(this@MainActivity)
        val inflater: LayoutInflater = layoutInflater
        val dialogView: View = inflater.inflate(com.js_loop_erp.components.R.layout.splash_screen_layout, null)

        builder.setView(dialogView)
        builder.setCancelable(false)

        dialogSplashScreen = builder.create()
        dialogSplashScreen?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogSplashScreen?.show()

        // Set up the typing effect
        val dialogTitle: TextView = dialogView.findViewById(com.js_loop_erp.components.R.id.dialogTitleSplashScreen)
        val baseText = "Welcome To "
        val fullText = "Rumatec Vetcare"
        val typingDelay: Long = 100 // milliseconds per character

        dialogTitle.text = baseText

        val handler = Handler(Looper.getMainLooper())

        for (i in 1..fullText.length) {
            handler.postDelayed({
                dialogTitle.text = baseText + fullText.substring(0, i)

                // Dismiss dialog after full text is shown
                if (i == fullText.length) {
                    handler.postDelayed({
                        dialogSplashScreen?.dismiss()
                    }, 1000) // optional delay before dismissing
                }

            }, i * typingDelay)
        }
    }

    private fun openAppTour(){
        val appTour = AppTourDialogFragment()
        appTour.show(supportFragmentManager, "App Tour Dialog")
    }

    private fun addItemsInMenu(){

        val mainMenuItems = listOf(
            MainMenuItem(
                id = 1,
                menuItemName = "Log In/Off",
                menuItemImage = R.drawable.attendance,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 2,
                menuItemName = "Activity",
                menuItemImage = R.drawable.activity,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 3,
                menuItemName = "Leave",
                menuItemImage = R.drawable.leave,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 4,
                menuItemName = "Trip Plan",
                menuItemImage = R.drawable.trip_plan,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 5,
                menuItemName = "Products",
                menuItemImage = R.drawable.product_shelf_svgrepo_com,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 6,
                menuItemName = "Inventory",
                menuItemImage = R.drawable.warehouse_svgrepo_com,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 7,
                menuItemName = "Expense",
                menuItemImage = R.drawable.rupee_svgrepo_com,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 8,
                menuItemName = "Sales",
                menuItemImage = R.drawable.cashier_sale_shop_svgrepo_com,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 9,
                menuItemName = "Reporting",
                menuItemImage = R.drawable.approve_reporting,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 10,
                menuItemName = "Approve Trip",
                menuItemImage = R.drawable.approve_trip,
                isMenuItemAdmin = !USER_PERMISSION
            ),
            MainMenuItem(
                id = 11,
                menuItemName = "Approve Leave",
                menuItemImage = R.drawable.approve_leave,
                isMenuItemAdmin = !USER_PERMISSION
            ),
            MainMenuItem(
                id = 12,
                menuItemName = "Login Status",
                menuItemImage = R.drawable.checklist,
                isMenuItemAdmin = !USER_PERMISSION
            ),
            MainMenuItem(
                id = 13,
                menuItemName = "Settings",
                menuItemImage = com.js_loop_erp.components.R.drawable.settings_gear_svgrepo_com,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 14,
                menuItemName = "Sign Out",
                menuItemImage = com.js_loop_erp.components.R.drawable.baseline_add_24,
                isMenuItemAdmin = false
            )/*,
            MainMenuItem(
                id = 10,
                menuItemName = "Profile",
                menuItemImage = com.js_loop_erp.components.R.drawable.baseline_add_24,
                isMenuItemAdmin = false
            )*/
        )

        val adapter: MenuLayoutAdapter = MenuLayoutAdapter( mainMenuItems.filter { !it.isMenuItemAdmin }, this@MainActivity)
        recyclerViewMenuGrid.adapter = adapter
    }

    val updateResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // ✅ User accepted update
            Toast.makeText(this, "Update started!", Toast.LENGTH_SHORT).show()
        } else {
            // ❌ User canceled or update failed
            Toast.makeText(this, "Update canceled or failed", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Function that checks for updates and launches the flow
    private fun checkForUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    //Toast.makeText(this, "Failed to start update flow", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "checkForUpdate: Failed to start update flow ${e.printStackTrace()}")
                }
            } else {
                //Toast.makeText(this, "No update available", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "checkForUpdate: No update available")
            }
        }.addOnFailureListener {
//            Toast.makeText(this, "Failed to check for update", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "checkForUpdate: Failed to check for update ")
        }
    }

    companion object {

        val TAG = MainActivity::class.java.name
        
        private const val LOCATION_INTERVAL = 10000L
        private const val LOCATION_UPDATE_INTERVAL = 2000L

        private var LOGIN_LOG_OFF_BUTTON: String = "Log In"
        private var LOGIN_STATUS_ONLINE_OFFLINE: String = "Status Offline"

        private var LOCATION_DATA = "location_data"

        private var SYSTEM_LOCATION_ENABLE_DIALOG_ACTIVE: Boolean = false

        var USER_EMAIL: String = " "

        var START_LATITUDE: Double = 0.0
        var START_LONGITUDE: Double = 0.0

        var USER_TOKEN: String = " "
        var USER_ID: String = ""
        var USER_PERMISSION: Boolean = true//false

        var isHandlerRunning: Boolean = false
        
        var uri: String? = null
        val SERVER_SYNC_URI : String = "https://rverp.in" //"https://65.0.61.137"

        private const val REQUEST_LOCATION_PERMISSION = 1001

    }
}

data class ApplicationPermissions(
    val accessFineLocation: Boolean,
    val postNotifications: Boolean,
    val foregroundService: Boolean,
    val readMediaImages: Boolean,
    val accessCamera: Boolean,
    val accessBackGroundLocation: Boolean,
    val deviceModel: String,
    val deviceSDK: String
)