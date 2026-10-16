package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_settings")
data class ReminderSetting(
    @PrimaryKey val id: String, // "breakfast", "lunch", "dinner", "water_day", "bedtime"
    val title: String,
    val time: String, // HH:mm format e.g. "08:30"
    val isEnabled: Boolean,
    val message: String
)
