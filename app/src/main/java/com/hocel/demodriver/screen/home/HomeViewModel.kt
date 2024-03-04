package com.hocel.demodriver.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hocel.demodriver.model.DriverStatus

class HomeViewModel : ViewModel() {

    private var _driverStatus: MutableState<DriverStatus> = mutableStateOf(DriverStatus.Offline)
    val driverStatus: State<DriverStatus> = _driverStatus

    fun switchStatus(des: Boolean) {

    }

    fun acceptTrip(tripId: String) {

    }

    fun declineTrip(tripId: String) {

    }

    fun setDriverStatus(status: DriverStatus) {
        _driverStatus.value = status
    }

    fun cancelTrip() {

    }
}