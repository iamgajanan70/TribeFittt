package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.MonoOnPrimary
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditSleepScheduleDialog(
    initialWakeUpTime: String,
    initialSleepTime: String,
    initialGoalHours: Float,
    initialActualHours: Float,
    onDismiss: () -> Unit,
    onSave: (wakeUpTime: String, sleepTime: String, goalHours: Float, actualHours: Float) -> Unit
) {
    var wakeUpTime by remember { mutableStateOf(initialWakeUpTime) }
    var sleepTime by remember { mutableStateOf(initialSleepTime) }
    var goalHoursText by remember { mutableStateOf(initialGoalHours.toString()) }
    var actualHoursText by remember { mutableStateOf(initialActualHours.toString()) }

    val commonWakeTimes = listOf("04:30 AM", "05:00 AM", "05:30 AM", "06:00 AM", "06:30 AM", "07:00 AM", "08:00 AM")
    val commonSleepTimes = listOf("09:30 PM", "10:00 PM", "10:30 PM", "11:00 PM", "11:30 PM", "12:00 AM")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
            border = BorderStroke(1.dp, MonoOutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("edit_sleep_schedule_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = MonoPrimary)
                        Text(
                            text = "EDIT SLEEP SCHEDULE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoOnSurface
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MonoOnSurfaceVariant)
                    }
                }

                HorizontalDivider(color = MonoOutlineVariant)

                // 1. Wake Up Time
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
                        Text("WAKE UP TIME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                    }

                    OutlinedTextField(
                        value = wakeUpTime,
                        onValueChange = { wakeUpTime = it },
                        textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        placeholder = { Text("e.g. 05:00 AM") },
                        modifier = Modifier.fillMaxWidth().testTag("wake_up_time_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MonoOnSurface,
                            unfocusedTextColor = MonoOnSurface,
                            focusedContainerColor = MonoSurfaceLowest,
                            unfocusedContainerColor = MonoSurfaceLowest,
                            focusedBorderColor = MonoPrimary
                        )
                    )

                    // Quick presets
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        commonWakeTimes.forEach { time ->
                            val isSelected = wakeUpTime.equals(time, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) MonoPrimary else MonoSurfaceContainerLow)
                                    .clickable { wakeUpTime = time }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = time,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MonoOnPrimary else MonoOnSurface
                                )
                            }
                        }
                    }
                }

                // 2. Bedtime / Sleep Time
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Bedtime, contentDescription = null, tint = Color(0xFF5C6BC0), modifier = Modifier.size(16.dp))
                        Text("BEDTIME / SLEEP TIME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                    }

                    OutlinedTextField(
                        value = sleepTime,
                        onValueChange = { sleepTime = it },
                        textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        placeholder = { Text("e.g. 10:00 PM") },
                        modifier = Modifier.fillMaxWidth().testTag("sleep_time_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MonoOnSurface,
                            unfocusedTextColor = MonoOnSurface,
                            focusedContainerColor = MonoSurfaceLowest,
                            unfocusedContainerColor = MonoSurfaceLowest,
                            focusedBorderColor = MonoPrimary
                        )
                    )

                    // Quick presets
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        commonSleepTimes.forEach { time ->
                            val isSelected = sleepTime.equals(time, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) MonoPrimary else MonoSurfaceContainerLow)
                                    .clickable { sleepTime = time }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = time,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MonoOnPrimary else MonoOnSurface
                                )
                            }
                        }
                    }
                }

                // 3. Sleep Hours Logged & Target Goal
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ACTUAL SLEEP (HRS)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                        OutlinedTextField(
                            value = actualHoursText,
                            onValueChange = { actualHoursText = it },
                            textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("actual_sleep_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MonoOnSurface,
                                unfocusedTextColor = MonoOnSurface,
                                focusedContainerColor = MonoSurfaceLowest,
                                unfocusedContainerColor = MonoSurfaceLowest,
                                focusedBorderColor = MonoPrimary
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("SLEEP GOAL (HRS)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                        OutlinedTextField(
                            value = goalHoursText,
                            onValueChange = { goalHoursText = it },
                            textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("sleep_goal_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MonoOnSurface,
                                unfocusedTextColor = MonoOnSurface,
                                focusedContainerColor = MonoSurfaceLowest,
                                unfocusedContainerColor = MonoSurfaceLowest,
                                focusedBorderColor = MonoPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Save Action Button
                Button(
                    onClick = {
                        val gHours = goalHoursText.toFloatOrNull() ?: 6.0f
                        val aHours = actualHoursText.toFloatOrNull() ?: 0.0f
                        onSave(wakeUpTime.ifBlank { "05:00 AM" }, sleepTime.ifBlank { "10:00 PM" }, gHours, aHours)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("save_sleep_schedule_btn")
                ) {
                    Text("SAVE SLEEP SCHEDULE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnPrimary)
                }
            }
        }
    }
}
