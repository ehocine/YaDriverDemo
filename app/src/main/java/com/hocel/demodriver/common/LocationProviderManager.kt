package com.hocel.demodriver.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.IntentSender
import android.location.Location
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresPermission
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@Singleton
class LocationProviderManager @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val application: Application
) {

    private val context get() = application.applicationContext

    private var locationCallback: LocationCallback? = null

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun lastLocation(): LatLng? {
        val location: Location? = awaitLastLocation()
        location?.let {
            return LatLng(it.latitude, it.longitude)
        } ?: run {
            return currentLocation()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private suspend fun awaitLastLocation(): Location? =
        suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    continuation.resume(location, onCancellation = {})
                } ?: run {
                    continuation.resume(null, onCancellation = {})
                }
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
        }

    @SuppressLint("MissingPermission")
    suspend fun currentLocation(): LatLng? {
        val location = awaitCurrentLocation()
        location?.let {
            return LatLng(it.latitude, it.longitude)
        } ?: run {
            return null
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private suspend fun awaitCurrentLocation(): Location? = suspendCoroutine { continuation ->
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            }
        ).addOnSuccessListener { location: Location? ->
            location?.let {
                continuation.resume(it)
            } ?: run {
                continuation.resume(null)
            }
        }

    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdate(
        timeInterval: Long = 20000L,
        timeFastestInterval: Long = 20000L,
        callback: (Location) -> Unit,
    ) {
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            interval = timeInterval
            fastestInterval = timeFastestInterval
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback(callback = callback),
            context.mainLooper
        )

    }

    private fun locationCallback(callback: (Location) -> Unit): LocationCallback {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.locations.forEach { location ->
                    callback(location)
                }
            }
        }
        return locationCallback!!
    }

    fun stopLocationUpdate() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    suspend fun checkGpsAvailability(
        onRequesting: (IntentSenderRequest) -> Unit
    ): Boolean = suspendCancellableCoroutine { continuation ->
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(context).checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
                continuation.resume(true)
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        try {
                            val intentSenderRequest = IntentSenderRequest
                                .Builder((exception as ResolvableApiException).resolution)
                                .build()
                            onRequesting(intentSenderRequest)
                            continuation.cancel()
                        } catch (e: IntentSender.SendIntentException) {
                            e.printStackTrace()
                        } catch (e: ClassCastException) {
                            e.printStackTrace()
                        }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }
    }
}