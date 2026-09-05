package com.danielsela.hydrohero.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DailyProgress : Screen("daily_progress")
    object Reminders : Screen("reminders")
    object Shop : Screen("shop")
    object Settings : Screen("settings")
}
