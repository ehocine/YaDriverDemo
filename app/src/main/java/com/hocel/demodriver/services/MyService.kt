package com.hocel.demodriver.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyService :Service() {
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        return super.onStartCommand(intent, flags, startId)
    }

//    private fun handleTripEvent(trip: Trip) {
//        when (trip.status) {
//            TripStatus.Pending -> {
//                if (userData.value.status == DriverStatus.Online) {
//                    ringtoneManager.startRinging()
//                    tripAction.value = TripFlowAction.Pending
//                    currentTrip.value = trip
//                }
//            }
//
//            else -> Unit
//        }
//    }
}