package com.hocel.demodriver.model

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Trip : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var client: String = ""
    var pickUAd: String = ""
    var dropOAd: String = ""
    var crAt: RealmInstant = RealmInstant.now()
    var dID: String? = null
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