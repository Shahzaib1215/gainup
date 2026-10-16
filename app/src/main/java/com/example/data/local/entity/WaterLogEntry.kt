package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int, // e.g. 250, 500, 1000
    val loggedAtTimestamp: Long = System.currentTimeMillis(),
    val dateString: String // YYYY-MM-DD
)
