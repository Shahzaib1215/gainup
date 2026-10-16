package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_logs")
data class FoodLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodName: String,
    val mealType: String, // "Breakfast", "Lunch", "Dinner", "Snack"
    val servingDescription: String,
    val quantity: Float = 1.0f,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val loggedAtTimestamp: Long = System.currentTimeMillis(),
    val dateString: String // YYYY-MM-DD
)
