package com.js_loop_erp.components

class MainActivity {

    companion object{
        val TAG = MainActivity::class.java.name

        private const val ONGOING_NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "1001"
        private const val EXTRA_DEMO = "EXTRA_DEMO"

        private var LOGIN_LOG_OFF_BUTTON: String = "Log In"
        private var LOGIN_STATUS_ONLINE_OFFLINE: String = "Status Offline"

        private var LOCATION_DATA = "location_data"

        private var SYSTEM_LOCATION_ENABLE_DIALOG_ACTIVE: Boolean = false

        var USER_EMAIL: String = " "

        var START_LATITUDE: Double = 0.0
        var START_LONGITUDE: Double = 0.0

        var USER_TOKEN: String = " "
        var USER_ID: String = ""
        var USER_PERMISSION: Boolean = false

        var isHandlerRunning: Boolean = false

        const val UPDATE_INTERVAL_SECS = 5L
        const val FASTEST_UPDATE_INTERVAL_SECS = 2L
        var uri: String? = null
    }
}