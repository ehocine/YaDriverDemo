package com.hocel.demodriver.model

enum class TripFlowAction {
    Idle,
    Pending,
    Accepted,
    GoToPickUp,
    ArrivedToPickUp,
    StartTrip,
    EndTrip,
    Closed,
    CancelTrip
}