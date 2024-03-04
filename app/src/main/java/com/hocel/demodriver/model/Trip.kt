package com.hocel.demodriver.model

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Trip : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var owner_id: String = ""
    var name: String = ""
    var address: String? = null
    var timestamp: RealmInstant = RealmInstant.now()
    var tripAccepted: Boolean = false
}