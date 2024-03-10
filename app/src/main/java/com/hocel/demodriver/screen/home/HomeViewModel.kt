package com.hocel.demodriver.screen.home

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hocel.demodriver.MainActivity
import com.hocel.demodriver.common.RingtoneManager
import com.hocel.demodriver.data.RepositoryImpl
import com.hocel.demodriver.model.Driver
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.model.TripFlowAction
import com.hocel.demodriver.model.TripStatus
import com.hocel.demodriver.services.MyService
import com.hocel.demodriver.services.ServiceManager
import com.hocel.demodriver.services.tracking.TrackingServiceManager
import com.hocel.demodriver.util.Constants
import com.hocel.demodriver.util.pushRequestNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application,
    private val serviceManager: ServiceManager,
    private val trackingService: TrackingServiceManager,
    private val ringtoneManager: RingtoneManager
) : ViewModel() {

    var tripData = MutableStateFlow(emptyList<Trip>())

    var currentTrip: MutableState<Trip> = mutableStateOf(Trip())
        private set

    var tripAction = mutableStateOf(TripFlowAction.Idle)
        private set
    var userData = MutableStateFlow(Driver())
        private set

    init {
        RepositoryImpl.initialize()
        serviceManager.startService()
        viewModelScope.launch {
            RepositoryImpl.getUserData().collect {
                if (it.list.isNotEmpty()) {
                    userData.emit(it.list.first())
                    if (it.list.first().status == DriverStatus.Online) trackingService.startTracking()
                }
            }
        }
        viewModelScope.launch {
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
                if (tripData.value.isNotEmpty()) handleTripEvent(tripData.value.last())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleTripEvent(trip: Trip) {
        when (trip.status) {
            TripStatus.Pending -> {
                if (userData.value.status == DriverStatus.Online) {
                    ringtoneManager.startRinging()
                    tripAction.value = TripFlowAction.Pending
                    currentTrip.value = trip
                }
            }

            TripStatus.Accepted -> {
                ringtoneManager.stopRinging()
                tripAction.value = TripFlowAction.Accepted
                currentTrip.value = trip

            }

            TripStatus.GoToPickUp -> {
                tripAction.value = TripFlowAction.GoToPickUp
                currentTrip.value = trip
            }

            TripStatus.ArrivedToPickUp -> {
                tripAction.value = TripFlowAction.ArrivedToPickUp
                currentTrip.value = trip
            }

            TripStatus.StartTrip -> {
                tripAction.value = TripFlowAction.StartTrip
                currentTrip.value = trip
            }

            TripStatus.Finished -> {
                tripAction.value = TripFlowAction.EndTrip
                currentTrip.value = trip
            }

            TripStatus.Closed -> {
                tripAction.value = TripFlowAction.Closed
                currentTrip.value = trip
            }

            TripStatus.CanceledTrip -> {
                tripAction.value = TripFlowAction.CancelTrip
                currentTrip.value = trip
            }

            else -> Unit
        }
    }

    fun switchStatus(status: DriverStatus) {
        viewModelScope.launch {
            if (status == DriverStatus.Online) {
                serviceManager.startService()
                trackingService.startTracking()
            } else {
                serviceManager.stopService()
                trackingService.stopTracking()
            }
            RepositoryImpl.switchDriverStatus(status)
        }
    }

    fun tripAction(tripId: ObjectId, action: TripStatus) {
        viewModelScope.launch {
            RepositoryImpl.tripAction(tripId, action)
        }
    }

    fun declineTrip() {
        ringtoneManager.stopRinging()
    }
}