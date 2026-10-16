package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FoodLogEntry
import com.example.data.local.entity.SleepLogEntry
import com.example.data.local.entity.UserProfile
import com.example.data.local.entity.WaterLogEntry
import com.example.data.local.entity.WeightLogEntry
import com.example.ui.components.CalorieProgressCard
import com.example.ui.components.MacroBreakdownCard
import com.example.ui.components.WaterIntakeCard
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Orange400
import com.example.ui.theme.Orange500
import com.example.ui.theme.ProteinColor
import com.example.ui.theme.SleepPurple
import com.example.ui.theme.WaterBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    userProfile: UserProfile?,
    foodLogs: List<FoodLogEntry>,
    waterLogs: List<WaterLogEntry>,
    sleepLog: SleepLogEntry?,
    latestWeight: WeightLogEntry?,
    onOpenLogFood: () -> Unit,
    onOpenAiScan: () -> Unit,
    onOpenLogWeight: () -> Unit,
    onOpenLogSleep: () -> Unit,
    onAddWater: (Int) -> Unit,
    onResetWater: () -> Unit,
    getSmartAdvice: (Int, Int, Float, Int, Int, Int) -> Pair<String, String>
) {
    val targetCalories = userProfile?.dailyCalorieTarget ?: 2800
    val targetProtein = userProfile?.dailyProteinTargetGrams ?: 130
    val targetCarbs = userProfile?.dailyCarbsTargetGrams ?: 360
    val targetFats = userProfile?.dailyFatTargetGrams ?: 85
    val targetWaterMl = userProfile?.dailyWaterGoalMl ?: 3000
    val surplus = userProfile?.calorieSurplus ?: 500

    val consumedCalories = foodLogs.sumOf { it.calories }
    val consumedProtein = foodLogs.map { it.protein }.sum()
    val consumedCarbs = foodLogs.map { it.carbs }.sum()
    val consumedFats = foodLogs.map { it.fats }.sum()
    val consumedWaterMl = waterLogs.sumOf { it.amountMl }

    val currentWeight = latestWeight?.weightKg ?: userProfile?.currentWeightKg ?: 60.0f
    val targetWeight = userProfile?.targetWeightKg ?: 70.0f
    val weightRemaining = (targetWeight - currentWeight).coerceAtLeast(0f)

    val (smartTitle, smartMessage) = getSmartAdvice(
        consumedCalories, targetCalories,
        consumedProtein, targetProtein,
        consumedWaterMl, targetWaterMl
    )

    val todayFormatted = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .testTag("home_screen")
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Salam, ${userProfile?.name ?: "Champion"}!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = todayFormatted,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Streak & Check-in Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Orange500.copy(alpha = 0.12f),
                modifier = Modifier.clickable { onOpenLogWeight() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Orange500,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$currentWeight kg",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = Orange500
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Weight Goal Roadmap Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Brush.linearGradient(listOf(Emerald500, Orange500)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Target: $targetWeight kg",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${String.format("%.1f", weightRemaining)} kg to goal",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onOpenLogWeight() }
                    ) {
                        Text(
                            text = "Log Weight",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Action Dock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HeroActionTile(
                title = "Log Meal",
                subtitle = "40+ Desi foods",
                icon = Icons.Default.Fastfood,
                accentColor = Emerald500,
                onClick = onOpenLogFood,
                modifier = Modifier.weight(1f).testTag("quick_action_log_food")
            )

            HeroActionTile(
                title = "AI Food Scan",
                subtitle = "Multimodal AI",
                icon = Icons.Default.AutoAwesome,
                accentColor = Orange500,
                onClick = onOpenAiScan,
                modifier = Modifier.weight(1f).testTag("quick_action_ai_scan")
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Hero Calorie Surplus Gauge
        CalorieProgressCard(
            consumedCalories = consumedCalories,
            targetCalories = targetCalories,
            surplusCalories = surplus
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Macro Nutrition Split (Protein Priority)
        MacroBreakdownCard(
            currentProtein = consumedProtein,
            targetProtein = targetProtein,
            currentCarbs = consumedCarbs,
            targetCarbs = targetCarbs,
            currentFats = consumedFats,
            targetFats = targetFats
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Smart Pakistani Nutrition Advice Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("smart_advice_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Orange500.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Orange500,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = smartTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Orange500
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = smartMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Water Intake Card
        WaterIntakeCard(
            currentWaterMl = consumedWaterMl,
            targetWaterMl = targetWaterMl,
            onAddWater = onAddWater,
            onResetWater = onResetWater
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Sleep Recovery Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sleep_summary_card")
                .clickable { onOpenLogSleep() },
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(SleepPurple.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = SleepPurple,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Last Night's Sleep",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (sleepLog != null) "${sleepLog.hours} hrs (${sleepLog.quality} Quality)" else "Tap to log night rest",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SleepPurple.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (sleepLog != null) "Edit" else "+ Log",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SleepPurple,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun HeroActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}
