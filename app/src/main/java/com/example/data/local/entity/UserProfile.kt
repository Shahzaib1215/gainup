package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "User",
    val currentWeightKg: Float = 60.0f,
    val targetWeightKg: Float = 70.0f,
    val heightCm: Float = 175.0f,
    val age: Int = 24,
    val gender: String = "Male", // "Male" or "Female"
    val activityLevel: String = "Moderate", // "Sedentary", "Light", "Moderate", "Very Active"
    val calorieSurplus: Int = 500, // +300 to +700 kcal for healthy weight gain
    val dailyCalorieTarget: Int = 2800,
    val dailyProteinTargetGrams: Int = 130, // 1.8-2.0g per kg bodyweight
    val dailyCarbsTargetGrams: Int = 360,
    val dailyFatTargetGrams: Int = 85,
    val dailyWaterGoalMl: Int = 3000,
    val isOnboarded: Boolean = false
) {
    companion object {
        fun calculateNutritionGoals(
            currentWeightKg: Float,
            targetWeightKg: Float,
            heightCm: Float,
            age: Int,
            gender: String,
            activityLevel: String,
            surplus: Int = 500,
            waterGoalMl: Int = 3000
        ): UserProfile {
            // Mifflin-St Jeor Equation for BMR
            val bmr = if (gender.equals("Female", ignoreCase = true)) {
                (10 * currentWeightKg) + (6.25f * heightCm) - (5 * age) - 161
            } else {
                (10 * currentWeightKg) + (6.25f * heightCm) - (5 * age) + 5
            }

            val activityMultiplier = when (activityLevel.lowercase()) {
                "sedentary" -> 1.2f
                "light" -> 1.375f
                "very active" -> 1.725f
                else -> 1.55f // Moderate (default)
            }

            val maintenanceCalories = (bmr * activityMultiplier).toInt()
            val targetCalories = maintenanceCalories + surplus

            // Protein for muscle & weight gain: ~1.8g per kg bodyweight
            val proteinGrams = (currentWeightKg * 1.8f).toInt().coerceAtLeast(100)
            val proteinCalories = proteinGrams * 4

            // Fat: ~25-30% of total calories
            val fatCalories = (targetCalories * 0.28f).toInt()
            val fatGrams = (fatCalories / 9).coerceAtLeast(60)

            // Remaining calories to carbs
            val remainingCalories = (targetCalories - proteinCalories - fatCalories).coerceAtLeast(400)
            val carbsGrams = remainingCalories / 4

            return UserProfile(
                id = 1,
                name = "User",
                currentWeightKg = currentWeightKg,
                targetWeightKg = targetWeightKg,
                heightCm = heightCm,
                age = age,
                gender = gender,
                activityLevel = activityLevel,
                calorieSurplus = surplus,
                dailyCalorieTarget = targetCalories,
                dailyProteinTargetGrams = proteinGrams,
                dailyCarbsTargetGrams = carbsGrams,
                dailyFatTargetGrams = fatGrams,
                dailyWaterGoalMl = waterGoalMl,
                isOnboarded = true
            )
        }
    }
}
