package com.hocel.demodriver.services.backgroundService

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
class ServiceManager @Inject constructor(
    private val application: Application
) {

    fun startService() {
        if (application.isServiceRunningInForeground(BackgroundService::class.java)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Handler(Looper.getMainLooper()).post {
                ContextCompat.startForegroundService(
                    application.applicationContext,
                    Intent(application.applicationContext, BackgroundService::class.java)
                )
            }
        } else {
            ContextCompat.startForegroundService(
                application.applicationContext,
                Intent(application.applicationContext, BackgroundService::class.java)
            )
        }
    }

    fun stopService() {
        application.applicationContext.stopService(
            Intent(application.applicationContext, BackgroundService::class.java)
        )
    }
}