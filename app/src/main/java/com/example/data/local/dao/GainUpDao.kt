package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.DietPlanEntity
import com.example.data.local.entity.FoodItem
import com.example.data.local.entity.FoodLogEntry
import com.example.data.local.entity.ReminderSetting
import com.example.data.local.entity.SleepLogEntry
import com.example.data.local.entity.UserProfile
import com.example.data.local.entity.WaterLogEntry
import com.example.data.local.entity.WeightLogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface GainUpDao {

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    // --- Food Logs ---
    @Query("SELECT * FROM food_logs WHERE dateString = :date ORDER BY loggedAtTimestamp ASC")
    fun getFoodLogsByDate(date: String): Flow<List<FoodLogEntry>>

    @Query("SELECT * FROM food_logs ORDER BY loggedAtTimestamp DESC LIMIT :limit")
    fun getRecentFoodLogs(limit: Int = 100): Flow<List<FoodLogEntry>>

    @Query("SELECT * FROM food_logs ORDER BY loggedAtTimestamp DESC")
    fun getAllFoodLogs(): Flow<List<FoodLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodLog(entry: FoodLogEntry): Long

    @Query("DELETE FROM food_logs WHERE id = :id")
    suspend fun deleteFoodLogById(id: Long)

    // --- Food Database (Search & Catalog) ---
    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' OR urduName LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFoodItems(query: String): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items ORDER BY isDesi DESC, name ASC")
    fun getAllFoodItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE category = :category ORDER BY name ASC")
    fun getFoodItemsByCategory(category: String): Flow<List<FoodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItems(items: List<FoodItem>)

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun getFoodItemCount(): Int

    // --- Water Logs ---
    @Query("SELECT * FROM water_logs WHERE dateString = :date ORDER BY loggedAtTimestamp ASC")
    fun getWaterLogsByDate(date: String): Flow<List<WaterLogEntry>>

    @Query("SELECT * FROM water_logs ORDER BY loggedAtTimestamp DESC")
    fun getAllWaterLogs(): Flow<List<WaterLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(entry: WaterLogEntry): Long

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteWaterLogById(id: Long)

    @Query("DELETE FROM water_logs WHERE dateString = :date")
    suspend fun clearWaterLogsForDate(date: String)

    // --- Weight Logs ---
    @Query("SELECT * FROM weight_logs ORDER BY dateString ASC, loggedAtTimestamp ASC")
    fun getAllWeightLogs(): Flow<List<WeightLogEntry>>

    @Query("SELECT * FROM weight_logs ORDER BY dateString DESC, loggedAtTimestamp DESC LIMIT :limit")
    fun getRecentWeightLogs(limit: Int = 30): Flow<List<WeightLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(entry: WeightLogEntry): Long

    @Query("DELETE FROM weight_logs WHERE id = :id")
    suspend fun deleteWeightLogById(id: Long)

    // --- Sleep Logs ---
    @Query("SELECT * FROM sleep_logs ORDER BY dateString ASC, loggedAtTimestamp ASC")
    fun getAllSleepLogs(): Flow<List<SleepLogEntry>>

    @Query("SELECT * FROM sleep_logs WHERE dateString = :date LIMIT 1")
    fun getSleepLogByDate(date: String): Flow<SleepLogEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(entry: SleepLogEntry): Long

    @Query("DELETE FROM sleep_logs WHERE id = :id")
    suspend fun deleteSleepLogById(id: Long)

    // --- Reminders ---
    @Query("SELECT * FROM reminder_settings ORDER BY id ASC")
    fun getAllReminders(): Flow<List<ReminderSetting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderSetting>)

    @Update
    suspend fun updateReminder(reminder: ReminderSetting)

    // --- Diet Plans ---
    @Query("SELECT * FROM diet_plans ORDER BY createdAt DESC")
    fun getAllDietPlans(): Flow<List<DietPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietPlan(plan: DietPlanEntity): Long

    @Query("DELETE FROM diet_plans WHERE id = :id")
    suspend fun deleteDietPlanById(id: Long)
}
