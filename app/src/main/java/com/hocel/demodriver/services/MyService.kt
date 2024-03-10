package com.hocel.demodriver.services

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
import com.hocel.demodriver.MainActivity
import com.hocel.demodriver.R
import com.hocel.demodriver.common.RingtoneManager
import com.hocel.demodriver.data.RepositoryImpl
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.model.TripFlowAction
import com.hocel.demodriver.model.TripStatus
import com.hocel.demodriver.util.Constants
import com.hocel.demodriver.util.Constants.NOTIFICATION_CHANNEL_ID
import com.hocel.demodriver.util.createNotificationChannelIfNotExist
import com.hocel.demodriver.util.isServiceRunningInForeground
import com.hocel.demodriver.util.pushRequestNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyService : Service() {
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var ringtoneManager: RingtoneManager

    @Inject
    lateinit var scope: CoroutineScope

    private var tripData = MutableStateFlow(emptyList<Trip>())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        RepositoryImpl.configureCollections()
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            RepositoryImpl.readIncomingTrip().collect {
                tripData.emit(it.filter { trip ->
                    if (trip.status == TripStatus.Pending) {
                        true
                    } else if (trip.status != TripStatus.Pending && trip.driverId == RepositoryImpl.user?.id) {
                        true
                    } else {
                        false
                    }
                })
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notifManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notifManager.createNotificationChannelIfNotExist(
                        channelId = Constants.NOTIFICATION_CHANNEL_ID,
                        channelName = Constants.NOTIFICATION_CHANNEL_NAME,
                        importance = NotificationManager.IMPORTANCE_HIGH
                    )
                }
                startForeground(Constants.NOTIFICATION_ID, baseNotificationBuilder.build())
                if (tripData.value.isNotEmpty()) handleTripEvent(tripData.value.last())
            }
        }

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleTripEvent(trip: Trip) {
        when (trip.status) {
            TripStatus.Pending -> {
                pushRequestNotification(
                    context = application.applicationContext,
                    channelId = NOTIFICATION_CHANNEL_ID,
                    channelName = Constants.NOTIFICATION_CHANNEL_NAME,
                    title = "New trip",
                    description = "You have a new trip request",
                    intent = Intent(
                        application.applicationContext,
                        MainActivity::class.java
                    ).apply {
                        action = TripFlowAction.Pending.name
                        putExtra("trip_id", trip.owner_id)
                    }
                )
                ringtoneManager.startRinging()
                Log.d("MyService", "Got a trip: ${trip.client}")
            }

            else -> Unit
        }
    }
}