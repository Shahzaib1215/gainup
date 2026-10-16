package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra("reminder_id") ?: "gainup"
        val title = intent.getStringExtra("title") ?: "GainUp Reminder"
        val message = intent.getStringExtra("message") ?: "Time to fuel up for healthy weight gain!"

        GainUpNotificationHelper.showNotification(
            context = context,
            id = reminderId.hashCode(),
            title = title,
            message = message
        )
    }
}
