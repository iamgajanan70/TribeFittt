package com.example.ui.components

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.WorkoutLogEntity
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerHigh
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkoutHistoryExpandedDialog(
    workouts: List<WorkoutLogEntity>,
    initialSelectedWorkout: WorkoutLogEntity? = null,
    onDismiss: () -> Unit
) {
    var expandedWorkoutId by remember { mutableStateOf<Long?>(initialSelectedWorkout?.id ?: workouts.firstOrNull()?.id) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MonoBackground)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .testTag("workout_history_dialog"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WORKOUT HISTORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonoOnSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${workouts.size} Completed Sessions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonoOnSurface
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MonoOnSurface)
                }
            }

            if (workouts.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MonoOutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = MonoOnSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "No Workout History Yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoOnSurface
                        )
                        Text(
                            text = "Start a Live Session or log a workout to build your history.",
                            fontSize = 12.sp,
                            color = MonoOnSurfaceVariant
                        )
                    }
                }
            } else {
                workouts.forEach { item ->
                    val isExpanded = expandedWorkoutId == item.id
                    val exercisesList = item.exercisesText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    val estCalories = item.durationMinutes * 8

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isExpanded) MonoPrimary else MonoOutlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedWorkoutId = if (isExpanded) null else item.id
                            }
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MonoSurfaceContainerHigh)
                                            .border(1.dp, MonoOutlineVariant, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MonoPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = item.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MonoOnSurface
                                        )
                                        Text(
                                            text = "${item.dateText} • ${item.durationMinutes} minutes",
                                            fontSize = 12.sp,
                                            color = MonoOnSurfaceVariant
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFE8F5E9))
                                        .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(16.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "COMPLETED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }

                            // Quick Stats Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MonoSurfaceContainerLow)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = MonoOnSurfaceVariant, modifier = Modifier.size(14.dp))
                                    Text("${item.durationMinutes} min", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MonoOnSurfaceVariant, modifier = Modifier.size(14.dp))
                                    Text("${item.exerciseCount} exercises", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = MonoPrimary, modifier = Modifier.size(14.dp))
                                    Text("~$estCalories kcal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                }
                            }

                            // Expanded Exercise Breakdown
                            if (isExpanded) {
                                HorizontalDivider(color = MonoOutlineVariant)

                                Text(
                                    text = "COMPLETED EXERCISES BREAKDOWN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MonoOnSurfaceVariant,
                                    letterSpacing = 0.5.sp
                                )

                                if (exercisesList.isNotEmpty()) {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        exercisesList.forEachIndexed { idx, ex ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .border(1.dp, MonoOutlineVariant, RoundedCornerShape(8.dp))
                                                    .background(MonoSurfaceContainerHigh)
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = "${idx + 1}. $ex",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MonoOnSurface
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Full workout recorded.",
                                        fontSize = 12.sp,
                                        color = MonoOnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, MonoOutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CLOSE HISTORY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
