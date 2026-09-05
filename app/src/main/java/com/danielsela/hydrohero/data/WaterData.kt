package com.danielsela.hydrohero.data

data class WaterEntry(
    val amount: Int, // in ml
    val timestamp: Long = System.currentTimeMillis()
)

data class UserData(
    var dailyGoal: Int = 2000, // in ml
    var currentIntake: Int = 0, // in ml
    var glassesCount: Int = 0,
    var streak: Int = 0,
    var coins: Int = 800,
    var selectedAvatar: String = "💧", // Default avatar emoji
    var selectedBackground: String = "none", // Default background ID
    var selectedEffect: String = "💧", // Default effect icon
    var isPremium: Boolean = false, // Premium subscription status
    var premiumType: String = "none" // "none", "monthly", "lifetime"
)
