package com.danielsela.hydrohero.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val REMINDERS_CHANNEL_ID = "hydrohero_reminders"

    fun ensureCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            REMINDERS_CHANNEL_ID,
            "Hydration Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Hydro Hero reminder notifications"
        }
        manager.createNotificationChannel(channel)
    }
}

