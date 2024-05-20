package com.hocel.demodriver.screen.home

import android.os.Build
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
import com.hocel.demodriver.model.Mission
import com.hocel.demodriver.model.Task
import com.hocel.demodriver.model.TaskStatus
import com.hocel.demodriver.services.backgroundService.ServiceManager
import com.hocel.demodriver.services.tracking.TrackingServiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    var currentMission: MutableStateFlow<Mission?> = MutableStateFlow(null)
        private set
    var currentTask: MutableState<Task?> = mutableStateOf(null)
        private set
    var userData = MutableStateFlow(Driver())
        private set

    var currentLocation: MutableStateFlow<LatLng> = MutableStateFlow(LatLng(0.0, 0.0))
        private set

    private var locationJob: Job? = null

    var sheetContentState = mutableStateOf(SheetContentState.NONE)
        private set

    init {
        RepositoryImpl.initialize()
        serviceManager.startService()
        startLocationUpdate()
        viewModelScope.launch {
            RepositoryImpl.getUserData2()
                .collect { userResult ->
                    userResult.list.firstOrNull()?.let { user ->
                        userData.emit(user)
                        viewModelScope.launch {
                            if (user.miD.isNotBlank()) {
                                if (currentTask.value == null) setSheetContentState(
                                    SheetContentState.MISSION
                                )
                                getMissionById(user.miD)
                            }else{
                                setSheetContentState(
                                    SheetContentState.NONE
                                )
                            }
                            if (user.curMiD.isNotBlank() && user.curMiD != user.miD) {
                                if (currentTask.value == null) setSheetContentState(
                                    SheetContentState.MISSION
                                )
                                getMissionById(user.curMiD)
                            }
                            if (user.status == DriverStatus.Online) trackingService.startTracking()
                        }
                    }
                }
        }
    }

    private suspend fun getMissionById(missionId: String) {
        RepositoryImpl.getMissionById(missionId)
            .collect { missionResult ->
                missionResult.list.firstOrNull()?.let { mission ->
                    currentMission.emit(mission)
                }
            }
    }

    fun setSheetContentState(state: SheetContentState) {
        sheetContentState.value = state
    }

    fun selectTask(task: Task) {
        viewModelScope.launch {
            setSheetContentState(SheetContentState.TASK)
            currentMission.collect { mission ->
                mission?.let {
                    currentTask.value = it.tasks.find { it2 -> it2.t_id == task.t_id }
                }
            }
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

    fun taskAction(task: Task, driver: Driver, action: TaskStatus?) {
        viewModelScope.launch {
            RepositoryImpl.taskAction(task, driver, action)
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

//@RequiresApi(Build.VERSION_CODES.O)
//    private fun handleMissionEvent(mission: Mission) {
//        when (mission.status) {
//            TripStatus.Pending -> {
//                if (userData.value.status == DriverStatus.Online) {
//                    ringtoneManager.startRinging()
//                }
//            }
//
//            TripStatus.Accepted -> {
//                ringtoneManager.stopRinging()
//                currentMission.value = mission
//            }
//
//            TripStatus.GoToPickUp -> {
//                currentMission.value = mission
//            }
//
//            TripStatus.ArrivedToPickUp -> {
//                currentMission.value = mission
//            }
//
//            TripStatus.StartTrip -> {
//                currentMission.value = mission
//            }
//
//            TripStatus.Finished -> {
//                currentMission.value = mission
//            }
//
//            TripStatus.Closed -> {
//                currentMission.value = mission
//            }
//
//            TripStatus.Canceled -> {
//                currentMission.value = mission
//            }
//
//            else -> Unit
//        }
//    }