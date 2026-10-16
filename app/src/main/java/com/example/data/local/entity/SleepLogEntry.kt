package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_logs")
data class SleepLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hours: Float,
    val bedtime: String = "",
    val wakeTime: String = "",
    val quality: String = "Good", // "Fair", "Good", "Great"
    val loggedAtTimestamp: Long = System.currentTimeMillis(),
    val dateString: String // YYYY-MM-DD
)
