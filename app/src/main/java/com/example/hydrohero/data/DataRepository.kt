package com.example.hydrohero.data

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar

class DataRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "hydro_hero_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_CURRENT_INTAKE = "current_intake"
        private const val KEY_GLASSES_COUNT = "glasses_count"
        private const val KEY_STREAK = "streak"
        private const val KEY_COINS = "coins"
        private const val KEY_SELECTED_AVATAR = "selected_avatar"
        private const val KEY_SELECTED_BACKGROUND = "selected_background"
        private const val KEY_SELECTED_EFFECT = "selected_effect"
        private const val KEY_LAST_DATE = "last_date"
        private const val KEY_GOAL_COMPLETED_TODAY = "goal_completed_today"
        private const val KEY_OWNED_ITEMS = "owned_items"
        private const val KEY_REMINDER_COMPLETIONS = "reminder_completions"
        private const val KEY_REMINDER_REWARD_CLAIMED = "reminder_reward_claimed"
        private const val KEY_IS_PREMIUM = "is_premium"
        private const val KEY_PREMIUM_TYPE = "premium_type"
    }

    fun getUserData(): UserData {
        // Reset daily intake and streak every time app opens (for prototype)
        val today = getTodayDateString()
        val lastDate = prefs.getString(KEY_LAST_DATE, null)
        
        // Reset reminder completions if it's a new day
        if (lastDate != today) {
            prefs.edit()
                .putInt(KEY_REMINDER_COMPLETIONS, 0)
                .putBoolean(KEY_REMINDER_REWARD_CLAIMED, false)
                .apply()
        }
        
        // Always reset intake, streak, coins, owned items, and premium status on app start (for testing)
        prefs.edit()
            .putString(KEY_LAST_DATE, today)
            .putInt(KEY_CURRENT_INTAKE, 0)
            .putInt(KEY_GLASSES_COUNT, 0)
            .putInt(KEY_STREAK, 0)
            .putBoolean(KEY_GOAL_COMPLETED_TODAY, false)
            .putInt(KEY_COINS, 800) // Reset to 800 coins
            .putStringSet(KEY_OWNED_ITEMS, setOf("water", "bear", "sea")) // Reset to default owned items
            .putBoolean(KEY_IS_PREMIUM, false) // Reset premium status
            .putString(KEY_PREMIUM_TYPE, "none") // Reset premium type
            .putString(KEY_SELECTED_EFFECT, "💧") // Reset effect to default
            .apply()
        
        return UserData(
            dailyGoal = prefs.getInt(KEY_DAILY_GOAL, 2000),
            currentIntake = 0, // Always start at 0
            glassesCount = 0, // Always start at 0
            streak = 0, // Always start at 0
            coins = 800, // Always reset to 800
            selectedAvatar = prefs.getString(KEY_SELECTED_AVATAR, "💧") ?: "💧",
            selectedBackground = prefs.getString(KEY_SELECTED_BACKGROUND, "sea") ?: "sea",
            selectedEffect = prefs.getString(KEY_SELECTED_EFFECT, "💧") ?: "💧",
            isPremium = prefs.getBoolean(KEY_IS_PREMIUM, false),
            premiumType = prefs.getString(KEY_PREMIUM_TYPE, "none") ?: "none"
        )
    }
    
    fun getReminderCompletions(): Int {
        return prefs.getInt(KEY_REMINDER_COMPLETIONS, 0)
    }
    
    fun saveReminderCompletions(count: Int) {
        prefs.edit().putInt(KEY_REMINDER_COMPLETIONS, count).apply()
    }
    
    fun getReminderRewardClaimed(): Boolean {
        return prefs.getBoolean(KEY_REMINDER_REWARD_CLAIMED, false)
    }
    
    fun saveReminderRewardClaimed(claimed: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDER_REWARD_CLAIMED, claimed).apply()
    }

    fun saveUserData(userData: UserData) {
        val today = getTodayDateString()
        val wasGoalReached = userData.currentIntake >= userData.dailyGoal
        
        prefs.edit()
            .putInt(KEY_DAILY_GOAL, userData.dailyGoal)
            .putInt(KEY_CURRENT_INTAKE, userData.currentIntake)
            .putInt(KEY_GLASSES_COUNT, userData.glassesCount)
            .putInt(KEY_STREAK, userData.streak)
            .putInt(KEY_COINS, userData.coins)
            .putString(KEY_SELECTED_AVATAR, userData.selectedAvatar)
            .putString(KEY_LAST_DATE, today)
            .putBoolean(KEY_GOAL_COMPLETED_TODAY, wasGoalReached)
            .apply()
    }

    fun updateDailyGoal(goal: Int) {
        val currentIntake = prefs.getInt(KEY_CURRENT_INTAKE, 0)
        // Recalculate goal completion based on current intake vs new goal
        val wasGoalReached = currentIntake >= goal
        
        val editor = prefs.edit()
            .putInt(KEY_DAILY_GOAL, goal)
            .putBoolean(KEY_GOAL_COMPLETED_TODAY, wasGoalReached)
        
        // If goal wasn't reached, make sure we clear any stale completion flags
        if (!wasGoalReached) {
            editor.putBoolean(KEY_GOAL_COMPLETED_TODAY, false)
        }
        
        editor.apply()
    }

    fun updateIntake(intake: Int, glassesCount: Int) {
        val today = getTodayDateString()
        val dailyGoal = prefs.getInt(KEY_DAILY_GOAL, 2000)
        val wasGoalReached = intake >= dailyGoal
        
        prefs.edit()
            .putInt(KEY_CURRENT_INTAKE, intake)
            .putInt(KEY_GLASSES_COUNT, glassesCount)
            .putString(KEY_LAST_DATE, today)
            .putBoolean(KEY_GOAL_COMPLETED_TODAY, wasGoalReached)
            .apply()
    }

    fun updateCoins(coins: Int) {
        prefs.edit().putInt(KEY_COINS, coins).apply()
    }

    fun updateSelectedAvatar(avatar: String) {
        prefs.edit().putString(KEY_SELECTED_AVATAR, avatar).apply()
    }
    
    fun updateSelectedBackground(backgroundId: String) {
        prefs.edit().putString(KEY_SELECTED_BACKGROUND, backgroundId).apply()
    }
    
    fun updateSelectedEffect(effectIcon: String) {
        prefs.edit().putString(KEY_SELECTED_EFFECT, effectIcon).apply()
    }
    
    fun updatePremiumStatus(isPremium: Boolean, premiumType: String) {
        prefs.edit()
            .putBoolean(KEY_IS_PREMIUM, isPremium)
            .putString(KEY_PREMIUM_TYPE, premiumType)
            .apply()
    }

    fun updateStreak(streak: Int) {
        prefs.edit().putInt(KEY_STREAK, streak).apply()
    }

    fun getOwnedItemIds(): Set<String> {
        return prefs.getStringSet(KEY_OWNED_ITEMS, setOf("water", "bear", "sea")) ?: setOf("water", "bear", "sea")
    }

    fun saveOwnedItemIds(itemIds: List<String>) {
        prefs.edit().putStringSet(KEY_OWNED_ITEMS, itemIds.toSet()).apply()
    }

    private fun getTodayDateString(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year-$month-$day"
    }
}
