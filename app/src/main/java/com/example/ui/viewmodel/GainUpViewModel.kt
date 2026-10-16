package com.example.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiFoodScanResult
import com.example.data.ai.AiWeeklyPlanResult
import com.example.data.ai.GeminiNutritionService
import com.example.data.local.entity.DietPlanEntity
import com.example.data.local.entity.FoodItem
import com.example.data.local.entity.FoodLogEntry
import com.example.data.local.entity.ReminderSetting
import com.example.data.local.entity.SleepLogEntry
import com.example.data.local.entity.UserProfile
import com.example.data.local.entity.WaterLogEntry
import com.example.data.local.entity.WeightLogEntry
import com.example.data.repository.GainUpRepository
import com.example.notification.GainUpNotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class GainUpViewModel(private val repository: GainUpRepository) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedDate = MutableStateFlow(GainUpRepository.getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val foodLogsForSelectedDate: StateFlow<List<FoodLogEntry>> = _selectedDate
        .flatMapLatest { date -> repository.getFoodLogsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val waterLogsForSelectedDate: StateFlow<List<WaterLogEntry>> = _selectedDate
        .flatMapLatest { date -> repository.getWaterLogsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sleepLogForSelectedDate: StateFlow<SleepLogEntry?> = _selectedDate
        .flatMapLatest { date -> repository.getSleepLogForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allWeightLogs: StateFlow<List<WeightLogEntry>> = repository.allWeightLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFoodLogs: StateFlow<List<FoodLogEntry>> = repository.getAllFoodLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSleepLogs: StateFlow<List<SleepLogEntry>> = repository.allSleepLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFoodItems: StateFlow<List<FoodItem>> = repository.allFoodItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<FoodItem>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.allFoodItems else repository.searchFoodItems(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<ReminderSetting>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dietPlans: StateFlow<List<DietPlanEntity>> = repository.allDietPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI States
    private val _isAiScanning = MutableStateFlow(false)
    val isAiScanning: StateFlow<Boolean> = _isAiScanning.asStateFlow()

    private val _aiScanResult = MutableStateFlow<AiFoodScanResult?>(null)
    val aiScanResult: StateFlow<AiFoodScanResult?> = _aiScanResult.asStateFlow()

    private val _isGeneratingAiPlan = MutableStateFlow(false)
    val isGeneratingAiPlan: StateFlow<Boolean> = _isGeneratingAiPlan.asStateFlow()

    private val _generatedWeeklyPlan = MutableStateFlow<AiWeeklyPlanResult?>(null)
    val generatedWeeklyPlan: StateFlow<AiWeeklyPlanResult?> = _generatedWeeklyPlan.asStateFlow()

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun completeOnboarding(
        name: String,
        currentWeightKg: Float,
        targetWeightKg: Float,
        heightCm: Float,
        age: Int,
        gender: String,
        activityLevel: String,
        surplus: Int,
        waterGoalMl: Int
    ) {
        viewModelScope.launch {
            val profile = UserProfile.calculateNutritionGoals(
                currentWeightKg = currentWeightKg,
                targetWeightKg = targetWeightKg,
                heightCm = heightCm,
                age = age,
                gender = gender,
                activityLevel = activityLevel,
                surplus = surplus,
                waterGoalMl = waterGoalMl
            ).copy(name = name)
            repository.saveUserProfile(profile)

            // Log initial weight
            repository.logWeight(currentWeightKg, "Starting weight on GainUp", GainUpRepository.getTodayDateString())
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun logFood(
        foodName: String,
        mealType: String,
        servingDesc: String,
        quantity: Float,
        calories: Int,
        protein: Float,
        carbs: Float,
        fats: Float
    ) {
        viewModelScope.launch {
            repository.logFood(
                FoodLogEntry(
                    foodName = foodName,
                    mealType = mealType,
                    servingDescription = servingDesc,
                    quantity = quantity,
                    calories = (calories * quantity).toInt(),
                    protein = protein * quantity,
                    carbs = carbs * quantity,
                    fats = fats * quantity,
                    dateString = _selectedDate.value
                )
            )
        }
    }

    fun deleteFoodLog(id: Long) {
        viewModelScope.launch {
            repository.deleteFoodLog(id)
        }
    }

    fun logWater(amountMl: Int) {
        viewModelScope.launch {
            repository.logWater(amountMl, _selectedDate.value)
        }
    }

    fun clearWater() {
        viewModelScope.launch {
            repository.clearWaterForDate(_selectedDate.value)
        }
    }

    fun logWeight(weightKg: Float, note: String = "") {
        viewModelScope.launch {
            repository.logWeight(weightKg, note, GainUpRepository.getTodayDateString())
        }
    }

    fun deleteWeightLog(id: Long) {
        viewModelScope.launch {
            repository.deleteWeightLog(id)
        }
    }

    fun logSleep(hours: Float, bedtime: String = "", wakeTime: String = "", quality: String = "Good") {
        viewModelScope.launch {
            repository.logSleep(hours, bedtime, wakeTime, quality, _selectedDate.value)
        }
    }

    fun scanFoodPhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            _isAiScanning.value = true
            _aiScanResult.value = null
            val result = GeminiNutritionService.scanFoodPhoto(bitmap)
            _isAiScanning.value = false
            _aiScanResult.value = result.getOrNull()
        }
    }

    fun clearAiScanResult() {
        _aiScanResult.value = null
    }

    fun generateAiWeeklyPlan() {
        viewModelScope.launch {
            val profile = userProfile.value
            val targetCal = profile?.dailyCalorieTarget ?: 2800
            val targetProt = profile?.dailyProteinTargetGrams ?: 130
            val curWeight = profile?.currentWeightKg ?: 60f
            val targetWeight = profile?.targetWeightKg ?: 70f

            _isGeneratingAiPlan.value = true
            val result = GeminiNutritionService.generateWeeklyDietPlan(
                targetCalories = targetCal,
                targetProtein = targetProt,
                currentWeightKg = curWeight,
                targetWeightKg = targetWeight
            )
            _isGeneratingAiPlan.value = false
            _generatedWeeklyPlan.value = result.getOrNull()
        }
    }

    fun toggleReminder(reminder: ReminderSetting, context: Context) {
        val updated = reminder.copy(isEnabled = !reminder.isEnabled)
        viewModelScope.launch {
            repository.updateReminder(updated)
            if (updated.isEnabled) {
                GainUpNotificationHelper.scheduleDailyReminder(
                    context = context,
                    reminderId = updated.id,
                    title = updated.title,
                    message = updated.message,
                    timeString = updated.time
                )
            } else {
                GainUpNotificationHelper.cancelReminder(context, updated.id)
            }
        }
    }

    fun testReminderNotification(reminder: ReminderSetting, context: Context) {
        GainUpNotificationHelper.showNotification(
            context = context,
            id = (1000..9999).random(),
            title = reminder.title,
            message = "GainUp Alert: ${reminder.message}"
        )
    }

    fun getDynamicSmartAdvice(
        consumedCalories: Int,
        targetCalories: Int,
        consumedProtein: Float,
        targetProtein: Int,
        consumedWaterMl: Int,
        waterGoalMl: Int
    ): Pair<String, String> {
        val proteinDeficit = targetProtein - consumedProtein
        val calorieDeficit = targetCalories - consumedCalories

        return when {
            proteinDeficit > 35 -> {
                "Protein Low Hai Aaj" to "You still need ${proteinDeficit.toInt()}g protein. Add 2 boiled eggs, a chicken breast, or a glass of whole buffalo milk to fuel muscle recovery!"
            }
            calorieDeficit > 700 -> {
                "Calorie Surplus Needed" to "You have $calorieDeficit kcal left to hit your healthy weight-gain surplus. Try a banana-almond shake, peanut butter toast, or a plate of Haleem!"
            }
            consumedWaterMl < (waterGoalMl / 2) -> {
                "Hydration for Digestion" to "Calorie-dense Pakistani meals require plenty of water for efficient nutrient absorption. Grab a 500ml glass now!"
            }
            consumedCalories >= targetCalories && consumedProtein >= targetProtein -> {
                "Surplus Achieved!" to "Shabash! You smashed your calorie and protein goals today. Rest well tonight to allow muscle repair and steady growth."
            }
            else -> {
                "Desi Bulking Tip" to "Doodh pati and lassi are rich sources of effortless liquid calories. Consistent daily surplus guarantees steady weight gain!"
            }
        }
    }
}

class GainUpViewModelFactory(private val repository: GainUpRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GainUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GainUpViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
