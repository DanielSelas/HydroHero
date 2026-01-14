package com.example.hydrohero.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Reminders : Screen("reminders")
    object Shop : Screen("shop")
    object Settings : Screen("settings")
}
