package com.hocel.demodriver.services.tracking

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.hocel.demodriver.util.isServiceRunningInForeground
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingServiceManager @Inject constructor(
    private val application: Application,
) {

    fun startTracking() {
        if (application.isServiceRunningInForeground(TrackingService::class.java)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Handler(Looper.getMainLooper()).post {
                ContextCompat.startForegroundService(
                    application.applicationContext,
                    Intent(application.applicationContext, TrackingService::class.java)
                )
            }
        } else {
            ContextCompat.startForegroundService(
                application.applicationContext,
                Intent(application.applicationContext, TrackingService::class.java)
            )
        }
    }

    fun stopTracking() {
        application.applicationContext.stopService(
            Intent(application.applicationContext, TrackingService::class.java)
        )
    }

}