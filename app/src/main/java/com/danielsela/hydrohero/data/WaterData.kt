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

/**
 * A finished day, archived when the calendar date rolls over.
 *
 * Deliberately local-only: one row per day is tiny, it must work offline, and
 * keeping it out of the cloud avoids needing sign-in (and avoids widening what
 * the Play Data Safety form has to declare). If multi-device sync is added
 * later, this is the model a sync layer would carry.
 */
data class DailyRecord(
    val date: String,      // ISO yyyy-MM-dd
    val totalMl: Int,
    val glasses: Int,
    val goalMl: Int,
) {
    val goalMet: Boolean get() = goalMl > 0 && totalMl >= goalMl
}
