package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerHigh
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest
import kotlinx.coroutines.delay

@Composable
fun LiveWorkoutModal(
    workoutTitle: String,
    onFinishWorkout: (durationMinutes: Int, exercisesText: String) -> Unit,
    onDismiss: () -> Unit
) {
    var elapsedSeconds by remember { mutableStateOf(142) } // Starts at 2m 22s for instant immersion
    var isTimerRunning by remember { mutableStateOf(true) }
    var showCompletedFullSummary by remember { mutableStateOf(false) }

    var set1Completed by remember { mutableStateOf(true) }
    var set2Completed by remember { mutableStateOf(true) }
    var set3Completed by remember { mutableStateOf(true) }
    var set4Completed by remember { mutableStateOf(true) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000)
            elapsedSeconds++
        }
    }

    val minutes = (elapsedSeconds / 60).coerceAtLeast(1)
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

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
                .testTag("live_workout_modal"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (showCompletedFullSummary) Color(0xFF2E7D32) else MonoPrimary)
                    )
                    Text(
                        text = if (showCompletedFullSummary) "SESSION DELIVERED" else "LIVE SESSION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (showCompletedFullSummary) Color(0xFF2E7D32) else MonoPrimary,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MonoOnSurface)
                }
            }

            if (showCompletedFullSummary) {
                // FULL VIEW OF WORKOUT COMPLETED IN THIS SESSION
                Card(
                    colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(28.dp))
                        }

                        Text("WORKOUT COMPLETED! 🎉", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        Text(workoutTitle, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MonoOnSurfaceVariant)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MonoSurfaceContainerLow)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DURATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                                Text("$minutes min", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("EST. CALORIES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                                Text("${minutes * 8} kcal", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MonoPrimary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TOTAL VOLUME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                                Text("3,120 kg", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            }
                        }
                    }
                }

                Text("FULL VIEW OF COMPLETED EXERCISES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant, letterSpacing = 0.5.sp)

                Card(
                    colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MonoOutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Exercise 1 Detail
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("1. Barbell Bench Press", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                Text("4 Sets Completed", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                            Text("Set 1: 100 kg x 10 reps • Set 2: 100 kg x 8 reps • Set 3: 105 kg x 6 reps • Set 4: 105 kg x 6 reps", fontSize = 12.sp, color = MonoOnSurfaceVariant)
                        }

                        HorizontalDivider(color = MonoOutlineVariant)

                        // Exercise 2 Detail
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("2. Overhead Barbell Press", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                Text("3 Sets Completed", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                            Text("Set 1: 60 kg x 10 reps • Set 2: 60 kg x 8 reps • Set 3: 65 kg x 6 reps", fontSize = 12.sp, color = MonoOnSurfaceVariant)
                        }

                        HorizontalDivider(color = MonoOutlineVariant)

                        // Exercise 3 Detail
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("3. Incline Dumbbell Bench Press", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                Text("3 Sets Completed", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                            Text("Set 1: 30 kg x 12 reps • Set 2: 32 kg x 10 reps • Set 3: 32 kg x 8 reps", fontSize = 12.sp, color = MonoOnSurfaceVariant)
                        }

                        HorizontalDivider(color = MonoOutlineVariant)

                        // Exercise 4 Detail
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("4. Tricep Dips & Cable Pushdowns", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                Text("3 Sets Completed", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                            Text("Set 1: Bodyweight x 15 reps • Set 2: 25 kg x 12 reps • Set 3: 30 kg x 10 reps", fontSize = 12.sp, color = MonoOnSurfaceVariant)
                        }
                    }
                }

                Button(
                    onClick = {
                        onFinishWorkout(minutes, "Bench Press, Overhead Press, Incline Dumbbell, Tricep Dips")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_and_exit_workout_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("SAVE SESSION TO HISTORY & EXIT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 0.5.sp)
                }
            } else {
                // Title & Timer Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MonoOutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(workoutTitle, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)

                        Text(
                            text = timeFormatted,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MonoPrimary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { isTimerRunning = !isTimerRunning },
                                border = androidx.compose.foundation.BorderStroke(1.dp, MonoOutlineVariant)
                            ) {
                                Icon(
                                    imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = MonoOnSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isTimerRunning) "PAUSE" else "RESUME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            }
                        }
                    }
                }

                // Current Exercise Logger
                Text("EXERCISE 1 OF 4", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant, letterSpacing = 0.5.sp)

                Card(
                    colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MonoOutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Barbell Bench Press", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        HorizontalDivider(color = MonoOutlineVariant)

                        // Sets Row
                        @Composable
                        fun SetRow(setNum: Int, weight: String, reps: String, isDone: Boolean, onToggle: () -> Unit) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("SET $setNum", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                                Text("$weight kg x $reps reps", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isDone) MonoPrimary else MonoSurfaceContainerLow)
                                        .border(1.dp, MonoOutlineVariant, CircleShape)
                                        .clickable { onToggle() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = if (isDone) Color.White else MonoOnSurfaceVariant, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        SetRow(1, "100", "10", set1Completed) { set1Completed = !set1Completed }
                        HorizontalDivider(color = MonoOutlineVariant)
                        SetRow(2, "100", "8", set2Completed) { set2Completed = !set2Completed }
                        HorizontalDivider(color = MonoOutlineVariant)
                        SetRow(3, "105", "6", set3Completed) { set3Completed = !set3Completed }
                        HorizontalDivider(color = MonoOutlineVariant)
                        SetRow(4, "105", "6", set4Completed) { set4Completed = !set4Completed }
                    }
                }

                // Finish Workout Button
                Button(
                    onClick = {
                        isTimerRunning = false
                        showCompletedFullSummary = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("finish_workout_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("FINISH WORKOUT", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
