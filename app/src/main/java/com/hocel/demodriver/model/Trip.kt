package com.hocel.demodriver.model

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Trip : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var owner_id: String = ""
    var client: String = ""
    var pickUpAddress: String = ""
    var dropOffAddress: String = ""
    var createdAt: RealmInstant = RealmInstant.now()
    var driverId: String? = null
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