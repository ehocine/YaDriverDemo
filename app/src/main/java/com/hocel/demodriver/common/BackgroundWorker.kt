package com.hocel.demodriver.common

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.model.TripFlowAction
import com.hocel.demodriver.model.TripStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class BackgroundWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    private val ringtoneManager: RingtoneManager
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {

            Result.success()
        } catch (e: Exception) {
            // Handle errors
            Result.failure()
        }
    }

    private fun handleTripEvent(trip: Trip) {
        when (trip.status) {
            TripStatus.Pending -> {
                ringtoneManager.startRinging()
            }

            else -> Unit
        }
    }
}