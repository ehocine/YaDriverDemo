package com.hocel.demodriver.model

enum class TripStatus(val status: String) {
    Pending("pending"),
    Accepted("accepted"),
    Canceled("canceled"),
    ArrivedToPickUp("arrived_to_pickup"),
    Finished("finished")
}
