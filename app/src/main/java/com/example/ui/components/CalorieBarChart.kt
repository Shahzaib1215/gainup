package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FoodLogEntry
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Orange400
import com.example.ui.theme.Orange500
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class DayCalorieStat(
    val dayLabel: String,
    val dateString: String,
    val calories: Int,
    val isToday: Boolean
)

@Composable
fun WeeklyCalorieBarChartCard(
    foodLogs: List<FoodLogEntry>,
    targetCalories: Int,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val todayStr = dateFormat.format(calendar.time)

    val last7Days = (6 downTo 0).map { offset ->
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }
        val dateStr = dateFormat.format(cal.time)
        val dayLabel = dayFormat.format(cal.time)
        val cals = foodLogs.filter { it.dateString == dateStr }.sumOf { it.calories }
        DayCalorieStat(
            dayLabel = dayLabel,
            dateString = dateStr,
            calories = cals,
            isToday = dateStr == todayStr
        )
    }

    val maxCalorieInView = (last7Days.maxOfOrNull { it.calories } ?: targetCalories)
        .coerceAtLeast(targetCalories).coerceAtLeast(2000)

    val nonZeroDays = last7Days.filter { it.calories > 0 }
    val averageCalories = if (nonZeroDays.isNotEmpty()) nonZeroDays.sumOf { it.calories } / nonZeroDays.size else 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_calorie_chart_card"),
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
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Calorie Intake",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Average: $averageCalories kcal/day",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = (if (averageCalories >= targetCalories) Emerald500 else Orange500).copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (averageCalories >= targetCalories) "Surplus Met" else "Surplus Target",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (averageCalories >= targetCalories) Emerald500 else Orange500,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                last7Days.forEach { stat ->
                    val fraction = (stat.calories.toFloat() / maxCalorieInView.toFloat()).coerceIn(0.05f, 1f)
                    val isSurplus = stat.calories >= targetCalories

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (stat.calories > 0) {
                            Text(
                                text = "${stat.calories}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height((95 * fraction).dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(
                                    when {
                                        stat.calories == 0 -> MaterialTheme.colorScheme.surfaceVariant
                                        isSurplus -> Emerald500
                                        stat.isToday -> Orange500
                                        else -> Orange400.copy(alpha = 0.8f)
                                    }
                                )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = stat.dayLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (stat.isToday) FontWeight.Bold else FontWeight.Medium,
                            color = if (stat.isToday) Emerald500 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
