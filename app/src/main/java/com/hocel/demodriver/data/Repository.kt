package com.hocel.demodriver.data

import com.hocel.demodriver.model.Driver
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Mission
import com.hocel.demodriver.model.Task
import com.hocel.demodriver.model.TripStatus
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun configureCollections()
    fun createDriver(mEmail: String)
    fun getUserData(): Flow<ResultsChange<Driver>>
    suspend fun taskAction(task: Task, driver: Driver, action: TripStatus)
    suspend fun switchDriverStatus(driver: Driver, nStatus: DriverStatus)
    suspend fun sendLocation(lat: Double, lng: Double)
    suspend fun getMissionById(missionId: String): Flow<Mission?>
    suspend fun updateProfileInfo(driver: Driver, nName: String, nEmail: String)
}