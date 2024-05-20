package com.hocel.demodriver.services.backgroundService

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.hocel.demodriver.common.RingtoneManager
import com.hocel.demodriver.data.RepositoryImpl
import com.hocel.demodriver.util.Constants
import com.hocel.demodriver.util.Constants.TRACKING_NOTIFICATION_CHANNEL_ID
import com.hocel.demodriver.util.createNotificationChannelIfNotExist
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BackgroundService : Service() {
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var ringtoneManager: RingtoneManager

    @Inject
    lateinit var scope: CoroutineScope

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        RepositoryImpl.configureCollections()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannelIfNotExist(
                channelId = TRACKING_NOTIFICATION_CHANNEL_ID,
                channelName = Constants.TRACKING_NOTIFICATION_CHANNEL_NAME,
                importance = NotificationManager.IMPORTANCE_HIGH
            )
        }
        startForeground(Constants.NOTIFICATION_ID, baseNotificationBuilder.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            RepositoryImpl.getUserData2().collect { userResult ->
                userResult.list.firstOrNull()?.let { user ->
                    scope.launch {
                        if (user.miD.isNotBlank()) {
                            if (user.curMiD != user.miD) {
                                Log.d("MyTrip", "Trip: ${user.miD}")
                                getUserTrip(user.miD)
                            }
                        }
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannelIfNotExist(
                channelId = TRACKING_NOTIFICATION_CHANNEL_ID,
                channelName = Constants.TRACKING_NOTIFICATION_CHANNEL_NAME,
                importance = NotificationManager.IMPORTANCE_HIGH
            )
        }
        startForeground(Constants.NOTIFICATION_ID, baseNotificationBuilder.build())
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getUserTrip(tripId: String) {
        RepositoryImpl.getMissionById(tripId)
            .collect { trip ->
                trip?.let {
                   // handleTripEvent(it, applicationContext)
                }
            }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun handleTripEvent(trip: Task, context: Context) {
//        when (trip.status) {
//            TripStatus.Pending -> {
//                if (!context.isAppInForeground(context.packageName)) {
//                    pushRequestNotification(
//                        context = application.applicationContext,
//                        channelId = NOTIFICATION_CHANNEL_ID,
//                        channelName = NOTIFICATION_CHANNEL_NAME,
//                        title = "New trip",
//                        description = "You have a new trip request",
//                        intent = Intent(
//                            application.applicationContext,
//                            MainActivity::class.java
//                        ).apply {
//                            action = TripStatus.Pending.name
//                            putExtra("trip_id", trip._id.toHexString())
//                        }
//                    )
//                    ringtoneManager.startRinging()
//                }
//            }
//
//            else -> Unit
//        }
//    }
}

internal const val NOTIFICATION_CHANNEL_ID = "trips_channel"
internal const val NOTIFICATION_CHANNEL_NAME = "Trip Notification"