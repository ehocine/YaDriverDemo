package com.hocel.demodriver.data

import android.util.Log
import com.hocel.demodriver.model.Driver
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Mission
import com.hocel.demodriver.model.MissionStatus
import com.hocel.demodriver.model.Task
import com.hocel.demodriver.model.TaskStatus
import com.hocel.demodriver.util.Constants.APP_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.annotations.ExperimentalFlexibleSyncApi
import io.realm.kotlin.mongodb.ext.subscribe
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

object RepositoryImpl : Repository {
    val app = App.create(APP_ID)
    var user = app.currentUser
    lateinit var realm: Realm


    fun initialize() {
        configureCollections()
        //generateSampleMission()
    }

    override fun configureCollections() {
        if (user != null) {
            val config = SyncConfiguration.Builder(
                user!!,
                setOf(Driver::class, Task::class, Mission::class)
            )
                .initialSubscriptions { sub ->
                    add(query = sub.query<Driver>(query = "_id == $0", ObjectId(user!!.id)))
                    add(query = sub.query<Mission>())

                }
                .errorHandler { _, error ->
                    Log.e("syncError", "syncError", error)

                }
                .build()
            realm = Realm.open(config)
        }
    }

    override fun createDriver(mEmail: String) {
        val driver = Driver().apply {
            email = mEmail
            status = DriverStatus.Offline
        }
        if (user != null) {
            CoroutineScope(Dispatchers.IO).launch {
                realm.write {
                    try {
                        copyToRealm(driver.apply { _id = ObjectId(user!!.id) })
                    } catch (e: Exception) {
                        Log.d("MongoRepository", e.message.toString())
                    }
                }
            }
        }
    }

    fun generateSampleMission() {
        val mission = Mission().apply {
            m_title = "Sample Mission"
            m_desc = "This is a sample mission with 5 tasks."
            cr_at = System.currentTimeMillis()
            status = MissionStatus.Pending
        }

        val tasks = List(5) { index ->
            Task().apply {
                t_name = "Task ${index + 1}"
                t_desc = "Description for task ${index + 1}"
                lat = 12345678L + index
                long = 87654321L + index
                dID = "dID_${index + 1}"
                client = "Client ${index + 1}"
                status = TaskStatus.Pending
            }
        }

        mission.tasks.addAll(tasks)
        if (user != null) {
            CoroutineScope(Dispatchers.IO).launch {
                realm.write {
                    try {
                        copyToRealm(mission)
                    } catch (e: Exception) {
                        Log.d("MongoRepository", e.message.toString())
                    }
                }
            }
        }
    }

    fun getUserData2(): Flow<ResultsChange<Driver>> {
        val l = realm.query<Driver>(query = "_id == $0", ObjectId(this.user!!.id))
            .asFlow()
        return l
    }

    override fun getUserData(): Flow<ResultsChange<Driver>> {
        return realm.query<Driver>(query = "_id == $0", ObjectId(this.user!!.id))
            .find().asFlow()
    }

    override suspend fun taskAction(task: Task, driver: Driver, action: TaskStatus?) {
        if (user != null) {
            action?.let {
                realm.write {
                    try {
                        findLatest(task)?.let {
                            it.apply {
                                status = action
                                if (action == TaskStatus.StartTask) dID = user!!.id
                            }
                        }
                        findLatest(driver)?.let {
                            it.apply {
                                when (action) {
                                    TaskStatus.StartTask -> it.curMiD =
                                        task.mission._id.toHexString()

                                    else -> Unit
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
                    query<Driver>(query = "_id == $0", ObjectId(user!!.id))
                        .find()
                        .first()
                run {
                    queriedDriver.dLoca = "($lat , $lng)"
                    queriedDriver.lastTrac = RealmInstant.now()
                }
            }
        }
    }

    override fun getMissionById(missionId: String): Flow<ResultsChange<Mission>> {
        return realm.query<Mission>(query = "_id == $0", ObjectId(missionId))
            .asFlow()
    }
//    override suspend fun getTask(task: Task): Flow<Task?> {
//        realm.query<Task>()
//    }

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