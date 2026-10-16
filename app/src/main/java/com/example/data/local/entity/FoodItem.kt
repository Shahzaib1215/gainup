package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val urduName: String = "",
    val category: String, // "Pakistani Desi", "Breakfast", "Protein Sources", "Grains & Roti", "Dairy & Shakes", "Street Food"
    val defaultServing: String, // e.g. "1 plate (300g)", "1 medium (60g)"
    val caloriesPerServing: Int,
    val proteinPerServing: Float,
    val carbsPerServing: Float,
    val fatsPerServing: Float,
    val isDesi: Boolean = true,
    val weightGainTip: String = ""
)
