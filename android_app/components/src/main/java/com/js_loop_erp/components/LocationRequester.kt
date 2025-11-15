package com.js_loop_erp.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Criteria.ACCURACY_COARSE
import android.location.Criteria.ACCURACY_FINE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume

class LocationRequester(
    private val context: Context
) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var locationRequest: Pair<LocationManager, LocationListener>? = null

     fun checkPermission(): Boolean {
        return permissions.indexOfFirst { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        } >= 0
    }

    suspend fun getLastLocation(): Location? {
        if (!checkPermission()) return null

        return suspendCancellableCoroutine { cont ->
            cont.invokeOnCancellation {
                cancel()
            }

            Log.d(TAG, "getLastLocation() - before getNewestLastKnownLocation")
            getNewestLastKnownLocation(locationManager)?.let { location ->
                Log.d(TAG, "getLastLocation() - before cont.resume(it)")

                cont.resume(location)
                Log.d(TAG, "getLastLocation() - before @suspendCancellableCoroutine")
                return@suspendCancellableCoroutine
            }
            Log.d(TAG, "getLastLocation() - after getNewestLastKnownLocation")

            try {
                dumpProviders()

                val providerCriteria = getProviderCriteria()

                Log.d(TAG, "getLastLocation() - locationManager.getBestProvider with criteria: $providerCriteria")

                locationManager.getBestProvider(providerCriteria, true)?.let { provider ->
                    Log.d(TAG, "getLastLocation() - locationManager.getBestProvider returned providers: $provider")

                    val locationListener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            locationManager.removeUpdates(this)
                            cont.resume(location)
                        }

                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                            // do nothing
                        }

                        override fun onProviderEnabled(provider: String) {
                            // do nothing
                        }

                        override fun onProviderDisabled(provider: String) {
                            locationManager.removeUpdates(this)
                            cont.resume(null)
                        }
                    }.also { listener ->
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            CoroutineScope(Dispatchers.Main).launch {
                                locationManager.requestLocationUpdates(provider, 0, 0f, listener)
                            }
                        } else {
                            Log.d(TAG, "getLastLocation: Request permissions from the user (if in Activity or Fragment)")
                        }
                    }

                    locationRequest = Pair(locationManager, locationListener)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error on location request ", e)
                cancel()
                cont.resume(null)
            }
        }
    }

    fun observeLocation(minUpdateTime: Long, minDistance: Float): Flow<Location?> {
        return callbackFlow<Location?> {
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    Log.d(TAG, "onLocationChanged: sanjay_location_3 ${location.toString()} ")
                    trySendBlocking(location)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    Log.d(TAG, "onLocationChanged: sanjay_location_4")
                    // do nothing
                }

                override fun onProviderEnabled(provider: String) {
                    Log.d(TAG, "onLocationChanged: sanjay_location_5")
                    // do nothing
                }

                override fun onProviderDisabled(provider: String) {
                    Log.d(TAG, "onLocationChanged: sanjay_location_6")
                    cancel()
                }
            }

            if (checkPermission()) {
                try {
                    //dumpProviders()

                    val providerCriteria = getProviderCriteria()

                    locationManager.getBestProvider(providerCriteria, true)?.let { provider ->
                        withContext(Dispatchers.Main) {
                            locationManager.requestLocationUpdates(provider, minUpdateTime, minDistance, locationListener)
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error on location request ", e)
                }
            }

            awaitClose {
                locationManager.removeUpdates(locationListener)
            }
        }.buffer(Channel.CONFLATED) // To avoid blocking
    }

    fun cancel() {
        locationRequest?.let { (locationManager, locationListener) ->
            locationRequest = null
            locationManager.removeUpdates(locationListener)
        }
    }

    private fun getProviderCriteria() = Criteria().apply {
        accuracy = ACCURACY_FINE
    }

    @SuppressLint("MissingPermission")
    private fun getNewestLastKnownLocation(locationManager: LocationManager): Location? {
        var bestLocation: Location? = null
        locationManager.getProviders(true).forEach { provider ->
            try {
                locationManager.getLastKnownLocation(provider)?.let { location ->
                    Log.d(TAG, "getNewestLastKnownLocation: locationManager.getProviders: provider: $provider, location: $location, accuracy: ${location.accuracy} elapsedRealtimeNanos: ${location.elapsedRealtimeNanos}")

                    bestLocation?.let { lastLocation ->
                        if (location.elapsedRealtimeNanos < lastLocation.elapsedRealtimeNanos) {
                            bestLocation = location
                        }
                    } ?: let {
                        bestLocation = location
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error on location request ", e)
            }
        }

        Log.i(TAG, "getNewestLastKnownLocation: bestLocation returning: $bestLocation, accuracy: ${bestLocation?.accuracy} elapsedRealtimeNanos: ${bestLocation?.elapsedRealtimeNanos}")

        return bestLocation
    }

    private fun dumpProviders() {
        locationManager.getProviders(false).forEach {
            val providerInstance = locationManager.getProvider(it)
            Log.d(TAG, "getLastLocation() - locationManager.getProviders with provider: $it, enabled: ${locationManager.isProviderEnabled(it)}, provider: ${providerInstance}")
        }
    }

    companion object {
        val TAG: String = LocationRequester::class.java.simpleName

        private val permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}