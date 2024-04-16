package com.hocel.demodriver.navigation

sealed class Screen(val route: String) {
    object Login : Screen(route = "login_screen")
    object Signup : Screen(route = "signup_screen")
    object Home : Screen(route = "home_screen")
}
