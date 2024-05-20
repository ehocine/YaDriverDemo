package com.hocel.demodriver.model

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

