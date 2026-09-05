package com.danielsela.hydrohero.data

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * Locks in the persistence rules that were the app's worst bug: every launch
 * used to wipe streak, coins, owned items and premium.
 *
 * These run instrumented rather than as plain JVM tests because DataRepository
 * talks to real SharedPreferences and org.json, neither of which exists on the
 * unit-test classpath.
 *
 * Time is not mockable here, so "yesterday" is simulated by writing last_date
 * directly before constructing the repository — the same thing a real overnight
 * rollover does.
 */
@RunWith(AndroidJUnit4::class)
class DataRepositoryTest {

    private lateinit var context: Context

    private fun prefs() = context.getSharedPreferences("hydro_hero_prefs", Context.MODE_PRIVATE)

    /** Pretends the last session happened [daysAgo] days ago. */
    private fun setLastDate(daysAgo: Long) {
        prefs().edit()
            .putString("last_date", LocalDate.now().minusDays(daysAgo).toString())
            .commit()
    }

    @Before
    fun clearState() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        prefs().edit().clear().commit()
    }

    // ── The P0 regression ───────────────────────────────────────────────

    @Test
    fun reopeningOnTheSameDayKeepsEverything() {
        val repository = DataRepository(context)
        repository.getUserData()                       // establishes today
        repository.updateIntake(intake = 1200, glassesCount = 4)
        repository.updateCoins(950)
        repository.updateStreak(7)
        repository.updatePremiumStatus(isPremium = true, premiumType = "lifetime")
        repository.saveOwnedItemIds(listOf("water", "none", "moss"))

        // Simulates a cold start: a brand new repository over the same storage.
        val reopened = DataRepository(context).getUserData()

        assertEquals("intake must survive a restart", 1200, reopened.currentIntake)
        assertEquals(4, reopened.glassesCount)
        assertEquals(950, reopened.coins)
        assertEquals(7, reopened.streak)
        assertTrue(reopened.isPremium)
        assertEquals("lifetime", reopened.premiumType)
        assertTrue(DataRepository(context).getOwnedItemIds().contains("moss"))
    }

    @Test
    fun newDayClearsDailyStateButKeepsProgress() {
        val repository = DataRepository(context)
        repository.getUserData()
        repository.updateIntake(intake = 1800, glassesCount = 7)
        repository.updateCoins(640)
        repository.updateStreak(3)
        repository.updatePremiumStatus(isPremium = true, premiumType = "monthly")
        setLastDate(daysAgo = 1)

        val today = DataRepository(context).getUserData()

        assertEquals("a new day starts empty", 0, today.currentIntake)
        assertEquals(0, today.glassesCount)
        assertEquals("coins are not daily", 640, today.coins)
        assertTrue("premium is not daily", today.isPremium)
        assertEquals("monthly", today.premiumType)
    }

    // ── Streak roll-over ────────────────────────────────────────────────

    @Test
    fun streakSurvivesWhenYesterdayGoalWasMet() {
        val repository = DataRepository(context)
        repository.getUserData()
        repository.updateStreak(5)
        repository.updateIntake(intake = 2000, glassesCount = 8)   // sets goal_completed_today
        setLastDate(daysAgo = 1)

        assertEquals(5, DataRepository(context).getUserData().streak)
    }

    @Test
    fun streakResetsWhenYesterdayGoalWasMissed() {
        val repository = DataRepository(context)
        repository.getUserData()
        repository.updateStreak(5)
        repository.updateIntake(intake = 500, glassesCount = 2)    // short of the 2000 goal
        setLastDate(daysAgo = 1)

        assertEquals(0, DataRepository(context).getUserData().streak)
    }

    @Test
    fun streakResetsAfterAGapEvenIfTheLastTrackedDayWasComplete() {
        val repository = DataRepository(context)
        repository.getUserData()
        repository.updateStreak(9)
        repository.updateIntake(intake = 2500, glassesCount = 10)
        setLastDate(daysAgo = 3)                                   // two days never opened

        assertEquals(
            "a gap means days were missed, so the run is broken",
            0,
            DataRepository(context).getUserData().streak
        )
    }

    // ── Daily history ───────────────────────────────────────────────────

    @Test
    fun rolloverArchivesTheFinishedDay() {
        val repository = DataRepository(context)
        repository.getUserData()
        repository.updateIntake(intake = 1750, glassesCount = 6)
        val yesterday = LocalDate.now().minusDays(1).toString()
        setLastDate(daysAgo = 1)

        DataRepository(context).getUserData()
        val archived = DataRepository(context).getDailyHistory()

        assertEquals(1, archived.size)
        assertEquals(yesterday, archived[0].date)
        assertEquals(1750, archived[0].totalMl)
        assertEquals("the goal in force that day is stored", 2000, archived[0].goalMl)
        assertFalse(archived[0].goalMet)
    }

    @Test
    fun historyIsEmptyOnFirstEverLaunch() {
        DataRepository(context).getUserData()
        assertTrue(DataRepository(context).getDailyHistory().isEmpty())
    }

    // ── Serialization ───────────────────────────────────────────────────

    @Test
    fun remindersRoundTripWithPunctuationInFreeText() {
        // A delimited format would corrupt on the comma and the quotes; this is
        // why reminders are stored as JSON.
        val awkward = Reminder(
            id = "custom_1",
            title = """Drink up, "hero"!""",
            description = "At 8:00 AM, daily; skip weekends.",
            time = "8:00 AM",
            isEnabled = false,
            isPreset = false,
        )
        DataRepository(context).saveCustomReminders(listOf(awkward))

        val loaded = DataRepository(context).getCustomReminders()

        assertEquals(listOf(awkward), loaded)
    }

    @Test
    fun waterEntriesKeepIdenticalDrinks() {
        // Two identical drinks logged in the same millisecond must both survive;
        // a Set-based store would silently collapse them into one.
        val sameMoment = 1_757_000_000_000L
        val entries = listOf(
            WaterEntry(amount = 250, timestamp = sameMoment),
            WaterEntry(amount = 250, timestamp = sameMoment),
        )
        DataRepository(context).saveWaterEntries(entries)

        assertEquals(2, DataRepository(context).getWaterEntries().size)
    }

    @Test
    fun malformedStoredDataFallsBackInsteadOfCrashing() {
        prefs().edit()
            .putString("custom_reminders", "not json at all")
            .putString("daily_history", "{ broken")
            .commit()

        val repository = DataRepository(context)

        assertEquals(null, repository.getCustomReminders())
        assertTrue(repository.getDailyHistory().isEmpty())
    }

    // ── Reset ───────────────────────────────────────────────────────────

    @Test
    fun resetWipesProgressButNotTheIntro() {
        val repository = DataRepository(context)
        repository.getUserData()
        repository.setOnboardingSeen()
        repository.updateCoins(2000)
        repository.updateStreak(12)
        repository.saveThemeHue("coral")

        repository.resetPrototypeState()
        val afterReset = DataRepository(context).getUserData()

        assertEquals(800, afterReset.coins)
        assertEquals(0, afterReset.streak)
        assertFalse(afterReset.isPremium)
        assertTrue("resetting progress must not replay onboarding", repository.hasSeenOnboarding())
        assertEquals("appearance is a preference, not progress", "coral", repository.getThemeHue())
    }
}
