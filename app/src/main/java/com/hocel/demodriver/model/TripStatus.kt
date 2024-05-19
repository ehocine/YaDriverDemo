package com.hocel.demodriver.model

enum class TripStatus(val status: String) {
    Pending("pen"),
    Accepted("acce"),
    Canceled("canc"),
    GoToPickUp("go_to_pi"),
    ArrivedToPickUp("arrived_to_pi"),
    StartTrip("start_tr"),
    Finished("fnsh"),
    Closed("closed"),
    CanceledTrip("canceled_tr")
}

enum class MissionStatus(val status: String) {
    Pending("pen"),
    StartMission("start_ms"),
    Finished("fnsh_ms"),

}

enum class TaskStatus(val status: String) {
    Pending("pen"),
    StartTask("start_ts"),
    Finished("fnsh_ts"),

}

