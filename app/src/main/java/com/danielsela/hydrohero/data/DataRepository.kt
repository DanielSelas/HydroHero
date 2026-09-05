package com.danielsela.hydrohero.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_QUIET_HOURS_ENABLED = "quiet_hours_enabled"
        private const val KEY_PRESET_REMINDERS = "preset_reminders"
        private const val KEY_CUSTOM_REMINDERS = "custom_reminders"
        private const val KEY_DAILY_HISTORY = "daily_history"
        private const val KEY_ONBOARDING_SEEN = "onboarding_seen"

        private const val DEFAULT_COINS = 800

        /** Roughly a year; one row per day stays well under a few tens of KB. */
        private const val MAX_HISTORY_DAYS = 365
    }

    fun getUserData(): UserData {
        val today = getTodayDateString()
        val lastDate = prefs.getString(KEY_LAST_DATE, null)

        // Roll over to a new calendar day: reset ONLY the daily-scoped state.
        // Streak, coins, owned items, premium and cosmetics must survive across
        // days and app restarts.
        if (lastDate != today) {
            // Archive the day that just ended before its counters are wiped.
            if (lastDate != null) {
                archiveDay(
                    date = lastDate,
                    totalMl = prefs.getInt(KEY_CURRENT_INTAKE, 0),
                    glasses = prefs.getInt(KEY_GLASSES_COUNT, 0),
                    goalMl = prefs.getInt(KEY_DAILY_GOAL, 2000)
                )
            }

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

    // ── Onboarding ──────────────────────────────────────────────────────
    // Not cleared by resetPrototypeState(): "Reset progress" wipes progress,
    // it should not replay the intro at someone who has already seen it.

    fun hasSeenOnboarding(): Boolean = prefs.getBoolean(KEY_ONBOARDING_SEEN, false)

    fun setOnboardingSeen() {
        prefs.edit().putBoolean(KEY_ONBOARDING_SEEN, true).apply()
    }

    // ── Daily history ───────────────────────────────────────────────────

    /** Archived days, oldest first. Today is NOT included — it is still live. */
    fun getDailyHistory(): List<DailyRecord> {
        val raw = prefs.getString(KEY_DAILY_HISTORY, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).map { index ->
                val item = array.getJSONObject(index)
                DailyRecord(
                    date = item.getString("date"),
                    totalMl = item.getInt("totalMl"),
                    glasses = item.getInt("glasses"),
                    goalMl = item.getInt("goalMl")
                )
            }
        } catch (e: JSONException) {
            emptyList()
        }
    }

    /**
     * Writes one finished day. A day with no water logged is still recorded, so
     * a gap in the chart means "app not opened", not "drank nothing".
     */
    private fun archiveDay(date: String, totalMl: Int, glasses: Int, goalMl: Int) {
        val existing = getDailyHistory().filterNot { it.date == date }
        val updated = (existing + DailyRecord(date, totalMl, glasses, goalMl))
            .sortedBy { it.date }
            .takeLast(MAX_HISTORY_DAYS)

        val array = JSONArray()
        updated.forEach { record ->
            array.put(
                JSONObject()
                    .put("date", record.date)
                    .put("totalMl", record.totalMl)
                    .put("glasses", record.glasses)
                    .put("goalMl", record.goalMl)
            )
        }
        prefs.edit().putString(KEY_DAILY_HISTORY, array.toString()).apply()
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
            .remove(KEY_PRESET_REMINDERS)
            .remove(KEY_CUSTOM_REMINDERS)
            .remove(KEY_DAILY_HISTORY)
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, false)
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

    // ── Notification settings ───────────────────────────────────────────
    // These drive whether alarms are scheduled at all, so they have to
    // survive a restart: otherwise reminders silently stop after the app
    // is closed once.

    fun getNotificationsEnabled(): Boolean = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)

    fun saveNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun getQuietHoursEnabled(): Boolean = prefs.getBoolean(KEY_QUIET_HOURS_ENABLED, false)

    fun saveQuietHoursEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_QUIET_HOURS_ENABLED, enabled).apply()
    }

    // ── Reminders ───────────────────────────────────────────────────────
    // Stored as JSON because the title/description are free text; a
    // delimited format would break on the first comma a user types.
    // Returns null when nothing was ever saved, so the caller can fall
    // back to its defaults instead of showing an empty list.

    fun getPresetReminders(): List<Reminder>? =
        prefs.getString(KEY_PRESET_REMINDERS, null)?.let(::remindersFromJson)

    fun savePresetReminders(reminders: List<Reminder>) {
        prefs.edit().putString(KEY_PRESET_REMINDERS, remindersToJson(reminders)).apply()
    }

    fun getCustomReminders(): List<Reminder>? =
        prefs.getString(KEY_CUSTOM_REMINDERS, null)?.let(::remindersFromJson)

    fun saveCustomReminders(reminders: List<Reminder>) {
        prefs.edit().putString(KEY_CUSTOM_REMINDERS, remindersToJson(reminders)).apply()
    }

    private fun remindersToJson(reminders: List<Reminder>): String {
        val array = JSONArray()
        reminders.forEach { reminder ->
            array.put(
                JSONObject()
                    .put("id", reminder.id)
                    .put("title", reminder.title)
                    .put("description", reminder.description)
                    .put("time", reminder.time)
                    .put("isEnabled", reminder.isEnabled)
                    .put("isPreset", reminder.isPreset)
            )
        }
        return array.toString()
    }

    /** Returns null on anything malformed so the caller falls back to defaults. */
    private fun remindersFromJson(raw: String): List<Reminder>? = try {
        val array = JSONArray(raw)
        (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            Reminder(
                id = item.getString("id"),
                title = item.getString("title"),
                description = item.getString("description"),
                time = item.getString("time"),
                isEnabled = item.getBoolean("isEnabled"),
                isPreset = item.getBoolean("isPreset")
            )
        }
    } catch (e: JSONException) {
        null
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
