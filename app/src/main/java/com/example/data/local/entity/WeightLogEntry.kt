package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_logs")
data class WeightLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Float,
    val note: String = "",
    val loggedAtTimestamp: Long = System.currentTimeMillis(),
    val dateString: String // YYYY-MM-DD
)
