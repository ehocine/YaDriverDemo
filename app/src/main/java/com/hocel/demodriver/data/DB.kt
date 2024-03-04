package com.stevdza.san.demodriver.data

import android.util.Log
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.util.Constants.APP_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

object DB : Repository {
    private val app = App.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(
                user,
                setOf(Trip::class)
            )
                .initialSubscriptions { sub ->
                    add(query = sub.query<Trip>(query = "owner_id == $0", user.id))
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    override fun getData(): Flow<List<Trip>> {
        return realm.query<Trip>().asFlow().map { it.list }
    }

    override suspend fun createTrip(trip: Trip) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(trip.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

//    override suspend fun updatePerson(driver: Driver) {
//        realm.write {
//            val queriedDriver =
//                query<Driver>(query = "_id == $0", driver._id)
//                    .first()
//                    .find()
//            if (queriedDriver != null) {
//                queriedDriver.name = driver.name
//            } else {
//                Log.d("MongoRepository", "Queried Person does not exist.")
//            }
//        }
//    }

//    override suspend fun deletePerson(id: ObjectId) {
//        realm.write {
//            try {
//                val driver = query<Driver>(query = "_id == $0", id)
//                    .first()
//                    .find()
//                driver?.let { delete(it) }
//            } catch (e: Exception) {
//                Log.d("MongoRepository", "${e.message}")
//            }
//        }
//    }

    override suspend fun cancelTrip(id: ObjectId) {

    }
}