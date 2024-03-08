package com.hocel.demodriver.data

import android.util.Log
import com.hocel.demodriver.model.Driver
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.model.TripStatus
import com.hocel.demodriver.util.Constants.APP_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

object RepositoryImpl : Repository {
    private val app = App.create(APP_ID)
    val user = app.currentUser
    private lateinit var realm: Realm

    fun initialize() {
        configureCollections()
        createDriver()
        //insertTrip()
    }

    override fun configureCollections() {
        if (user != null) {
            val config = SyncConfiguration.Builder(
                user,
                setOf(Driver::class, Trip::class)
            )
                .initialSubscriptions { sub ->
                    add(query = sub.query<Driver>(query = "owner_id == $0", user.id))
                    add(query = sub.query<Trip>(query = "owner_id == $0", user.id))
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    fun insertTrip() {
        val trip = Trip().apply {
            owner_id = user?.id.toString()
            client = "Hakim"
            pickUpAddress = "Sidi Yahia , Said hamdin"
            dropOffAddress = "Boulveard Didouche  Mourad , Alger centre"
            createdAt = RealmInstant.now()
            status = TripStatus.Pending
        }
        if (user != null) {
            CoroutineScope(Dispatchers.IO).launch {
                realm.write {
                    try {
                        copyToRealm(trip)
                    } catch (e: Exception) {
                        Log.d("MongoRepository", e.message.toString())
                    }
                }
            }

        }
    }

    override fun createDriver() {
        val driver = Driver().apply {
            owner_id = user?.id.toString()
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

    override suspend fun getUserData(): Flow<ResultsChange<Driver>> {
        return realm.query<Driver>(query = "_id == $0", ObjectId(user!!.id))
            .find().asFlow()
    }

    override suspend fun tripAction(tripId: ObjectId, action: TripStatus) {
        if (user != null) {
            realm.write {
                val queriedTrip =
                    query<Trip>(query = "_id == $0", tripId)
                        .first()
                        .find()
                if (queriedTrip != null) {
                    queriedTrip.status = action
                    if (action == TripStatus.Accepted) queriedTrip.driverId = user.id
                } else {
                    Log.d("MongoRepository", "Queried Driver does not exist.")
                }
                val queriedDriver =
                    query<Driver>(query = "_id == $0", ObjectId(user.id))
                        .first()
                        .find()
                if (queriedDriver != null) {
                    if (action == TripStatus.Accepted) queriedDriver.currentTripId =
                        tripId.toHexString() else Unit
                } else {
                    Log.d("MongoRepository", "Queried Driver does not exist.")
                }
            }
        }
    }

    override suspend fun readIncomingTrip(): Flow<List<Trip>> {
        return realm.query<Trip>().find().asFlow().map { it.list }
    }

    override suspend fun switchDriverStatus(status: DriverStatus) {
        if (user != null) {
            realm.write {
                val queriedDriver =
                    query<Driver>(query = "_id == $0", ObjectId(user.id))
                        .first()
                        .find()
                if (queriedDriver != null) {
                    queriedDriver.status = status
                } else {
                    Log.d("MongoRepository", "Queried Driver does not exist.")
                }
            }
        }
    }

    override suspend fun cancelTrip(id: ObjectId) {

    }
}