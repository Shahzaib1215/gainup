package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.entity.FoodLogEntry
import com.example.data.local.entity.SleepLogEntry
import com.example.data.local.entity.UserProfile
import com.example.data.local.entity.WaterLogEntry
import com.example.data.local.entity.WeightLogEntry
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExportHelper {

    fun generateProgressCsv(
        userProfile: UserProfile?,
        weightLogs: List<WeightLogEntry>,
        foodLogs: List<FoodLogEntry>,
        waterLogs: List<WaterLogEntry>,
        sleepLogs: List<SleepLogEntry>
    ): String {
        val sb = StringBuilder()
        val exportTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // 1. Header & User Summary
        sb.append("# GainUp Healthy Weight Gain & Nutrition Progress Export\n")
        sb.append("# Generated on:,$exportTimestamp\n")
        sb.append("# User Name:,${escapeCsv(userProfile?.name ?: "User")}\n")
        sb.append("# Current Weight (kg):,${userProfile?.currentWeightKg ?: 0f}\n")
        sb.append("# Target Goal Weight (kg):,${userProfile?.targetWeightKg ?: 0f}\n")
        sb.append("# Daily Calorie Target (kcal):,${userProfile?.dailyCalorieTarget ?: 2800}\n")
        sb.append("# Daily Surplus (kcal):,${userProfile?.calorieSurplus ?: 500}\n")
        sb.append("# Daily Protein Target (g):,${userProfile?.dailyProteinTargetGrams ?: 130}\n")
        sb.append("\n")

        // 2. Weight Check-In Progression Section
        sb.append("=== SECTION 1: WEIGHT CHECK-INS ===\n")
        sb.append("Date,Weight (kg),Change from Previous (kg),Notes\n")
        val sortedWeightLogs = weightLogs.sortedBy { it.dateString }
        var previousWeight = sortedWeightLogs.firstOrNull()?.weightKg
        sortedWeightLogs.forEach { entry ->
            val change = if (previousWeight != null) entry.weightKg - previousWeight!! else 0f
            val changeStr = if (change >= 0) "+${String.format(Locale.US, "%.1f", change)}" else String.format(Locale.US, "%.1f", change)
            sb.append("${entry.dateString},${entry.weightKg},$changeStr,${escapeCsv(entry.note)}\n")
            previousWeight = entry.weightKg
        }
        sb.append("\n")

        // 3. Daily Summary Aggregation Section
        sb.append("=== SECTION 2: DAILY SUMMARY & SURPLUS PERFORMANCE ===\n")
        sb.append("Date,Total Calories (kcal),Target (kcal),Surplus Status,Protein (g),Carbs (g),Fats (g),Water (ml),Sleep (hrs)\n")

        val allDates = (foodLogs.map { it.dateString } +
                weightLogs.map { it.dateString } +
                waterLogs.map { it.dateString } +
                sleepLogs.map { it.dateString }).distinct().sortedDescending()

        val targetCal = userProfile?.dailyCalorieTarget ?: 2800

        allDates.forEach { date ->
            val dayFoods = foodLogs.filter { it.dateString == date }
            val dayCals = dayFoods.sumOf { it.calories }
            val dayProt = dayFoods.map { it.protein }.sum()
            val dayCarbs = dayFoods.map { it.carbs }.sum()
            val dayFats = dayFoods.map { it.fats }.sum()
            val dayWater = waterLogs.filter { it.dateString == date }.sumOf { it.amountMl }
            val daySleep = sleepLogs.firstOrNull { it.dateString == date }?.hours ?: 0f

            val status = if (dayCals >= targetCal) "SURPLUS MET (+${dayCals - targetCal} kcal)" else "BELOW TARGET (-${targetCal - dayCals} kcal)"

            sb.append("$date,$dayCals,$targetCal,$status,${String.format(Locale.US, "%.1f", dayProt)},${String.format(Locale.US, "%.1f", dayCarbs)},${String.format(Locale.US, "%.1f", dayFats)},$dayWater,${String.format(Locale.US, "%.1f", daySleep)}\n")
        }
        sb.append("\n")

        // 4. Detailed Food & Calorie Logs Section
        sb.append("=== SECTION 3: DETAILED MEAL LOGS ===\n")
        sb.append("Date,Meal Type,Food Name,Serving,Multiplier,Calories (kcal),Protein (g),Carbs (g),Fats (g)\n")
        foodLogs.sortedByDescending { it.loggedAtTimestamp }.forEach { food ->
            sb.append("${food.dateString},${food.mealType},${escapeCsv(food.foodName)},${escapeCsv(food.servingDescription)},${food.quantity},${food.calories},${String.format(Locale.US, "%.1f", food.protein)},${String.format(Locale.US, "%.1f", food.carbs)},${String.format(Locale.US, "%.1f", food.fats)}\n")
        }

        return sb.toString()
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    fun writeCsvToUri(context: Context, uri: Uri, csvContent: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(csvContent.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun shareCsvFile(context: Context, csvContent: String) {
        try {
            val fileName = "gainup_progress_export.csv"
            val cacheFile = File(context.cacheDir, fileName)
            FileOutputStream(cacheFile).use { fos ->
                fos.write(csvContent.toByteArray(Charsets.UTF_8))
                fos.flush()
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cacheFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "GainUp Healthy Weight-Gain Progress Export")
                putExtra(Intent.EXTRA_TEXT, "Here is my GainUp progress backup containing weight logs, calorie surplus history, and daily nutrition metrics.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Export GainUp CSV Backup via")
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error sharing CSV: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
