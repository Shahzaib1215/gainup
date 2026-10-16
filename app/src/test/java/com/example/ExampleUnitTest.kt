package com.example

import com.example.data.local.entity.FoodLogEntry
import com.example.data.local.entity.SleepLogEntry
import com.example.data.local.entity.UserProfile
import com.example.data.local.entity.WaterLogEntry
import com.example.data.local.entity.WeightLogEntry
import com.example.util.CsvExportHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testGenerateProgressCsvContainsRequiredSections() {
        val profile = UserProfile(
            name = "Hamza Khan",
            currentWeightKg = 62.5f,
            targetWeightKg = 72.0f,
            dailyCalorieTarget = 2900,
            calorieSurplus = 500,
            dailyProteinTargetGrams = 135
        )

        val weightLogs = listOf(
            WeightLogEntry(id = 1, weightKg = 60.0f, dateString = "2026-10-01", note = "Starting baseline"),
            WeightLogEntry(id = 2, weightKg = 61.2f, dateString = "2026-10-08", note = "Week 1 check-in"),
            WeightLogEntry(id = 3, weightKg = 62.5f, dateString = "2026-10-15", note = "Steady gain")
        )

        val foodLogs = listOf(
            FoodLogEntry(
                id = 1,
                foodName = "Chicken Biryani",
                mealType = "Lunch",
                servingDescription = "1 plate (350g)",
                quantity = 1f,
                calories = 580,
                protein = 28f,
                carbs = 76f,
                fats = 18f,
                dateString = "2026-10-15"
            ),
            FoodLogEntry(
                id = 2,
                foodName = "Banana Almond Shake, with Honey",
                mealType = "Snack",
                servingDescription = "1 large mug",
                quantity = 1f,
                calories = 450,
                protein = 14f,
                carbs = 68f,
                fats = 16f,
                dateString = "2026-10-15"
            )
        )

        val waterLogs = listOf(
            WaterLogEntry(id = 1, amountMl = 1000, dateString = "2026-10-15"),
            WaterLogEntry(id = 2, amountMl = 500, dateString = "2026-10-15")
        )

        val sleepLogs = listOf(
            SleepLogEntry(id = 1, hours = 8.0f, dateString = "2026-10-15", quality = "Great")
        )

        val csvOutput = CsvExportHelper.generateProgressCsv(
            userProfile = profile,
            weightLogs = weightLogs,
            foodLogs = foodLogs,
            waterLogs = waterLogs,
            sleepLogs = sleepLogs
        )

        // Verify Header Information
        assertTrue(csvOutput.contains("# GainUp Healthy Weight Gain & Nutrition Progress Export"))
        assertTrue(csvOutput.contains("Hamza Khan"))
        assertTrue(csvOutput.contains("72.0"))

        // Verify Weight Section
        assertTrue(csvOutput.contains("=== SECTION 1: WEIGHT CHECK-INS ==="))
        assertTrue(csvOutput.contains("2026-10-01,60.0"))
        assertTrue(csvOutput.contains("Week 1 check-in"))

        // Verify Food & Calorie Section
        assertTrue(csvOutput.contains("=== SECTION 3: DETAILED MEAL LOGS ==="))
        assertTrue(csvOutput.contains("Chicken Biryani"))
        assertTrue(csvOutput.contains("580"))

        // Verify CSV escaping of commas
        assertTrue(csvOutput.contains("\"Banana Almond Shake, with Honey\""))

        // Verify Daily Summary Section
        assertTrue(csvOutput.contains("=== SECTION 2: DAILY SUMMARY & SURPLUS PERFORMANCE ==="))
        assertTrue(csvOutput.contains("2026-10-15"))
    }
}
