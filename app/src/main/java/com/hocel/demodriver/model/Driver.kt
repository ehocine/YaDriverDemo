package com.hocel.demodriver.model

import io.realm.kotlin.ext.realmListOf
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

