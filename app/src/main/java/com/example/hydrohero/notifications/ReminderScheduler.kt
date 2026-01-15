package com.example.hydrohero.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.hydrohero.data.Reminder
import java.util.Calendar

class ReminderScheduler(private val context: Context) {

    fun scheduleDaily(reminder: Reminder): Boolean {
        val (hour, minute) = parseTime(reminder.time) ?: return false
        val triggerAt = nextTriggerTimeMillis(hour, minute)
        val intent = ReminderReceiver.buildIntent(
            context = context,
            reminderId = reminder.id,
            title = reminder.title,
            description = reminder.description,
            hour = hour,
            minute = minute
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCodeFor(reminder.id),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or pendingIntentImmutableFlag()
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return setDailyAlarmSafely(alarmManager, triggerAt, pendingIntent)
    }

    fun cancel(reminderId: String) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCodeFor(reminderId),
            intent,
            PendingIntent.FLAG_NO_CREATE or pendingIntentImmutableFlag()
        )
        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun rescheduleNext(reminderId: String, title: String, description: String, hour: Int, minute: Int) {
        val triggerAt = nextTriggerTimeMillis(hour, minute)
        val intent = ReminderReceiver.buildIntent(
            context = context,
            reminderId = reminderId,
            title = title,
            description = description,
            hour = hour,
            minute = minute
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCodeFor(reminderId),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or pendingIntentImmutableFlag()
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        setDailyAlarmSafely(alarmManager, triggerAt, pendingIntent)
    }

    private fun setDailyAlarmSafely(
        alarmManager: AlarmManager,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent
    ): Boolean {
        // Android 12+ requires the user to grant special access for exact alarms, otherwise
        // setExact* will throw SecurityException. For a prototype/reminders, inexact is fine.
        return try {
            val canUseExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else true

            if (canUseExact) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    @Suppress("DEPRECATION")
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else {
                // Fall back to inexact window (15 minutes)
                val window = 15 * 60 * 1000L
                alarmManager.setWindow(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    window,
                    pendingIntent
                )
            }
            true
        } catch (_: SecurityException) {
            // Final safety net: don't crash the app if the OS rejects exact alarms
            false
        }
    }

    private fun nextTriggerTimeMillis(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        if (cal.timeInMillis <= System.currentTimeMillis() + 1000) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    private fun requestCodeFor(reminderId: String): Int = reminderId.hashCode()

    private fun pendingIntentImmutableFlag(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0

    private fun parseTime(time: String): Pair<Int, Int>? {
        // Accept formats like "8:00 AM", "08:00am", "12:30 PM"
        val regex = Regex("""^\s*(\d{1,2})\s*:\s*(\d{2})\s*([AaPp][Mm])\s*$""")
        val match = regex.find(time) ?: return null
        val hRaw = match.groupValues[1].toIntOrNull() ?: return null
        val m = match.groupValues[2].toIntOrNull() ?: return null
        if (m !in 0..59) return null
        val ampm = match.groupValues[3].lowercase()
        if (hRaw !in 1..12) return null
        val h = when (ampm) {
            "am" -> if (hRaw == 12) 0 else hRaw
            "pm" -> if (hRaw == 12) 12 else hRaw + 12
            else -> return null
        }
        return h to m
    }
}

