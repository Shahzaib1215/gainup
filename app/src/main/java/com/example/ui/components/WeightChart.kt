package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.WeightLogEntry
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Orange400
import com.example.ui.theme.Orange500

@Composable
fun WeightChartCard(
    weightLogs: List<WeightLogEntry>,
    targetWeightKg: Float,
    currentWeightKg: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weight_chart_card"),
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
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weight Progression",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Current $currentWeightKg kg → Goal $targetWeightKg kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val startWeight = weightLogs.firstOrNull()?.weightKg ?: currentWeightKg
                val netGained = currentWeightKg - startWeight
                val isPositive = netGained >= 0f

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = (if (isPositive) Emerald500 else Orange500).copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = if (isPositive) Emerald500 else Orange500,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPositive) "+${String.format("%.1f", netGained)} kg" else "${String.format("%.1f", netGained)} kg",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isPositive) Emerald500 else Orange500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Canvas Chart
            if (weightLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No weight logs recorded yet.\nTap 'Log Weight' to start visualizing your gain trend!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val chartPoints = weightLogs.sortedBy { it.loggedAtTimestamp }
                val minWeight = (chartPoints.minOf { it.weightKg }.coerceAtMost(targetWeightKg) - 1.5f).coerceAtLeast(30f)
                val maxWeight = (chartPoints.maxOf { it.weightKg }.coerceAtLeast(targetWeightKg) + 1.5f)
                val range = (maxWeight - minWeight).coerceAtLeast(1f)

                val lineColor = Emerald500
                val goalColor = Orange500

                Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val width = size.width
                        val height = size.height
                        val paddingBottom = 24.dp.toPx()
                        val paddingTop = 16.dp.toPx()
                        val paddingStart = 20.dp.toPx()
                        val paddingEnd = 20.dp.toPx()

                        val effectiveWidth = width - paddingStart - paddingEnd
                        val effectiveHeight = height - paddingTop - paddingBottom

                        // Dashed Target Goal Line
                        val targetY = paddingTop + (1f - (targetWeightKg - minWeight) / range) * effectiveHeight
                        val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)

                        drawLine(
                            color = goalColor.copy(alpha = 0.8f),
                            start = Offset(paddingStart, targetY),
                            end = Offset(width - paddingEnd, targetY),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = dashedEffect
                        )

                        // Compute points
                        val points = chartPoints.mapIndexed { index, item ->
                            val x = if (chartPoints.size == 1) {
                                paddingStart + effectiveWidth / 2f
                            } else {
                                paddingStart + (index.toFloat() / (chartPoints.size - 1)) * effectiveWidth
                            }
                            val y = paddingTop + (1f - (item.weightKg - minWeight) / range) * effectiveHeight
                            Offset(x, y)
                        }

                        // Smooth Bezier Curve Path
                        if (points.size > 1) {
                            val curvePath = Path().apply {
                                moveTo(points[0].x, points[0].y)
                                for (i in 0 until points.size - 1) {
                                    val current = points[i]
                                    val next = points[i + 1]
                                    val control1 = Offset((current.x + next.x) / 2f, current.y)
                                    val control2 = Offset((current.x + next.x) / 2f, next.y)
                                    cubicTo(control1.x, control1.y, control2.x, control2.y, next.x, next.y)
                                }
                            }

                            // Gradient Area
                            val fillPath = Path().apply {
                                addPath(curvePath)
                                lineTo(points.last().x, height - paddingBottom)
                                lineTo(points.first().x, height - paddingBottom)
                                close()
                            }

                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        lineColor.copy(alpha = 0.28f),
                                        Color.Transparent
                                    ),
                                    startY = paddingTop,
                                    endY = height - paddingBottom
                                )
                            )

                            // Stroke
                            drawPath(
                                path = curvePath,
                                color = lineColor,
                                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Draw Point Rings
                        points.forEach { pt ->
                            drawCircle(
                                color = Color.White,
                                radius = 6.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = lineColor,
                                radius = 4.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }

                // Legend
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Emerald500, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Logged Weights",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Orange500, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Goal Target: ${targetWeightKg}kg",
                            style = MaterialTheme.typography.labelSmall,
                            color = Orange500,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
