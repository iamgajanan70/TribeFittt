package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.local.DailyTaskRecordEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.NutritionSummary
import com.example.data.model.PermanentTask
import com.example.ui.components.HabitContributionGraph
import com.example.ui.components.PermanentTaskCard
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
fun DashboardScreen(
    userProfile: UserProfileEntity?,
    nutritionSummary: NutritionSummary,
    selectedDate: String,
    dayTaskRecords: List<DailyTaskRecordEntity>,
    allDailyTaskRecords: List<DailyTaskRecordEntity>,
    onSelectDate: (String) -> Unit,
    onGoToToday: () -> Unit,
    onGoToYesterday: () -> Unit,
    onShiftDate: (Int) -> Unit,
    // Task update actions
    onAddSteps: (Int) -> Unit,
    onAddWater: (Float) -> Unit,
    onToggleNoJunkFood: () -> Unit,
    onToggleProteinMeal: (String) -> Unit,
    onLogSleep: (Float) -> Unit,
    onToggleWorkout: () -> Unit,
    onAddStudyMinutes: (Int) -> Unit,
    isStudyTimerRunning: Boolean,
    studyTimerSeconds: Int,
    onStartStudyTimer: () -> Unit,
    onPauseStudyTimer: () -> Unit,
    onSaveStudyTimer: () -> Unit,
    onAddReadingPages: (Int) -> Unit,
    onLogBedtime: (String) -> Unit,
    onLogWakeUpTime: (String) -> Unit,
    onOpenLiveWorkout: (String) -> Unit,
    onTriggerNotification: () -> Unit,
    onTriggerIncompleteHabits2x: () -> Unit,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToNutrition: () -> Unit,
    onNavigateToSettings: () -> Unit,
    isDarkMode: Boolean = false
) {
    val todayStr = remember { DateUtils.getTodayDateString() }
    val isViewingToday = selectedDate == todayStr

    val completedCount = dayTaskRecords.count { it.isCompleted }
    val progressPercentage = (completedCount * 10).coerceIn(0, 100)
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercentage / 100f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "daily_overall_progress"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MonoBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Date Navigation Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("date_navigation_bar"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                border = BorderStroke(0.75.dp, MonoOutlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onShiftDate(-1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Day",
                                tint = MonoOnSurface
                            )
                        }

                        Column(modifier = Modifier.padding(horizontal = 6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isViewingToday) "TODAY" else DateUtils.formatDateForDisplay(selectedDate).uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MonoOnSurface,
                                    fontSize = 13.sp
                                )
                                if (isViewingToday) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(Color(0xFF2E7D32), CircleShape)
                                    )
                                }
                            }
                            Text(
                                text = DateUtils.getDayOfWeek(selectedDate).uppercase(),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.5.sp,
                                color = MonoOnSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { onShiftDate(1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Day",
                                tint = MonoOnSurface
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (!isViewingToday) {
                            OutlinedButton(
                                onClick = onGoToToday,
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(0.75.dp, MonoPrimary)
                            ) {
                                Text(
                                    text = "TODAY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MonoPrimary
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onGoToYesterday,
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.75.dp, MonoOutlineVariant)
                        ) {
                            Text(
                                text = "YESTERDAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MonoOnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 2. Daily 10 Goals Overview Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_goals_overview_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (completedCount == 10) Color(0xFF1B5E20).copy(alpha = 0.15f) else MonoSurfaceLowest
                ),
                border = BorderStroke(
                    1.dp,
                    if (completedCount == 10) Color(0xFF2E7D32) else MonoOutlineVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Permanent System Goals",
                                    tint = MonoPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "10 PERMANENT GOALS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = MonoPrimary,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (completedCount == 10) "10/10 PERFECT DAY CRUSHED!" else "$completedCount of 10 Goals Met",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = if (completedCount == 10) Color(0xFF2E7D32) else MonoOnSurface,
                                fontSize = 20.sp
                            )
                        }

                        // Circular Progress Indicator
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.size(54.dp),
                                color = if (completedCount == 10) Color(0xFF2E7D32) else MonoPrimary,
                                trackColor = MonoSurfaceContainerHigh,
                                strokeWidth = 5.dp
                            )
                            Text(
                                text = "$progressPercentage%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = MonoOnSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MonoSurfaceContainerHigh)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(8.dp)
                                .background(if (completedCount == 10) Color(0xFF2E7D32) else MonoPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reminder actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (completedCount == 10) "Unstoppable execution. Zero compromises." else "${10 - completedCount} goals remaining today",
                            style = MaterialTheme.typography.bodySmall,
                            color = MonoOnSurfaceVariant,
                            fontSize = 11.5.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = onTriggerNotification,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Test Notification",
                                    tint = MonoOnSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = onTriggerIncompleteHabits2x,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "2X Urgent Alert",
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Links (Workouts & Nutrition logs)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToWorkouts() },
                    shape = RoundedCornerShape(12.dp),
                    color = MonoSurfaceLowest,
                    border = BorderStroke(0.75.dp, MonoOutlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MonoPrimary, modifier = Modifier.size(20.dp))
                        Column {
                            Text("WORKOUT LOG", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            Text("Split & Routines", fontSize = 9.5.sp, color = MonoOnSurfaceVariant)
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToNutrition() },
                    shape = RoundedCornerShape(12.dp),
                    color = MonoSurfaceLowest,
                    border = BorderStroke(0.75.dp, MonoOutlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = MonoPrimary, modifier = Modifier.size(20.dp))
                        Column {
                            Text("MEAL TRACKER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            Text("${nutritionSummary.totalCalories} kcal logged", fontSize = 9.5.sp, color = MonoOnSurfaceVariant)
                        }
                    }
                }
            }
        }

        // 4. Header for 10 Permanent Tasks List
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAILY GOAL PROTOCOLS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = MonoOnSurface
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MonoSurfaceContainerHigh
                ) {
                    Text(
                        text = "LOCKED PROTOCOL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonoOnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // 5. The 10 Permanent Tasks (Fixed System Order)
        items(
            items = PermanentTask.ALL_PERMANENT_TASKS,
            key = { it.id }
        ) { task ->
            val record = dayTaskRecords.find { it.taskId == task.id } ?: DailyTaskRecordEntity(
                taskId = task.id,
                dateText = selectedDate,
                targetValue = task.targetValue
            )

            PermanentTaskCard(
                task = task,
                record = record,
                onAddSteps = { onAddSteps(it) },
                onAddWater = { onAddWater(it) },
                onToggleNoJunkFood = { onToggleNoJunkFood() },
                onToggleProteinMeal = { onToggleProteinMeal(it) },
                onLogSleep = { onLogSleep(it) },
                onToggleWorkout = { onToggleWorkout() },
                onAddStudyMinutes = { onAddStudyMinutes(it) },
                isStudyTimerRunning = isStudyTimerRunning,
                studyTimerSeconds = studyTimerSeconds,
                onStartStudyTimer = onStartStudyTimer,
                onPauseStudyTimer = onPauseStudyTimer,
                onSaveStudyTimer = onSaveStudyTimer,
                onAddReadingPages = { onAddReadingPages(it) },
                onLogBedtime = { onLogBedtime(it) },
                onLogWakeUpTime = { onLogWakeUpTime(it) },
                onOpenLiveWorkout = { onOpenLiveWorkout("Full Body Workout") },
                isDarkMode = isDarkMode
            )
        }

        // 6. 16-Week Contribution Matrix
        item {
            Spacer(modifier = Modifier.height(10.dp))
            HabitContributionGraph(
                allDailyTaskRecords = allDailyTaskRecords,
                selectedDate = selectedDate,
                onSelectDate = onSelectDate,
                isDarkMode = isDarkMode
            )
        }
    }
}
