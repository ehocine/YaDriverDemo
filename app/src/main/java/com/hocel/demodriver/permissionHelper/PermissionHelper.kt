package com.hocel.demodriver.permissionHelper

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.hocel.demodriver.MainActivity
import javax.inject.Inject

class PermissionHelper @Inject constructor(
    val application: Application,
) {

    val context: Context get() = application.applicationContext
    fun hasLocationPermissions(): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun askForNotificationPermission(
        launcher: ActivityResultLauncher<String>
    ) {
        launcher.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun ComponentActivity.askForNotificationPermission(
    permissionHelper: PermissionHelper
) {
    if (!permissionHelper.hasNotificationPermission()) {
        (this@askForNotificationPermission as? MainActivity)?.notificationPermissionLauncher?.let {
            permissionHelper.askForNotificationPermission(it)
        }
    }
}