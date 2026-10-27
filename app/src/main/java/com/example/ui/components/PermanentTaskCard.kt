package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DailyTaskRecordEntity
import com.example.data.model.PermanentTask
import com.example.data.model.TaskType
import com.example.data.model.getIcon
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnPrimary
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerHigh
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PermanentTaskCard(
    task: PermanentTask,
    record: DailyTaskRecordEntity,
    onAddSteps: (Int) -> Unit = {},
    onAddWater: (Float) -> Unit = {},
    onToggleNoJunkFood: () -> Unit = {},
    onToggleProteinMeal: (String) -> Unit = {},
    onLogSleep: (Float) -> Unit = {},
    onToggleWorkout: () -> Unit = {},
    onAddStudyMinutes: (Int) -> Unit = {},
    isStudyTimerRunning: Boolean = false,
    studyTimerSeconds: Int = 0,
    onStartStudyTimer: () -> Unit = {},
    onPauseStudyTimer: () -> Unit = {},
    onSaveStudyTimer: () -> Unit = {},
    onAddReadingPages: (Int) -> Unit = {},
    onLogBedtime: (String) -> Unit = {},
    onLogWakeUpTime: (String) -> Unit = {},
    onOpenLiveWorkout: () -> Unit = {},
    isDarkMode: Boolean = false
) {
    val isCompleted = record.isCompleted
    val progressRatio = (record.completionPercentage / 100f).coerceIn(0f, 1f)

    val animatedRatio by animateFloatAsState(
        targetValue = progressRatio,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "task_progress_${task.id}"
    )

    val subtleBorder = BorderStroke(
        width = if (isCompleted) 1.25.dp else 0.75.dp,
        color = if (isCompleted) Color(0xFF2E7D32).copy(alpha = 0.6f) else MonoOutlineVariant.copy(alpha = 0.45f)
    )

    var showTimeDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
        border = subtleBorder
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Icon + Title + Category + Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (isCompleted) Color(0xFF2E7D32).copy(alpha = 0.15f) else MonoSurfaceContainerLow,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                0.75.dp,
                                if (isCompleted) Color(0xFF2E7D32) else MonoOutlineVariant,
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = task.getIcon(),
                            contentDescription = task.title,
                            tint = if (isCompleted) Color(0xFF2E7D32) else MonoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${task.orderIndex}. ${task.title}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MonoOnSurface
                            )
                        }
                        Text(
                            text = "${task.category} • ${task.description}",
                            fontSize = 10.sp,
                            color = MonoOnSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                // Completion Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCompleted) Color(0xFF2E7D32) else MonoSurfaceContainerLow,
                    border = BorderStroke(0.5.dp, if (isCompleted) Color(0xFF2E7D32) else MonoOutlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        if (isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                        }
                        Text(
                            text = if (isCompleted) "GOAL MET" else "${record.completionPercentage.toInt()}%",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isCompleted) Color.White else MonoOnSurfaceVariant
                        )
                    }
                }
            }

            // Current Value Display & Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.valueDisplay.ifBlank { "0 / ${task.targetValue.toInt()} ${task.unit}" },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCompleted) Color(0xFF2E7D32) else MonoOnSurface
                )
                Text(
                    text = "Target: ${if (task.taskType == TaskType.BEDTIME) "< 10:00 PM" else if (task.taskType == TaskType.WAKEUP) "< 4:30 AM" else "${task.targetValue.toInt()} ${task.unit}"}",
                    fontSize = 10.5.sp,
                    color = MonoOnSurfaceVariant
                )
            }

            // Progress Bar (Linear)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MonoSurfaceContainerHigh)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedRatio)
                        .height(6.dp)
                        .background(if (isCompleted) Color(0xFF2E7D32) else MonoPrimary)
                )
            }

            // Interactive Controls Specific to Each Permanent Task
            when (task.taskType) {
                TaskType.STEPS -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onAddSteps(500) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Text("+500", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }
                        OutlinedButton(
                            onClick = { onAddSteps(1000) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Text("+1,000", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }
                        OutlinedButton(
                            onClick = { onAddSteps(2500) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Text("+2,500", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }
                    }
                }

                TaskType.WATER -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onAddWater(0.25f) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Text("+250ml", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                        }
                        OutlinedButton(
                            onClick = { onAddWater(0.5f) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Text("+500ml", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                        }
                        OutlinedButton(
                            onClick = { onAddWater(1.0f) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Text("+1.0L", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                        }
                    }
                }

                TaskType.NO_JUNK_FOOD -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MonoSurfaceContainerLow)
                            .clickable { onToggleNoJunkFood() }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Checkbox(
                                checked = isCompleted,
                                onCheckedChange = { onToggleNoJunkFood() },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF2E7D32),
                                    checkmarkColor = Color.White
                                )
                            )
                            Text(
                                text = "Zero Junk Food Eaten Today",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MonoOnSurface
                            )
                        }
                        Switch(
                            checked = isCompleted,
                            onCheckedChange = { onToggleNoJunkFood() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2E7D32)
                            )
                        )
                    }
                }

                TaskType.PROTEIN_MEALS -> {
                    val activeMeals = record.extraData.split(",").map { it.trim() }.toSet()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Breakfast", "Lunch", "Dinner").forEach { meal ->
                            val hasProtein = activeMeals.contains(meal)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onToggleProteinMeal(meal) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (hasProtein) Color(0xFF2E7D32).copy(alpha = 0.15f) else MonoSurfaceContainerLow,
                                border = BorderStroke(
                                    0.75.dp,
                                    if (hasProtein) Color(0xFF2E7D32) else MonoOutlineVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (hasProtein) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (hasProtein) Color(0xFF2E7D32) else MonoOnSurfaceVariant,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = meal,
                                        fontSize = 11.sp,
                                        fontWeight = if (hasProtein) FontWeight.Bold else FontWeight.Normal,
                                        color = if (hasProtein) Color(0xFF2E7D32) else MonoOnSurface
                                    )
                                }
                            }
                        }
                    }
                }

                TaskType.SLEEP -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(6.0f, 7.0f, 8.0f).forEach { hours ->
                            OutlinedButton(
                                onClick = { onLogSleep(hours) },
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.75.dp, MonoOutlineVariant)
                            ) {
                                Text("${hours.toInt()} hrs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            }
                        }
                        OutlinedButton(
                            onClick = { onLogSleep(record.actualValue + 0.5f) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Text("+30m", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }
                    }
                }

                TaskType.WORKOUT -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = onToggleWorkout,
                            modifier = Modifier.weight(1f).height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) Color(0xFF2E7D32) else MonoPrimary
                            )
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.Check else Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MonoOnPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCompleted) "Completed ✓" else "Mark Done",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MonoOnPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = onOpenLiveWorkout,
                            modifier = Modifier.weight(1f).height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MonoOnSurface, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Live Session", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }
                    }
                }

                TaskType.STUDY -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Study Timer controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MonoSurfaceContainerLow)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = MonoPrimary, modifier = Modifier.size(16.dp))
                                val timerMins = studyTimerSeconds / 60
                                val timerSecs = studyTimerSeconds % 60
                                Text(
                                    text = String.format("%02d:%02d", timerMins, timerSecs),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MonoOnSurface
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (!isStudyTimerRunning) {
                                    Button(
                                        onClick = onStartStudyTimer,
                                        modifier = Modifier.height(28.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MonoOnPrimary, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Start", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnPrimary)
                                    }
                                } else {
                                    Button(
                                        onClick = onPauseStudyTimer,
                                        modifier = Modifier.height(28.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                                    ) {
                                        Icon(Icons.Default.Pause, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Pause", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                if (studyTimerSeconds > 0) {
                                    OutlinedButton(
                                        onClick = onSaveStudyTimer,
                                        modifier = Modifier.height(28.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.75.dp, MonoOutlineVariant)
                                    ) {
                                        Text("Save", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                    }
                                }
                            }
                        }

                        // Quick Add Minutes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onAddStudyMinutes(15) },
                                modifier = Modifier.weight(1f).height(30.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.75.dp, MonoOutlineVariant)
                            ) {
                                Text("+15m", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            }
                            OutlinedButton(
                                onClick = { onAddStudyMinutes(30) },
                                modifier = Modifier.weight(1f).height(30.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.75.dp, MonoOutlineVariant)
                            ) {
                                Text("+30m", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            }
                            OutlinedButton(
                                onClick = { onAddStudyMinutes(60) },
                                modifier = Modifier.weight(1f).height(30.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.75.dp, MonoOutlineVariant)
                            ) {
                                Text("+1h", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            }
                        }
                    }
                }

                TaskType.READING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onAddReadingPages(1) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Text("+1 Page", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }
                        OutlinedButton(
                            onClick = { onAddReadingPages(5) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Text("+5 Pages", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }
                        OutlinedButton(
                            onClick = { onAddReadingPages(10) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Text("+10 Pages", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }
                    }
                }

                TaskType.BEDTIME -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("09:15 PM", "09:30 PM", "09:45 PM", "10:00 PM").forEach { time ->
                            OutlinedButton(
                                onClick = { onLogBedtime(time) },
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.75.dp, MonoOutlineVariant)
                            ) {
                                Text(time.replace(" PM", ""), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            }
                        }
                    }
                }

                TaskType.WAKEUP -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("04:00 AM", "04:15 AM", "04:30 AM", "04:45 AM").forEach { time ->
                            val isGoalMet = time != "04:45 AM"
                            OutlinedButton(
                                onClick = { onLogWakeUpTime(time) },
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.75.dp, if (isGoalMet) MonoOutlineVariant else Color(0xFFD32F2F).copy(alpha = 0.5f))
                            ) {
                                Text(time.replace(" AM", ""), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isGoalMet) MonoOnSurface else Color(0xFFD32F2F))
                            }
                        }
                    }
                }
            }
        }
    }
}
