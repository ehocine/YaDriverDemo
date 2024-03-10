package com.hocel.demodriver.services

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import com.hocel.demodriver.util.isServiceRunningInForeground
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceManager @Inject constructor(
    private val application: Application,
) {

    fun startService() {
        if (application.isServiceRunningInForeground(MyService::class.java)) return
        ContextCompat.startForegroundService(
            application.applicationContext,
            Intent(application.applicationContext, MyService::class.java)
        )
    }

    fun stopService() {
        application.applicationContext.stopService(
            Intent(application.applicationContext, MyService::class.java)
        )
    }
}