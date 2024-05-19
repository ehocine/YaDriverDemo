package com.hocel.demodriver.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Driver : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var name: String = ""
    var email: String = ""
    var curMiD: String = ""
    var miD: String = ""
    var dLoca: String? = null
    var lastTrac: RealmInstant? = null
    private var state: String = DriverStatus.Offline.status
    var status: DriverStatus
        get() {
            return try {
                DriverStatus.values().firstOrNull { it.status == state } ?: DriverStatus.Offline
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                DriverStatus.Offline
            }
        }
        set(value) {
            state = value.status
        }
}

class Mission : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var m_title: String = ""
    var m_desc: String = ""
    var tasks: RealmList<Task> = realmListOf()
    var cr_at: Long = 0L
    private var state: String = TripStatus.Pending.status
    var status: TripStatus
        get() {
            return try {
                TripStatus.values().firstOrNull { it.status == state } ?: TripStatus.Pending
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                TripStatus.Pending
            }
        }
        set(value) {
            state = value.status
        }

}

class Task : EmbeddedRealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var t_name: String = ""
    var t_desc: String = ""
    var lat: Long = 0L
    var long: Long = 0L
    var dID: String? = null
    var client: String = ""
    private var state: String = TripStatus.Pending.status
    var status: TripStatus
        get() {
            return try {
                TripStatus.values().firstOrNull { it.status == state } ?: TripStatus.Pending
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                TripStatus.Pending
            }
        }
        set(value) {
            state = value.status
        }
}