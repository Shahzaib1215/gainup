package com.example.data.repository

import com.example.data.local.PreloadedFoodData
import com.example.data.local.dao.GainUpDao
import com.example.data.local.entity.DietPlanEntity
import com.example.data.local.entity.FoodItem
import com.example.data.local.entity.FoodLogEntry
import com.example.data.local.entity.ReminderSetting
import com.example.data.local.entity.SleepLogEntry
import com.example.data.local.entity.UserProfile
import com.example.data.local.entity.WaterLogEntry
import com.example.data.local.entity.WeightLogEntry
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GainUpRepository(private val dao: GainUpDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()

    suspend fun getUserProfileOnce(): UserProfile? = dao.getUserProfileOnce()

    suspend fun saveUserProfile(profile: UserProfile) {
        dao.saveUserProfile(profile)
    }

    // Food Logs
    fun getFoodLogsForDate(date: String): Flow<List<FoodLogEntry>> = dao.getFoodLogsByDate(date)

    fun getAllFoodLogs(): Flow<List<FoodLogEntry>> = dao.getAllFoodLogs()

    suspend fun logFood(entry: FoodLogEntry): Long = dao.insertFoodLog(entry)

    suspend fun deleteFoodLog(id: Long) = dao.deleteFoodLogById(id)

    // Food Items
    val allFoodItems: Flow<List<FoodItem>> = dao.getAllFoodItems()

    fun searchFoodItems(query: String): Flow<List<FoodItem>> = dao.searchFoodItems(query)

    fun getFoodItemsByCategory(category: String): Flow<List<FoodItem>> = dao.getFoodItemsByCategory(category)

    suspend fun checkAndSeedInitialData() {
        if (dao.getFoodItemCount() == 0) {
            dao.insertFoodItems(PreloadedFoodData.getDefaultFoodItems())
            dao.insertReminders(PreloadedFoodData.getDefaultReminders())
        }
    }

    // Water Logs
    fun getWaterLogsForDate(date: String): Flow<List<WaterLogEntry>> = dao.getWaterLogsByDate(date)

    suspend fun logWater(amountMl: Int, date: String = getTodayDateString()): Long {
        return dao.insertWaterLog(
            WaterLogEntry(
                amountMl = amountMl,
                loggedAtTimestamp = System.currentTimeMillis(),
                dateString = date
            )
        )
    }

    suspend fun clearWaterForDate(date: String) {
        dao.clearWaterLogsForDate(date)
    }

    // Weight Logs
    val allWeightLogs: Flow<List<WeightLogEntry>> = dao.getAllWeightLogs()

    val recentWeightLogs: Flow<List<WeightLogEntry>> = dao.getRecentWeightLogs()

    suspend fun logWeight(weightKg: Float, note: String = "", date: String = getTodayDateString()): Long {
        val id = dao.insertWeightLog(
            WeightLogEntry(
                weightKg = weightKg,
                note = note,
                loggedAtTimestamp = System.currentTimeMillis(),
                dateString = date
            )
        )
        // Also update current weight on UserProfile if present
        val profile = dao.getUserProfileOnce()
        if (profile != null) {
            val updated = UserProfile.calculateNutritionGoals(
                currentWeightKg = weightKg,
                targetWeightKg = profile.targetWeightKg,
                heightCm = profile.heightCm,
                age = profile.age,
                gender = profile.gender,
                activityLevel = profile.activityLevel,
                surplus = profile.calorieSurplus,
                waterGoalMl = profile.dailyWaterGoalMl
            ).copy(name = profile.name, id = profile.id, isOnboarded = profile.isOnboarded)
            dao.saveUserProfile(updated)
        }
        return id
    }

    suspend fun deleteWeightLog(id: Long) = dao.deleteWeightLogById(id)

    // Sleep Logs
    val allSleepLogs: Flow<List<SleepLogEntry>> = dao.getAllSleepLogs()

    fun getSleepLogForDate(date: String): Flow<SleepLogEntry?> = dao.getSleepLogByDate(date)

    suspend fun logSleep(hours: Float, bedtime: String, wakeTime: String, quality: String, date: String = getTodayDateString()): Long {
        return dao.insertSleepLog(
            SleepLogEntry(
                hours = hours,
                bedtime = bedtime,
                wakeTime = wakeTime,
                quality = quality,
                loggedAtTimestamp = System.currentTimeMillis(),
                dateString = date
            )
        )
    }

    // Reminders
    val allReminders: Flow<List<ReminderSetting>> = dao.getAllReminders()

    suspend fun updateReminder(reminder: ReminderSetting) {
        dao.updateReminder(reminder)
    }

    // Diet Plans
    val allDietPlans: Flow<List<DietPlanEntity>> = dao.getAllDietPlans()

    suspend fun saveDietPlan(plan: DietPlanEntity): Long = dao.insertDietPlan(plan)

    suspend fun deleteDietPlan(id: Long) = dao.deleteDietPlanById(id)

    companion object {
        fun getTodayDateString(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return formatter.format(Date())
        }
    }
}
