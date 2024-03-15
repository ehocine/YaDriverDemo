package com.hocel.demodriver.data

import android.util.Log
import com.hocel.demodriver.model.Driver
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Rider
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.model.TripStatus
import com.hocel.demodriver.util.Constants.APP_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.annotations.ExperimentalFlexibleSyncApi
import io.realm.kotlin.mongodb.ext.subscribe
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.WaitForSync
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

object RepositoryImpl : Repository {
    private val app = App.create(APP_ID)
    val user = app.currentUser
    lateinit var realm: Realm

    fun initialize() {
        configureCollections()
        createDriver()
    }

    override fun configureCollections() {
        if (user != null) {
            val config = SyncConfiguration.Builder(
                user,
                setOf(Driver::class, Trip::class, Rider::class)
            )
                .initialSubscriptions(rerunOnOpen = true) { sub ->
                    add(query = sub.query<Driver>(query = "_id == $0", ObjectId(user.id)))
                    add(query = sub.query<Trip>(query = "_id == $0", ObjectId(user.id)))
                    add(query = sub.query<Rider>(query = "_id == $0", ObjectId(user.id)))
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    override fun createDriver() {
        val driver = Driver().apply {
            status = DriverStatus.Offline
        }
        if (user != null) {
            CoroutineScope(Dispatchers.IO).launch {
                realm.write {
                    try {
                        copyToRealm(driver.apply { _id = ObjectId(user.id) })
                    } catch (e: Exception) {
                        Log.d("MongoRepository", e.message.toString())
                    }
                }
            }
        }
    }

    override suspend fun getUserData(): Flow<Driver?> {
        return realm.query<Driver>(query = "_id == $0", ObjectId(user!!.id))
            .find().firstOrNull()?.asFlow()?.map { it.obj } ?: flow { null }
    }

    override suspend fun tripAction(tripId: ObjectId, action: TripStatus) {
        if (user != null) {
            realm.write {
                val queriedTrip =
                    query<Trip>(query = "_id == $0", tripId)
                        .find()
                        .first()
                queriedTrip.status = action
                if (action == TripStatus.Accepted) queriedTrip.driverId = user.id
                val queriedDriver =
                    query<Driver>(query = "_id == $0", ObjectId(user.id))
                        .find()
                        .first()
                run {
                    when (action) {
                        TripStatus.Accepted -> queriedDriver.currentTripId = tripId.toHexString()
                        TripStatus.Canceled, TripStatus.Closed -> {
                            queriedDriver.currentTripId = ""
                            queriedDriver.tripRequestId = ""
                        }

                        else -> Unit
                    }
                    if (action == TripStatus.Accepted) queriedDriver.currentTripId =
                        tripId.toHexString() else Unit
                }
            }
        }
    }

    override suspend fun switchDriverStatus(status: DriverStatus) {
        if (user != null) {
            realm.write {
                val queriedDriver =
                    query<Driver>(query = "_id == $0", ObjectId(user.id))
                        .find()
                        .first()
                queriedDriver.status = status
            }
        }
    }

    override suspend fun sendLocation(lat: Double, lng: Double) {
        if (user != null) {
            realm.write {
                val queriedDriver =
                    query<Driver>(query = "_id == $0", ObjectId(user.id))
                        .find()
                        .first()
                run {
                    queriedDriver.driverLocation = "($lat , $lng)"
                    queriedDriver.lastTracking = RealmInstant.now()
                }
            }
        }
    }

    @OptIn(ExperimentalFlexibleSyncApi::class)
    override suspend fun getTripById(tripId: String): Flow<Trip?> {
        return realm.query<Trip>(query = "_id == $0", ObjectId(tripId))
            .subscribe("Trip", updateExisting = true).first().asFlow().map { it.obj }
    }
}