package com.hocel.demodriver.model

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Driver : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var owner_id: String = ""
    var name: String = ""
    var email: String = ""
    var currentTripId: String? = null
    var driverLocation: String? = null
    var lastTracking: RealmInstant? = null
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
