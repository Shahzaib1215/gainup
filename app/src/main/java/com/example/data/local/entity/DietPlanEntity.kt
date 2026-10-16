package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diet_plans")
data class DietPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val targetCalories: Int,
    val targetProtein: Int,
    val isAiGenerated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val planDetailsJson: String // Serialized meal plan structure
)
