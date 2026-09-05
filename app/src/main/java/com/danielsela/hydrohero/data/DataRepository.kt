package com.danielsela.hydrohero.data

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate
import java.time.format.DateTimeParseException

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
        private const val KEY_COMPLETED_REMINDER_IDS = "completed_reminder_ids"
        private const val KEY_WATER_ENTRIES = "water_entries"
        private const val KEY_THEME_HUE = "theme_hue"
        private const val KEY_DARK_MODE = "dark_mode"

        private const val DEFAULT_COINS = 800
    }

    fun getUserData(): UserData {
        val today = getTodayDateString()
        val lastDate = prefs.getString(KEY_LAST_DATE, null)

        // Roll over to a new calendar day: reset ONLY the daily-scoped state.
        // Streak, coins, owned items, premium and cosmetics must survive across
        // days and app restarts.
        if (lastDate != today) {
            prefs.edit()
                .putString(KEY_LAST_DATE, today)
                .putInt(KEY_CURRENT_INTAKE, 0)
                .putInt(KEY_GLASSES_COUNT, 0)
                .putBoolean(KEY_GOAL_COMPLETED_TODAY, false)
                .putInt(KEY_REMINDER_COMPLETIONS, 0)
                .putBoolean(KEY_REMINDER_REWARD_CLAIMED, false)
                .putStringSet(KEY_COMPLETED_REMINDER_IDS, emptySet())
                .remove(KEY_WATER_ENTRIES)
                .putInt(KEY_STREAK, rolledOverStreak(lastDate, today))
                .apply()
        }

        return UserData(
            dailyGoal = prefs.getInt(KEY_DAILY_GOAL, 2000),
            currentIntake = prefs.getInt(KEY_CURRENT_INTAKE, 0),
            glassesCount = prefs.getInt(KEY_GLASSES_COUNT, 0),
            streak = prefs.getInt(KEY_STREAK, 0),
            coins = prefs.getInt(KEY_COINS, DEFAULT_COINS),
            selectedAvatar = prefs.getString(KEY_SELECTED_AVATAR, "💧") ?: "💧",
            selectedBackground = prefs.getString(KEY_SELECTED_BACKGROUND, "none") ?: "none",
            selectedEffect = prefs.getString(KEY_SELECTED_EFFECT, "💧") ?: "💧",
            isPremium = prefs.getBoolean(KEY_IS_PREMIUM, false),
            premiumType = prefs.getString(KEY_PREMIUM_TYPE, "none") ?: "none"
        )
    }

    /**
     * Streak value to keep when the calendar date changes.
     *
     * The streak is incremented on the day the goal is reached (see
     * [WaterViewModel.addWater]), so an unbroken run only has to survive the
     * rollover: it does when the previous tracked day was completed and it was
     * literally yesterday. A missed day, or a gap of more than one day, breaks it.
     */
    private fun rolledOverStreak(lastDate: String?, today: String): Int {
        // First ever launch: nothing to carry over, nothing to break.
        if (lastDate == null) return prefs.getInt(KEY_STREAK, 0)

        if (!prefs.getBoolean(KEY_GOAL_COMPLETED_TODAY, false)) return 0

        val lastDay = parseDate(lastDate) ?: return 0
        val todayDay = parseDate(today) ?: return 0
        return if (todayDay.toEpochDay() - lastDay.toEpochDay() == 1L) {
            prefs.getInt(KEY_STREAK, 0)
        } else {
            0
        }
    }

    /**
     * Prototype-only: reset all user progress to a clean state immediately.
     * Keeps the currently saved daily goal (KEY_DAILY_GOAL) so demos can adjust it.
     */
    fun resetPrototypeState() {
        val today = getTodayDateString()
        prefs.edit()
            .putString(KEY_LAST_DATE, today)
            .putInt(KEY_CURRENT_INTAKE, 0)
            .putInt(KEY_GLASSES_COUNT, 0)
            .putInt(KEY_STREAK, 0)
            .putBoolean(KEY_GOAL_COMPLETED_TODAY, false)
            .putInt(KEY_REMINDER_COMPLETIONS, 0)
            .putBoolean(KEY_REMINDER_REWARD_CLAIMED, false)
            .putInt(KEY_COINS, DEFAULT_COINS)
            .putStringSet(KEY_OWNED_ITEMS, setOf("water", "none"))
            .putBoolean(KEY_IS_PREMIUM, false)
            .putString(KEY_PREMIUM_TYPE, "none")
            .putString(KEY_SELECTED_EFFECT, "💧")
            .putString(KEY_SELECTED_AVATAR, "💧")
            .putString(KEY_SELECTED_BACKGROUND, "none")
            .putStringSet(KEY_COMPLETED_REMINDER_IDS, emptySet())
            .remove(KEY_WATER_ENTRIES)
            .apply()
    }

    /**
     * Today's individual drink log, backing the Daily Progress screen and the
     * Settings > Export data action. Cleared by the same daily rollover that
     * clears [KEY_CURRENT_INTAKE], so it always matches the intake shown on Home.
     *
     * Stored as one "amountMl:epochMillis" record per line — the log is a
     * handful of rows a day, so a full serializer would be overkill.
     */
    fun getWaterEntries(): List<WaterEntry> {
        val raw = prefs.getString(KEY_WATER_ENTRIES, null) ?: return emptyList()
        return raw.lineSequence()
            .mapNotNull { line ->
                val parts = line.split(':')
                val amount = parts.getOrNull(0)?.toIntOrNull()
                val timestamp = parts.getOrNull(1)?.toLongOrNull()
                if (amount != null && timestamp != null) WaterEntry(amount, timestamp) else null
            }
            .toList()
    }

    fun saveWaterEntries(entries: List<WaterEntry>) {
        val raw = entries.joinToString("\n") { "${it.amount}:${it.timestamp}" }
        prefs.edit().putString(KEY_WATER_ENTRIES, raw).apply()
    }

    // ── Appearance ──────────────────────────────────────────────────────
    // Display preferences, not progress: deliberately untouched by
    // resetPrototypeState(), same as the saved daily goal.

    fun getThemeHue(): String = prefs.getString(KEY_THEME_HUE, "teal") ?: "teal"

    fun saveThemeHue(id: String) {
        prefs.edit().putString(KEY_THEME_HUE, id).apply()
    }

    fun getDarkMode(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)

    fun saveDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun getCompletedReminderIds(): Set<String> {
        return prefs.getStringSet(KEY_COMPLETED_REMINDER_IDS, emptySet()) ?: emptySet()
    }

    fun saveCompletedReminderIds(ids: Set<String>) {
        prefs.edit().putStringSet(KEY_COMPLETED_REMINDER_IDS, ids).apply()
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
        return prefs.getStringSet(KEY_OWNED_ITEMS, setOf("water", "none")) ?: setOf("water", "none")
    }

    fun saveOwnedItemIds(itemIds: List<String>) {
        prefs.edit().putStringSet(KEY_OWNED_ITEMS, itemIds.toSet()).apply()
    }

    private fun getTodayDateString(): String = LocalDate.now().toString()

    /** Returns null for values written by older builds, which used a different format. */
    private fun parseDate(value: String): LocalDate? = try {
        LocalDate.parse(value)
    } catch (e: DateTimeParseException) {
        null
    }
}
