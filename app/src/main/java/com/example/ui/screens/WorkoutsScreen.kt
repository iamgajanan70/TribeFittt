package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.data.local.WorkoutLogEntity
import com.example.ui.components.LogWorkoutDialog
import com.example.ui.components.WorkoutHistoryExpandedDialog
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerLowest
import com.example.ui.theme.MonoSurfaceVariant

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkoutsScreen(
    workouts: List<WorkoutLogEntity>,
    onLogWorkout: (title: String, duration: Int, exercises: String) -> Unit,
    onStartActiveWorkout: (title: String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var selectedHistoryWorkout by remember { mutableStateOf<WorkoutLogEntity?>(null) }

    val todayWorkout = workouts.firstOrNull { it.status == "Scheduled" } ?: workouts.firstOrNull()
    val historyWorkouts = workouts.filter { it.status == "Completed" }

    val subtleBorder = BorderStroke(0.75.dp, MonoOutlineVariant.copy(alpha = 0.45f))

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
                .testTag("workouts_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Actions Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onStartActiveWorkout(todayWorkout?.title ?: "Daily Workout Session") },
                    colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("start_workout_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Start Workout", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MonoSurfaceContainerLowest)
                        .clickable { showAddDialog = true }
                        .testTag("add_workout_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log Custom Workout",
                        tint = MonoOnSurface
                    )
                }
            }

            // Current Scheduled Program Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceContainerLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TODAY'S SCHEDULED WORKOUT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoOnSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MonoPrimary)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = todayWorkout?.status ?: "Ready",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = todayWorkout?.title ?: "Full Body Hypertrophy",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonoOnSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = MonoPrimary, modifier = Modifier.size(16.dp))
                            Text("${todayWorkout?.durationMinutes ?: 45} mins", fontSize = 12.sp, color = MonoOnSurface)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = null, tint = MonoPrimary, modifier = Modifier.size(16.dp))
                            Text("${todayWorkout?.exerciseCount ?: 6} exercises", fontSize = 12.sp, color = MonoOnSurface)
                        }
                    }

                    // Exercises Breakdown Chips
                    val exList = (todayWorkout?.exercisesText ?: "Incline Dumbbell Press, Pull-ups, Barbell Squats, Roman Chair, Cable Lateral Raise, Hanging Leg Raise")
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        exList.forEach { exercise ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MonoSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = exercise,
                                    fontSize = 11.sp,
                                    color = MonoOnSurface
                                )
                            }
                        }
                    }
                }
            }

            // Quick Split Categories Row
            Text("Workout Programs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)

            val splitTemplates = listOf(
                Triple("Push Day (Chest, Shoulders)", "45 mins • 6 exercises", Icons.Default.FitnessCenter),
                Triple("Pull Day (Back, Biceps)", "50 mins • 6 exercises", Icons.Default.DirectionsRun),
                Triple("Legs & Core Conditioning", "60 mins • 7 exercises", Icons.Default.FitnessCenter),
                Triple("High Intensity Cardio Burn", "30 mins • 5 rounds", Icons.Default.DirectionsRun)
            )

            splitTemplates.forEach { (title, subtitle, icon) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartActiveWorkout(title) },
                    colors = CardDefaults.cardColors(containerColor = MonoSurfaceContainerLowest),
                    shape = RoundedCornerShape(14.dp),
                    border = subtleBorder
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MonoSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = MonoPrimary, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                Text(subtitle, fontSize = 11.sp, color = MonoOnSurfaceVariant)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MonoOnSurfaceVariant)
                    }
                }
            }

            // Workout History List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Workout History", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                Text("${historyWorkouts.size} Completed", fontSize = 11.sp, color = MonoOnSurfaceVariant)
            }

            if (historyWorkouts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MonoSurfaceContainerLowest)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No completed workouts logged yet.\nTap 'Start Workout' above to begin!",
                        fontSize = 12.sp,
                        color = MonoOnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                historyWorkouts.take(10).forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedHistoryWorkout = item
                                showHistoryDialog = true
                            },
                        colors = CardDefaults.cardColors(containerColor = MonoSurfaceContainerLowest),
                        shape = RoundedCornerShape(14.dp),
                        border = subtleBorder
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2E7D32).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                }
                                Column {
                                    Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                    Text("${item.durationMinutes} mins • ${item.dateText}", fontSize = 10.sp, color = MonoOnSurfaceVariant)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MonoOnSurfaceVariant, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showAddDialog) {
            LogWorkoutDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, duration, exercises ->
                    onLogWorkout(title, duration, exercises)
                    showAddDialog = false
                }
            )
        }

        if (showHistoryDialog) {
            WorkoutHistoryExpandedDialog(
                workouts = historyWorkouts,
                initialSelectedWorkout = selectedHistoryWorkout,
                onDismiss = {
                    showHistoryDialog = false
                    selectedHistoryWorkout = null
                }
            )
        }
    }
}
