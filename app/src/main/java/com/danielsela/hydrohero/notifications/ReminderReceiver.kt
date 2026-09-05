package com.danielsela.hydrohero.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.danielsela.hydrohero.R
import com.danielsela.hydrohero.data.DataRepository
import java.time.LocalTime

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationChannels.ensureCreated(context)

        val reminderId = intent.getStringExtra(EXTRA_REMINDER_ID) ?: return
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Hydro Hero"
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: "Time to drink water 💧"
        val hour = intent.getIntExtra(EXTRA_HOUR, -1)
        val minute = intent.getIntExtra(EXTRA_MINUTE, -1)

        // The receiver runs without the ViewModel, so preferences are read
        // straight from storage.
        val repository = DataRepository(context)

        // Always reschedule before any early return, otherwise a single
        // suppressed reminder would silently end the daily chain.
        if (hour in 0..23 && minute in 0..59) {
            ReminderScheduler(context).rescheduleNext(
                reminderId = reminderId,
                title = title,
                description = description,
                hour = hour,
                minute = minute
            )
        }

        // Safety net: a stale alarm can outlive the toggle that scheduled it.
        if (!repository.getNotificationsEnabled()) return

        if (repository.getQuietHoursEnabled() && isWithinQuietHours()) return

        val notification = NotificationCompat.Builder(context, NotificationChannels.REMINDERS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(reminderId.hashCode(), notification)
    }

    /** Quiet hours wrap midnight, so this is an OR rather than a range check. */
    private fun isWithinQuietHours(): Boolean {
        val hour = LocalTime.now().hour
        return hour >= QUIET_HOURS_START || hour < QUIET_HOURS_END
    }

    companion object {
        /** 10pm - 7am, matching the subtitle shown in Settings. */
        const val QUIET_HOURS_START = 22
        const val QUIET_HOURS_END = 7

        private const val EXTRA_REMINDER_ID = "extra_reminder_id"
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_DESCRIPTION = "extra_description"
        private const val EXTRA_HOUR = "extra_hour"
        private const val EXTRA_MINUTE = "extra_minute"

        fun buildIntent(
            context: Context,
            reminderId: String,
            title: String,
            description: String,
            hour: Int,
            minute: Int
        ): Intent {
            return Intent(context, ReminderReceiver::class.java).apply {
                putExtra(EXTRA_REMINDER_ID, reminderId)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_DESCRIPTION, description)
                putExtra(EXTRA_HOUR, hour)
                putExtra(EXTRA_MINUTE, minute)
            }
        }
    }
}

