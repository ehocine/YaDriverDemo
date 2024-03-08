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
    suspend fun readIncomingTrip(): Flow<List<Trip>>
    suspend fun getUserData(): Flow<ResultsChange<Driver>>
    suspend fun tripAction(tripId: ObjectId, action: TripStatus)
    suspend fun switchDriverStatus(status: DriverStatus)
    suspend fun cancelTrip(id: ObjectId)
}