package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiFoodScanDialog
import com.example.ui.components.ExportProgressDialog
import com.example.ui.components.LogFoodDialog
import com.example.ui.components.LogSleepDialog
import com.example.ui.components.LogWeightDialog
import com.example.ui.screens.FoodScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlanScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.theme.GainUpGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GainUpViewModel
import com.example.ui.viewmodel.GainUpViewModelFactory

enum class NavigationScreen(val label: String) {
    HOME("Home"),
    FOOD("Food"),
    PROGRESS("Progress"),
    PLAN("Plan"),
    MORE("More")
}

class MainActivity : ComponentActivity() {

    private val viewModel: GainUpViewModel by viewModels {
        GainUpViewModelFactory((application as GainUpApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                GainUpApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GainUpApp(viewModel: GainUpViewModel) {
    val context = LocalContext.current

    // Request Notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Permission granted or denied */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val foodLogs by viewModel.foodLogsForSelectedDate.collectAsStateWithLifecycle()
    val waterLogs by viewModel.waterLogsForSelectedDate.collectAsStateWithLifecycle()
    val sleepLog by viewModel.sleepLogForSelectedDate.collectAsStateWithLifecycle()
    val allWeightLogs by viewModel.allWeightLogs.collectAsStateWithLifecycle()
    val allFoodLogs by viewModel.allFoodLogs.collectAsStateWithLifecycle()
    val allSleepLogs by viewModel.allSleepLogs.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()

    val isAiScanning by viewModel.isAiScanning.collectAsStateWithLifecycle()
    val aiScanResult by viewModel.aiScanResult.collectAsStateWithLifecycle()
    val isGeneratingAiPlan by viewModel.isGeneratingAiPlan.collectAsStateWithLifecycle()
    val generatedWeeklyPlan by viewModel.generatedWeeklyPlan.collectAsStateWithLifecycle()

    // Navigation and Dialog state
    var currentScreen by remember { mutableStateOf(NavigationScreen.HOME) }
    var showLogFoodDialog by remember { mutableStateOf(false) }
    var showAiScanDialog by remember { mutableStateOf(false) }
    var showLogWeightDialog by remember { mutableStateOf(false) }
    var showLogSleepDialog by remember { mutableStateOf(false) }
    var showExportCsvDialog by remember { mutableStateOf(false) }

    // Check onboarding
    if (userProfile == null || !userProfile!!.isOnboarded) {
        OnboardingScreen(
            onComplete = { name, curWeight, tarWeight, height, age, gender, activity, surplus, waterGoal ->
                viewModel.completeOnboarding(
                    name = name,
                    currentWeightKg = curWeight,
                    targetWeightKg = tarWeight,
                    heightCm = height,
                    age = age,
                    gender = gender,
                    activityLevel = activity,
                    surplus = surplus,
                    waterGoalMl = waterGoal
                )
            }
        )
        return
    }

    // Handle Back button to return to HOME tab if on another screen
    BackHandler(enabled = currentScreen != NavigationScreen.HOME) {
        currentScreen = NavigationScreen.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == NavigationScreen.HOME,
                    onClick = { currentScreen = NavigationScreen.HOME },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == NavigationScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_home"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == NavigationScreen.FOOD,
                    onClick = { currentScreen = NavigationScreen.FOOD },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == NavigationScreen.FOOD) Icons.Filled.Restaurant else Icons.Outlined.Restaurant,
                            contentDescription = "Food"
                        )
                    },
                    label = { Text("Food", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_food"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == NavigationScreen.PROGRESS,
                    onClick = { currentScreen = NavigationScreen.PROGRESS },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == NavigationScreen.PROGRESS) Icons.AutoMirrored.Filled.ShowChart else Icons.AutoMirrored.Outlined.ShowChart,
                            contentDescription = "Progress"
                        )
                    },
                    label = { Text("Progress", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_progress"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == NavigationScreen.PLAN,
                    onClick = { currentScreen = NavigationScreen.PLAN },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == NavigationScreen.PLAN) Icons.AutoMirrored.Filled.MenuBook else Icons.AutoMirrored.Outlined.MenuBook,
                            contentDescription = "Plan"
                        )
                    },
                    label = { Text("Plan", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_plan"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == NavigationScreen.MORE,
                    onClick = { currentScreen = NavigationScreen.MORE },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == NavigationScreen.MORE) Icons.Filled.MoreHoriz else Icons.Outlined.MoreHoriz,
                            contentDescription = "More"
                        )
                    },
                    label = { Text("More", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_more"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                NavigationScreen.HOME -> {
                    HomeScreen(
                        userProfile = userProfile,
                        foodLogs = foodLogs,
                        waterLogs = waterLogs,
                        sleepLog = sleepLog,
                        latestWeight = allWeightLogs.lastOrNull(),
                        onOpenLogFood = { showLogFoodDialog = true },
                        onOpenAiScan = {
                            viewModel.clearAiScanResult()
                            showAiScanDialog = true
                        },
                        onOpenLogWeight = { showLogWeightDialog = true },
                        onOpenLogSleep = { showLogSleepDialog = true },
                        onAddWater = { amount -> viewModel.logWater(amount) },
                        onResetWater = { viewModel.clearWater() },
                        getSmartAdvice = { consumedCal, targetCal, consumedProt, targetProt, consumedWat, targetWat ->
                            viewModel.getDynamicSmartAdvice(
                                consumedCalories = consumedCal,
                                targetCalories = targetCal,
                                consumedProtein = consumedProt,
                                targetProtein = targetProt,
                                consumedWaterMl = consumedWat,
                                waterGoalMl = targetWat
                            )
                        }
                    )
                }

                NavigationScreen.FOOD -> {
                    FoodScreen(
                        foodLogs = foodLogs,
                        selectedDate = selectedDate,
                        targetCalories = userProfile?.dailyCalorieTarget ?: 2800,
                        targetProtein = userProfile?.dailyProteinTargetGrams ?: 130,
                        onDateChange = { newDate -> viewModel.setSelectedDate(newDate) },
                        onOpenLogFood = { showLogFoodDialog = true },
                        onOpenAiScan = {
                            viewModel.clearAiScanResult()
                            showAiScanDialog = true
                        },
                        onDeleteLog = { id -> viewModel.deleteFoodLog(id) }
                    )
                }

                NavigationScreen.PROGRESS -> {
                    ProgressScreen(
                        weightLogs = allWeightLogs,
                        foodLogs = allFoodLogs,
                        sleepLogs = allSleepLogs,
                        currentWeightKg = allWeightLogs.lastOrNull()?.weightKg ?: userProfile?.currentWeightKg ?: 60f,
                        targetWeightKg = userProfile?.targetWeightKg ?: 70f,
                        targetCalories = userProfile?.dailyCalorieTarget ?: 2800,
                        onOpenLogWeight = { showLogWeightDialog = true },
                        onOpenLogSleep = { showLogSleepDialog = true },
                        onOpenExportCsv = { showExportCsvDialog = true },
                        onDeleteWeightLog = { id -> viewModel.deleteWeightLog(id) }
                    )
                }

                NavigationScreen.PLAN -> {
                    PlanScreen(
                        targetCalories = userProfile?.dailyCalorieTarget ?: 2800,
                        targetProtein = userProfile?.dailyProteinTargetGrams ?: 130,
                        isGeneratingAiPlan = isGeneratingAiPlan,
                        aiWeeklyPlan = generatedWeeklyPlan,
                        onGenerateAiPlan = { viewModel.generateAiWeeklyPlan() },
                        onQuickLogMeal = { foodName, mealType, serving, calories, protein, carbs, fats ->
                            viewModel.logFood(
                                foodName = foodName,
                                mealType = mealType,
                                servingDesc = serving,
                                quantity = 1.0f,
                                calories = calories,
                                protein = protein,
                                carbs = carbs,
                                fats = fats
                            )
                        }
                    )
                }

                NavigationScreen.MORE -> {
                    MoreScreen(
                        userProfile = userProfile,
                        reminders = reminders,
                        onUpdateProfile = { updated -> viewModel.updateProfile(updated) },
                        onToggleReminder = { reminder, ctx -> viewModel.toggleReminder(reminder, ctx) },
                        onTestReminder = { reminder, ctx -> viewModel.testReminderNotification(reminder, ctx) },
                        onOpenLogSleep = { showLogSleepDialog = true },
                        onOpenExportCsv = { showExportCsvDialog = true }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showLogFoodDialog) {
        LogFoodDialog(
            foodItems = searchResults,
            searchQuery = searchQuery,
            onSearchQueryChange = { q -> viewModel.setSearchQuery(q) },
            onConfirmLog = { foodName, mealType, servingDesc, quantity, calories, protein, carbs, fats ->
                viewModel.logFood(
                    foodName = foodName,
                    mealType = mealType,
                    servingDesc = servingDesc,
                    quantity = quantity,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fats = fats
                )
            },
            onDismiss = { showLogFoodDialog = false }
        )
    }

    if (showAiScanDialog) {
        AiFoodScanDialog(
            isScanning = isAiScanning,
            scanResult = aiScanResult,
            onScanPhoto = { bitmap -> viewModel.scanFoodPhoto(bitmap) },
            onConfirmLog = { foodName, mealType, serving, calories, protein, carbs, fats ->
                viewModel.logFood(
                    foodName = foodName,
                    mealType = mealType,
                    servingDesc = serving,
                    quantity = 1.0f,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fats = fats
                )
            },
            onDismiss = {
                viewModel.clearAiScanResult()
                showAiScanDialog = false
            }
        )
    }

    if (showLogWeightDialog) {
        LogWeightDialog(
            initialWeight = allWeightLogs.lastOrNull()?.weightKg ?: userProfile?.currentWeightKg ?: 60f,
            onConfirmWeight = { weight, note ->
                viewModel.logWeight(weight, note)
            },
            onDismiss = { showLogWeightDialog = false }
        )
    }

    if (showLogSleepDialog) {
        LogSleepDialog(
            initialHours = sleepLog?.hours ?: 7.5f,
            onConfirmSleep = { hours, bedtime, wakeTime, quality ->
                viewModel.logSleep(hours, bedtime, wakeTime, quality)
            },
            onDismiss = { showLogSleepDialog = false }
        )
    }

    if (showExportCsvDialog) {
        ExportProgressDialog(
            userProfile = userProfile,
            weightLogs = allWeightLogs,
            foodLogs = allFoodLogs,
            waterLogs = waterLogs,
            sleepLogs = allSleepLogs,
            onDismiss = { showExportCsvDialog = false }
        )
    }
}
