package com.stevdza.san.demodriver.data

import com.hocel.demodriver.model.Trip
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface Repository {
    fun configureTheRealm()
    fun getData(): Flow<List<Trip>>
    suspend fun createTrip(trip: Trip)
    suspend fun cancelTrip(id: ObjectId)
}