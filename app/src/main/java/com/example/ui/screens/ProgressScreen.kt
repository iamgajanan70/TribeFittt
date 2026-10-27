package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DailyStepRecordEntity
import com.example.data.local.DailyTaskRecordEntity
import com.example.data.local.DailyWaterRecordEntity
import com.example.data.local.NutritionMealEntity
import com.example.data.local.WeightLogEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.PermanentTask
import com.example.data.model.getIcon
import com.example.ui.components.AddWeightDialog
import com.example.ui.components.D3WeightHistoryChart
import com.example.ui.components.HabitContributionGraph
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnPrimary
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerHigh
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest
import com.example.utils.DateUtils

@Composable
fun ProgressScreen(
    weightLogs: List<WeightLogEntity>,
    dailyTaskRecords: List<DailyTaskRecordEntity> = emptyList(),
    waterRecords: List<DailyWaterRecordEntity> = emptyList(),
    stepRecords: List<DailyStepRecordEntity> = emptyList(),
    workouts: List<WorkoutLogEntity> = emptyList(),
    meals: List<NutritionMealEntity> = emptyList(),
    selectedDate: String = remember { DateUtils.getTodayDateString() },
    onSelectDate: (String) -> Unit = {},
    currentWeightKg: Float = 70f,
    targetWeightKg: Float = 70f,
    onAddWeightLog: (Float) -> Unit,
    onExportCsv: () -> Unit = {},
    isDarkMode: Boolean = false
) {
    var showAddWeightDialog by remember { mutableStateOf(false) }
    val subtleBorder = BorderStroke(0.75.dp, MonoOutlineVariant.copy(alpha = 0.45f))

    val todayStr = remember { DateUtils.getTodayDateString() }

    // Real Water Metrics from Room
    val todayWater = waterRecords.find { it.dateText == todayStr }?.waterConsumedLiters ?: 0.0f
    val avgWater = if (waterRecords.isNotEmpty()) {
        waterRecords.map { it.waterConsumedLiters }.average().toFloat()
    } else 0.0f
    val bestWaterRecord = waterRecords.maxByOrNull { it.waterConsumedLiters }

    // Real Step Metrics from Room
    val todaySteps = stepRecords.find { it.dateText == todayStr }?.stepCount ?: 0
    val avgSteps = if (stepRecords.isNotEmpty()) {
        stepRecords.map { it.stepCount }.average().toInt()
    } else 0
    val bestStepRecord = stepRecords.maxByOrNull { it.stepCount }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MonoBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
                .animateContentSize()
                .testTag("progress_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PROGRESS & ANALYTICS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MonoOnSurface,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "10 Permanent Goals Analytics • 100% Real Room Data",
                        fontSize = 11.sp,
                        color = MonoOnSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddWeightDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("log_weight_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Scale,
                        contentDescription = null,
                        tint = MonoOnPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Log Weight",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonoOnPrimary
                    )
                }
            }

            // 1. Recharts / D3 Logic Weight History Visualization Component (100% Real Room Data)
            D3WeightHistoryChart(
                weightLogs = weightLogs,
                targetWeightKg = targetWeightKg,
                onOpenAddWeightDialog = { showAddWeightDialog = true },
                isDarkMode = isDarkMode
            )

            // 2. GitHub-Style 10 Permanent Goals Contribution Graph
            HabitContributionGraph(
                allDailyTaskRecords = dailyTaskRecords,
                selectedDate = selectedDate,
                onSelectDate = onSelectDate,
                isDarkMode = isDarkMode
            )

            // 3. 10 Permanent Goals Performance Matrix
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MonoPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "10 PERMANENT GOALS PERFORMANCE",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Black,
                                color = MonoOnSurface
                            )
                        }
                    }

                    HorizontalDivider(color = MonoOutlineVariant, thickness = 0.5.dp)

                    PermanentTask.ALL_PERMANENT_TASKS.forEach { task ->
                        val taskRecords = dailyTaskRecords.filter { it.taskId == task.id }
                        val completedTimes = taskRecords.count { it.isCompleted }
                        val totalTrackedDays = dailyTaskRecords.map { it.dateText }.distinct().size.coerceAtLeast(1)
                        val rate = if (totalTrackedDays > 0) (completedTimes * 100) / totalTrackedDays else 0

                        val completedDatesForTask = taskRecords.filter { it.isCompleted }.map { it.dateText }.toSet()
                        val taskStreak = DateUtils.calculateStreak(completedDatesForTask)

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MonoSurfaceContainerLow,
                            border = subtleBorder,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = task.getIcon(),
                                        contentDescription = null,
                                        tint = MonoPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "${task.orderIndex}. ${task.title}",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MonoOnSurface
                                        )
                                        Text(
                                            text = "${task.category} • Target: ${task.description}",
                                            fontSize = 10.sp,
                                            color = MonoOnSurfaceVariant
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "$completedTimes Done",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MonoOnSurface
                                        )
                                        Text(
                                            text = "$taskStreak d streak",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (taskStreak > 0) Color(0xFF2E7D32) else MonoOnSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Daily Hydration & Steps Historical Analytics
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(MonoPrimary, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = MonoOnPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "DAILY HYDRATION & STEPS HISTORY",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MonoOnSurface,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Independent daily records preserved permanently in Room",
                                    fontSize = 10.sp,
                                    color = MonoOnSurfaceVariant
                                )
                            }
                        }
                    }

                    // Water & Step Real Analytics Summary Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricSummaryCard(
                            title = "TODAY WATER",
                            value = "${String.format("%.1f", todayWater)}L",
                            subValue = "Goal: 3.0L",
                            modifier = Modifier.weight(1f)
                        )
                        MetricSummaryCard(
                            title = "AVG WATER",
                            value = "${String.format("%.1f", avgWater)}L/day",
                            subValue = "${waterRecords.size} logged days",
                            modifier = Modifier.weight(1f)
                        )
                        MetricSummaryCard(
                            title = "TODAY STEPS",
                            value = "$todaySteps",
                            subValue = "Goal: 10,000",
                            modifier = Modifier.weight(1f)
                        )
                        MetricSummaryCard(
                            title = "AVG STEPS",
                            value = "$avgSteps",
                            subValue = "${stepRecords.size} active days",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (bestWaterRecord != null || bestStepRecord != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (bestWaterRecord != null) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MonoSurfaceContainerLow,
                                    border = subtleBorder
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = MonoPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = "BEST HYDRATION",
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MonoOnSurfaceVariant
                                            )
                                            Text(
                                                text = "${String.format("%.1f", bestWaterRecord.waterConsumedLiters)}L on ${DateUtils.formatShortDisplay(bestWaterRecord.dateText)}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MonoOnSurface
                                            )
                                        }
                                    }
                                }
                            }

                            if (bestStepRecord != null) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MonoSurfaceContainerLow,
                                    border = subtleBorder
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsWalk,
                                            contentDescription = null,
                                            tint = MonoPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = "BEST STEPS DAY",
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MonoOnSurfaceVariant
                                            )
                                            Text(
                                                text = "${bestStepRecord.stepCount} steps on ${DateUtils.formatShortDisplay(bestStepRecord.dateText)}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MonoOnSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Summary Performance KPIs (Workouts & Nutrition Overview)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                    shape = RoundedCornerShape(14.dp),
                    border = subtleBorder
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "COMPLETED WORKOUTS",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoOnSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${workouts.count { it.status == "Completed" }}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = MonoOnSurface
                        )
                        Text(
                            text = "Total training sessions",
                            fontSize = 10.sp,
                            color = MonoOnSurfaceVariant
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                    shape = RoundedCornerShape(14.dp),
                    border = subtleBorder
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "MEALS LOGGED",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoOnSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${meals.size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = MonoOnSurface
                        )
                        Text(
                            text = "Dietary tracking records",
                            fontSize = 10.sp,
                            color = MonoOnSurfaceVariant
                        )
                    }
                }
            }

            // 6. Export Data Button (Room to CSV)
            OutlinedButton(
                onClick = onExportCsv,
                border = BorderStroke(0.75.dp, MonoOutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("export_progress_csv_btn"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = MonoOnSurface,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "EXPORT ALL ROOM DATA (CSV)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonoOnSurface,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showAddWeightDialog) {
            AddWeightDialog(
                onDismiss = { showAddWeightDialog = false },
                onSave = { w ->
                    onAddWeightLog(w)
                    showAddWeightDialog = false
                }
            )
        }
    }
}

@Composable
private fun MetricSummaryCard(
    title: String,
    value: String,
    subValue: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MonoSurfaceContainerLow,
        border = BorderStroke(0.75.dp, MonoOutlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = MonoOnSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = MonoOnSurfaceVariant,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = subValue,
                fontSize = 7.5.sp,
                color = MonoOnSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
