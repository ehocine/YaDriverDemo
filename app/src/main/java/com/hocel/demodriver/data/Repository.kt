package com.hocel.demodriver.data

import com.hocel.demodriver.model.Driver
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.model.TripStatus
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface Repository {
    fun configureCollections()
    fun createDriver()
    suspend fun getUserData(): Flow<Driver?>
    suspend fun tripAction(tripId: ObjectId, action: TripStatus)
    suspend fun switchDriverStatus(status: DriverStatus)
    suspend fun sendLocation(lat: Double, lng: Double)
    suspend fun getTripById(tripId: String): Flow<Trip?>
    suspend fun updateProfileInfo(name: String, email: String)
}