package com.hocel.demodriver.screen.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hocel.demodriver.common.RingtoneManager
import com.hocel.demodriver.data.RepositoryImpl
import com.hocel.demodriver.model.Driver
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.model.TripFlowAction
import com.hocel.demodriver.model.TripStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ringtoneManager: RingtoneManager
) : ViewModel() {

    var tripData = MutableStateFlow(emptyList<Trip>())

    var currentTrip: MutableState<Trip> = mutableStateOf(Trip())
        private set

    var driverAction: MutableState<TripFlowAction> = mutableStateOf(TripFlowAction.Idle)

    var tripAction = mutableStateOf(TripFlowAction.Idle)
        private set
    var userData = MutableStateFlow(Driver())
        private set

    init {
        RepositoryImpl.initialize()
        viewModelScope.launch {
            RepositoryImpl.getUserData().collect {
                if (it.list.isNotEmpty()) userData.emit(it.list.first())
            }
        }
        viewModelScope.launch {
            RepositoryImpl.readIncomingTrip().collect {
                tripData.emit(it)
            }
        }
        viewModelScope.launch {
            delay(300)
            handleTripEvent(tripData.value.first())
        }
    }

    private fun handleTripEvent(trip: Trip) {
        Log.d("MyTrip", "${trip.status}")
        when (trip.status) {
            TripStatus.Pending -> {
                ringtoneManager.startRinging()
                tripAction.value = TripFlowAction.Pending
                currentTrip.value = trip
            }

            TripStatus.Accepted -> {
                ringtoneManager.stopRinging()
                tripAction.value = TripFlowAction.Accepted
                currentTrip.value = trip

            }

            else -> {

            }
        }
    }

    fun switchStatus(status: DriverStatus) {
        viewModelScope.launch {
            RepositoryImpl.switchDriverStatus(status)
        }
    }

    fun acceptTrip(tripId: ObjectId) {
        viewModelScope.launch {
            RepositoryImpl.acceptTrip(tripId)
        }
    }

    fun declineTrip(tripId: String) {

    }

    fun cancelTrip() {

    }
}