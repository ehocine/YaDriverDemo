package com.hocel.demodriver.model

enum class TripStatus(val status: String) {
    Pending("pending"),
    Accepted("accepted"),
    Canceled("canceled"),
    GoToPickUp("go_to_pickup"),
    ArrivedToPickUp("arrived_to_pickup"),
    StartTrip("start_trip"),
    Finished("finished"),
    Closed("closed")
}
