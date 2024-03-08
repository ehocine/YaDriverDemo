package com.hocel.demodriver.model

enum class TripFlowAction {
    Idle,
    Pending,
    Accepted,
    GoToPickUp,
    StartTrip,
    EndTrip,
    CancelTrip
}