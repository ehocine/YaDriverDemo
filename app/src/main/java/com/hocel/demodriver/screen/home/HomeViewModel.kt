package com.hocel.demodriver.screen.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.hocel.demodriver.common.LocationProviderManager
import com.hocel.demodriver.common.RingtoneManager
import com.hocel.demodriver.data.RepositoryImpl
import com.hocel.demodriver.model.Driver
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.model.TripFlowAction
import com.hocel.demodriver.model.TripStatus
import com.hocel.demodriver.services.backgroundService.ServiceManager
import com.hocel.demodriver.services.tracking.TrackingServiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val serviceManager: ServiceManager,
    private val trackingService: TrackingServiceManager,
    private val ringtoneManager: RingtoneManager,
    private val _locationProvider: LocationProviderManager,
) : ViewModel() {

    var currentTrip = mutableStateOf(Trip())
        private set
    var currentTripId = mutableStateOf("")
        private set

    var tripAction = mutableStateOf(TripFlowAction.Idle)
        private set
    var userData = MutableStateFlow(Driver())
        private set

    var currentLocation: MutableStateFlow<LatLng> = MutableStateFlow(LatLng(0.0, 0.0))
        private set

    private var locationJob: Job? = null

    init {
        RepositoryImpl.initialize()
        serviceManager.startService()
        startLocationUpdate()
        viewModelScope.launch {
            delay(300)
            RepositoryImpl.getUserData()
                .collect { user ->
                    user?.let {
                        userData.emit(user)
                        viewModelScope.launch {
                            if (user.trRiD.isNotBlank()) {
                                getUserTrip(user.trRiD)
                            }
                            if (user.curTiD.isNotBlank() && user.curTiD != user.trRiD) {
                                getUserTrip(user.curTiD)
                            }
                            if (user.status == DriverStatus.Online) trackingService.startTracking()
                        }
                    }
                }
        }
    }

    private suspend fun getUserTrip(tripId: String) {
        RepositoryImpl.getTripById(tripId)
            .collect { trip ->
                trip?.let {
                    currentTrip.value = it
                    handleTripEvent(it)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleTripEvent(trip: Trip) {
        when (trip.status) {
            TripStatus.Pending -> {
                Log.d("EventTime", "Event ViewModel: in: ${System.currentTimeMillis()}")
                if (userData.value.status == DriverStatus.Online) {
                    ringtoneManager.startRinging()
                    tripAction.value = TripFlowAction.Pending
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

            TripStatus.Canceled -> {
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
            RepositoryImpl.switchDriverStatus(userData.value, status)
        }
    }

    fun tripAction(trip: Trip, driver: Driver, action: TripStatus) {
        viewModelScope.launch {
            RepositoryImpl.tripAction(trip, driver, action)
        }
    }

    fun declineTrip() {
        ringtoneManager.stopRinging()
    }

    private fun startLocationUpdate() {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            _locationProvider.requestLocationUpdate {
                currentLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }

    fun getCurrentPosition() {
        viewModelScope.launch {
            _locationProvider.currentLocation()?.let {
                currentLocation.value = it
            }
        }
    }

    fun updateProfileInfo(driver: Driver, name: String, email: String) {
        viewModelScope.launch {
            RepositoryImpl.updateProfileInfo(driver, name, email)
        }
    }
}