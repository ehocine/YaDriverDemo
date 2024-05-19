package com.hocel.demodriver.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Mission : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var m_title: String = ""
    var m_desc: String = ""
    var tasks: RealmList<Task> = realmListOf()
    var cr_at: Long = 0L
    private var state: String = MissionStatus.Pending.status
    var status: MissionStatus
        get() {
            return try {
                MissionStatus.values().firstOrNull { it.status == state } ?: MissionStatus.Pending
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                MissionStatus.Pending
            }
        }
        set(value) {
            state = value.status
        }

}

class Task : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var t_name: String = ""
    var t_desc: String = ""
    var lat: Long = 0L
    var long: Long = 0L
    var dID: String? = null
    var client: String = ""
    private var state: String = TripStatus.Pending.status
    var status: TaskStatus
        get() {
            return try {
                TaskStatus.values().firstOrNull { it.status == state } ?: TaskStatus.Pending
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                TaskStatus.Pending
            }
        }
        set(value) {
            state = value.status
        }
}


fun generateSampleMission(): Mission {
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
    return mission
}