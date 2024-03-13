package com.hocel.demodriver.services.tracking

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.hocel.demodriver.common.LocationProviderManager
import com.hocel.demodriver.data.RepositoryImpl
import com.hocel.demodriver.util.Constants
import com.hocel.demodriver.util.createNotificationChannelIfNotExist
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var locationProviderManager: LocationProviderManager

    @Inject
    lateinit var scope: CoroutineScope
    override fun onCreate() {
        super.onCreate()
        startLocationUpdate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannelIfNotExist(
                channelId = Constants.TRACKING_NOTIFICATION_CHANNEL_ID,
                channelName = Constants.TRACKING_NOTIFICATION_CHANNEL_NAME,
                importance = NotificationManager.IMPORTANCE_HIGH
            )
        }
        startForeground(Constants.NOTIFICATION_ID, baseNotificationBuilder.build())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationProviderManager.stopLocationUpdate()
    }

    private fun startLocationUpdate() {
        locationProviderManager.requestLocationUpdate { location ->
            sendData(location)
        }
    }

    private fun sendData(location: Location?) {
        location?.let {
            scope.launch {
                //RepositoryImpl.sendLocation(lat = it.latitude, lng = it.longitude)
            }
        }
    }
}