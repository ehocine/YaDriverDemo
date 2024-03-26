package com.hocel.demodriver.data

import android.util.Log
import com.hocel.demodriver.model.Driver
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Rider
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.model.TripStatus
import com.hocel.demodriver.util.Constants.APP_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.annotations.ExperimentalFlexibleSyncApi
import io.realm.kotlin.mongodb.ext.subscribe
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
                setOf(Driver::class, Trip::class)
            )
                .initialSubscriptions { sub ->
                    add(query = sub.query<Driver>(query = "_id == $0", ObjectId(user.id)))
                    add(query = sub.query<Trip>())
                    // add(query = sub.query<Rider>(query = "_id == $0", ObjectId(user.id)))
                }
                .errorHandler { _, error ->
                    Log.e("syncError", "syncError", error)

                }
                //  .log(LogLevel.ALL)
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

    override suspend fun tripAction(trip: Trip, driver: Driver, action: TripStatus) {
        if (user != null) {
            realm.write {
                try {
                    findLatest(trip)?.let {
                        it.apply {
                            status = action
                            if (action == TripStatus.Accepted) dID = user.id
                        }
                    }
                    findLatest(driver)?.let {
                        it.apply {
                            when (action) {
                                TripStatus.Accepted -> it.curTiD = trip._id.toHexString()
                                TripStatus.Canceled, TripStatus.Closed -> {
                                    it.curTiD = ""
                                    it.trRiD = ""
                                }

                                else -> Unit
                            }
                            if (action == TripStatus.Accepted) it.curTiD =
                                trip._id.toHexString() else Unit
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override suspend fun switchDriverStatus(driver: Driver, nStatus: DriverStatus) {
        if (user != null) {
            realm.write {
                findLatest(driver)?.let {
                    it.apply {
                        status = nStatus
                    }
                }
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
                    queriedDriver.dLoca = "($lat , $lng)"
                    queriedDriver.lastTrac = RealmInstant.now()
                }
            }
        }
    }

    @OptIn(ExperimentalFlexibleSyncApi::class)
    override suspend fun getTripById(tripId: String): Flow<Trip?> {
        Log.d("EventTime", "Event Repo: in: ${System.currentTimeMillis()}")
        val trip = realm.query<Trip>(query = "_id == $0", ObjectId(tripId))
            .subscribe("Trip", updateExisting = true).firstOrNull()?.asFlow()?.map { it.obj }
        return trip ?: flowOf(null)
    }

    override suspend fun updateProfileInfo(driver: Driver, nName: String, nEmail: String) {
        if (user != null) {
            realm.write {
                findLatest(driver)?.let {
                    it.apply {
                        name = nName
                        email = nEmail
                    }
                }
            }
        }
    }
}