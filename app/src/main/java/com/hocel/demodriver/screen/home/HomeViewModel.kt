package com.hocel.demodriver.screen.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val serviceManager: ServiceManager,
    private val trackingService: TrackingServiceManager,
    private val ringtoneManager: RingtoneManager,
    private val _locationProvider: LocationProviderManager,
) : ViewModel() {

    var currentTrip: MutableState<Trip> = mutableStateOf(Trip())
        private set

    var tripAction = mutableStateOf(TripFlowAction.Idle)
        private set
    var userData = MutableStateFlow(Driver())
        private set

    private val _currentLocation: MutableStateFlow<LatLng> = MutableStateFlow(LatLng(0.0, 0.0))
    val currentLocation: StateFlow<LatLng> get() = _currentLocation

    private var locationJob: Job? = null

    init {
        RepositoryImpl.initialize()
        //serviceManager.startService()
        startLocationUpdate()
        viewModelScope.launch {
            RepositoryImpl.getUserData().collect { user ->
                user?.let {
                    userData.emit(user)
                    user.tripRequestId?.let { tripId ->
                        if (user.currentTripId != tripId) {
                            val newTrip = RepositoryImpl.getTripById(tripId)
                            Log.d("InTrip", "${newTrip}")
                            currentTrip.value = newTrip
                            handleTripEvent(newTrip)
                        }
                    }
                    if (user.status == DriverStatus.Online) trackingService.startTracking()
                }
            }
        }
//        viewModelScope.launch {
//            RepositoryImpl.readIncomingTrip().collect {
//                tripData.emit(it.filter { trip ->
//                    if (trip.status == TripStatus.Pending) {
//                        true
//                    } else if (trip.status != TripStatus.Pending && trip.driverId == RepositoryImpl.user?.id) {
//                        true
//                    } else {
//                        false
//                    }
//                })
//                if (tripData.value.isNotEmpty()) handleTripEvent(tripData.value.last())
//            }
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleTripEvent(trip: Trip) {
        when (trip.status) {
            TripStatus.Pending -> {
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
                //serviceManager.startService()
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

    private fun startLocationUpdate() {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            _locationProvider.requestLocationUpdate {
                _currentLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }

    fun getCurrentPosition() {
        viewModelScope.launch {
            _locationProvider.currentLocation()?.let {
                _currentLocation.value = it
            }
        }
    }
}