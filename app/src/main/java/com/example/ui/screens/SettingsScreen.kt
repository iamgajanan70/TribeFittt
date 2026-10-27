package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfileEntity
import com.example.ui.components.UserAvatar
import com.example.ui.components.VisibleSwitch
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnPrimary
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    userProfile: UserProfileEntity?,
    onSaveGoals: (
        stepGoal: Int,
        waterGoalLiters: Float,
        calorieGoal: Int,
        calorieBurnGoal: Int,
        sleepHoursGoal: Float,
        dailyReadingPagesGoal: Int
    ) -> Unit,
    onSaveSleepSchedule: (
        wakeUpTime: String,
        sleepTime: String,
        sleepHoursGoal: Float,
        actualSleepHours: Float
    ) -> Unit = { _, _, _, _ -> },
    onUploadAvatar: (Bitmap) -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onTriggerTestNotification: () -> Unit,
    onTriggerTestWaterReminder: () -> Unit = {},
    onTriggerTestHabitReminder: (isSecondReminder: Boolean) -> Unit = {},
    onTriggerIncompleteHabits2x: () -> Unit = {},
    onToggleWaterReminders: () -> Unit = {},
    onSetWaterInterval: (Int) -> Unit = {},
    onToggleFocusMode: () -> Unit,
    onToggleDarkMode: () -> Unit = {},
    onLogOut: () -> Unit,
    onResetAllData: () -> Unit = {},
    onExportCsv: () -> Unit = {}
) {
    val context = LocalContext.current

    // Confirmation Dialog States
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showResetDataDialog by remember { mutableStateOf(false) }

    // Goal State
    var stepGoalText by remember(userProfile) { mutableStateOf((userProfile?.stepGoal ?: 10000).toString()) }
    var waterGoalText by remember(userProfile) { mutableStateOf((userProfile?.waterGoalLiters ?: 3.0f).toString()) }
    var calorieGoalText by remember(userProfile) { mutableStateOf((userProfile?.calorieGoal ?: 2000).toString()) }
    var calBurnGoalText by remember(userProfile) { mutableStateOf((userProfile?.calorieBurnGoal ?: 500).toString()) }
    var readingGoalText by remember(userProfile) { mutableStateOf((userProfile?.dailyReadingPagesGoal ?: 10).toString()) }

    // Sleep Schedule State
    var wakeUpTime by remember(userProfile) { mutableStateOf(userProfile?.wakeUpTime ?: "04:30 AM") }
    var sleepTime by remember(userProfile) { mutableStateOf(userProfile?.sleepTime ?: "10:00 PM") }
    var sleepGoalText by remember(userProfile) { mutableStateOf((userProfile?.sleepHoursGoal ?: 6.0f).toString()) }
    var actualSleepText by remember(userProfile) { mutableStateOf((userProfile?.actualSleepHours ?: 0.0f).toString()) }

    val waterRemindersActive = userProfile?.waterRemindersEnabled ?: true
    val currentIntervalHours = userProfile?.waterReminderIntervalHours ?: 2

    val commonWakeTimes = listOf("04:30 AM", "05:00 AM", "05:30 AM", "06:00 AM", "06:30 AM", "07:00 AM")
    val commonSleepTimes = listOf("09:30 PM", "10:00 PM", "10:30 PM", "11:00 PM", "11:30 PM", "12:00 AM")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                bitmap?.let { b -> onUploadAvatar(b) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
                .testTag("settings_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile & Avatar Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            UserAvatar(
                                avatarUrl = userProfile?.avatarUrl,
                                fullName = userProfile?.fullName ?: "User",
                                size = 56.dp,
                                onClick = { photoPickerLauncher.launch("image/*") }
                            )
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(MonoPrimary)
                                    .clickable { photoPickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "Upload Avatar",
                                    tint = MonoOnPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (userProfile?.fullName.isNullOrBlank()) "TribeFit Member" else userProfile!!.fullName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MonoOnSurface
                            )
                            Text(
                                text = if (userProfile?.email.isNullOrBlank()) "Local Profile" else userProfile!!.email,
                                fontSize = 12.sp,
                                color = MonoOnSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onNavigateToEditProfile,
                        border = BorderStroke(0.75.dp, MonoOutlineVariant),
                        modifier = Modifier.height(34.dp).testTag("edit_profile_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MonoOnSurface, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }
                }
            }

            // Continuous Background Tracking Indicator
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = MonoPrimary)
                        Text("CONTINUOUS BACKGROUND TRACKING", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }
                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))
                    Text(
                        text = "🟢 Active Foreground Service: TribeFit tracks physical step motion, hardware sensors, and mobile movements in real time even when closed, matching Google Health & Wellbeing.",
                        fontSize = 12.sp,
                        color = MonoOnSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
            }

            // Sleep & Wake Times Schedule Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier.fillMaxWidth().testTag("settings_sleep_schedule_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = MonoPrimary)
                        Text("SLEEP & WAKE SCHEDULE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }

                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))

                    // Wake Up Time Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
                            Text("MORNING WAKE UP TIME (Selected: $wakeUpTime)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            commonWakeTimes.forEach { time ->
                                val isSelected = wakeUpTime == time
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) MonoPrimary else MonoSurfaceContainerLow)
                                        .clickable { wakeUpTime = time }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = time,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MonoOnPrimary else MonoOnSurface
                                    )
                                }
                            }
                        }
                    }

                    // Sleep Time Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Bedtime, contentDescription = null, tint = Color(0xFF5C6BC0), modifier = Modifier.size(16.dp))
                            Text("BEDTIME SCHEDULE (Selected: $sleepTime)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            commonSleepTimes.forEach { time ->
                                val isSelected = sleepTime == time
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) MonoPrimary else MonoSurfaceContainerLow)
                                        .clickable { sleepTime = time }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = time,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MonoOnPrimary else MonoOnSurface
                                    )
                                }
                            }
                        }
                    }

                    // Actual Sleep Logged vs Goal Hours
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ACTUAL SLEEP (HRS)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            OutlinedTextField(
                                value = actualSleepText,
                                onValueChange = { actualSleepText = it },
                                textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 13.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MonoOnSurface,
                                    unfocusedTextColor = MonoOnSurface,
                                    focusedContainerColor = MonoSurfaceLowest,
                                    unfocusedContainerColor = MonoSurfaceLowest,
                                    focusedBorderColor = MonoPrimary,
                                    unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("SLEEP GOAL (HRS)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            OutlinedTextField(
                                value = sleepGoalText,
                                onValueChange = { sleepGoalText = it },
                                textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 13.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MonoOnSurface,
                                    unfocusedTextColor = MonoOnSurface,
                                    focusedContainerColor = MonoSurfaceLowest,
                                    unfocusedContainerColor = MonoSurfaceLowest,
                                    focusedBorderColor = MonoPrimary,
                                    unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val g = sleepGoalText.toFloatOrNull() ?: 6.0f
                            val a = actualSleepText.toFloatOrNull() ?: 0.0f
                            onSaveSleepSchedule(wakeUpTime, sleepTime, g, a)
                            Toast.makeText(context, "Sleep Schedule Saved!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("save_sleep_schedule_settings_btn")
                    ) {
                        Text("SAVE SLEEP & WAKE SCHEDULE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnPrimary)
                    }
                }
            }

            // 2-Hour Drinking Water Reminders Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF0288D1))
                            Column {
                                Text("2-HOUR DRINKING WATER REMINDER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                Text("Periodic hydration alerts throughout the day", fontSize = 10.sp, color = MonoOnSurfaceVariant)
                            }
                        }
                        VisibleSwitch(
                            checked = waterRemindersActive,
                            onCheckedChange = { onToggleWaterReminders() }
                        )
                    }

                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))

                    Text("REMINDER FREQUENCY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 3, 4).forEach { hours ->
                            val isSelected = currentIntervalHours == hours
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MonoPrimary else MonoSurfaceContainerLow)
                                    .clickable { onSetWaterInterval(hours) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (hours == 1) "1 Hour" else "$hours Hours",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MonoOnPrimary else MonoOnSurface
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { onTriggerTestWaterReminder() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        modifier = Modifier.fillMaxWidth().testTag("trigger_water_notification_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TEST 2-HOUR WATER REMINDER NOW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Habit Notifications (2X Rule)
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MonoPrimary)
                        Column {
                            Text("HABIT NOTIFICATIONS (2X RULE)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            Text("Pending habits trigger double reminders daily", fontSize = 10.sp, color = MonoOnSurfaceVariant)
                        }
                    }

                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onTriggerTestHabitReminder(false) },
                            border = BorderStroke(0.75.dp, MonoOutlineVariant),
                            modifier = Modifier.weight(1f).testTag("trigger_habit_notification_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("1ST REMINDER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }

                        Button(
                            onClick = { onTriggerTestHabitReminder(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                            modifier = Modifier.weight(1f).testTag("trigger_2x_urgent_habit_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("2X URGENT ALERT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Button(
                        onClick = { onTriggerIncompleteHabits2x() },
                        colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("trigger_all_incomplete_2x_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MonoOnPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TRIGGER 2X DOUBLE ALERT FOR PENDING TASKS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnPrimary)
                    }
                }
            }

            // Theme & Appearance
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = MonoPrimary)
                        Text("THEME & APPEARANCE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }

                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Dark Theme Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            Text("High contrast AMOLED dark palette", fontSize = 10.sp, color = MonoOnSurfaceVariant)
                        }
                        VisibleSwitch(
                            checked = userProfile?.isDarkMode == true,
                            onCheckedChange = { onToggleDarkMode() }
                        )
                    }
                }
            }

            // Daily Goals Configuration Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = MonoPrimary)
                        Text("DAILY TARGETS CONFIGURATION", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }

                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("STEPS TARGET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            OutlinedTextField(
                                value = stepGoalText,
                                onValueChange = { stepGoalText = it },
                                textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 13.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MonoOnSurface,
                                    unfocusedTextColor = MonoOnSurface,
                                    focusedContainerColor = MonoSurfaceLowest,
                                    unfocusedContainerColor = MonoSurfaceLowest,
                                    focusedBorderColor = MonoPrimary,
                                    unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("WATER GOAL (L)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            OutlinedTextField(
                                value = waterGoalText,
                                onValueChange = { waterGoalText = it },
                                textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 13.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MonoOnSurface,
                                    unfocusedTextColor = MonoOnSurface,
                                    focusedContainerColor = MonoSurfaceLowest,
                                    unfocusedContainerColor = MonoSurfaceLowest,
                                    focusedBorderColor = MonoPrimary,
                                    unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CALORIES IN (KCAL)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            OutlinedTextField(
                                value = calorieGoalText,
                                onValueChange = { calorieGoalText = it },
                                textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 13.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MonoOnSurface,
                                    unfocusedTextColor = MonoOnSurface,
                                    focusedContainerColor = MonoSurfaceLowest,
                                    unfocusedContainerColor = MonoSurfaceLowest,
                                    focusedBorderColor = MonoPrimary,
                                    unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("BURN TARGET (KCAL)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            OutlinedTextField(
                                value = calBurnGoalText,
                                onValueChange = { calBurnGoalText = it },
                                textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 13.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MonoOnSurface,
                                    unfocusedTextColor = MonoOnSurface,
                                    focusedContainerColor = MonoSurfaceLowest,
                                    unfocusedContainerColor = MonoSurfaceLowest,
                                    focusedBorderColor = MonoPrimary,
                                    unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val st = stepGoalText.toIntOrNull() ?: 10000
                            val w = waterGoalText.toFloatOrNull() ?: 3.0f
                            val c = calorieGoalText.toIntOrNull() ?: 2000
                            val cb = calBurnGoalText.toIntOrNull() ?: 500
                            val s = sleepGoalText.toFloatOrNull() ?: 6.0f
                            val r = readingGoalText.toIntOrNull() ?: 10
                            onSaveGoals(st, w, c, cb, s, r)
                            Toast.makeText(context, "Goals updated successfully!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("save_goals_settings_btn")
                    ) {
                        Text("SAVE ALL TARGETS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnPrimary)
                    }
                }
            }

            // Data Backup & Account Management (Logout & Reset Buttons)
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("DATA BACKUP & ACCOUNT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))

                    // 1. Export CSV
                    OutlinedButton(
                        onClick = onExportCsv,
                        border = BorderStroke(0.75.dp, MonoOutlineVariant),
                        modifier = Modifier.fillMaxWidth().testTag("export_csv_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = MonoOnSurface, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EXPORT LOCAL DATA TO CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }

                    // 2. Log Out Button (Triggers Confirmation Dialog)
                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        border = BorderStroke(1.dp, Color(0xFF757575)),
                        modifier = Modifier.fillMaxWidth().testTag("logout_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MonoOnSurface, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LOG OUT OF ACCOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }

                    // 3. Reset All Local Data Button (Triggers Warning Confirmation Dialog)
                    Button(
                        onClick = { showResetDataDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        modifier = Modifier.fillMaxWidth().testTag("reset_all_data_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CLEAR & RESET ALL DATA (SET TO 0)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- Log Out Confirmation Dialog ---
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                icon = {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Log Out",
                        tint = MonoPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                },
                title = {
                    Text(
                        text = "Log Out of TribeFit?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonoOnSurface
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to log out? Your recorded workouts, nutrition logs, step records, and permanent goal progress will remain safely saved in the local database.",
                        fontSize = 13.sp,
                        color = MonoOnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogOut()
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_logout_btn")
                    ) {
                        Text("LOG OUT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnPrimary)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showLogoutDialog = false },
                        border = BorderStroke(0.75.dp, MonoOutlineVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("cancel_logout_btn")
                    ) {
                        Text("CANCEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }
                },
                containerColor = MonoSurfaceLowest,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // --- Clear & Reset All Data Confirmation Dialog ---
        if (showResetDataDialog) {
            AlertDialog(
                onDismissRequest = { showResetDataDialog = false },
                icon = {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "⚠️ Clear All Local Data?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                },
                text = {
                    Text(
                        text = "DANGER: This action will permanently erase all your logged workouts, nutrition meals, weight history, water records, and 10-goal task progress from this device.\n\nAll counters will be reset to zero (0). This action cannot be undone. Are you sure you want to proceed?",
                        fontSize = 13.sp,
                        color = MonoOnSurface,
                        lineHeight = 18.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showResetDataDialog = false
                            onResetAllData()
                            Toast.makeText(context, "All data has been cleared and reset to 0", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_clear_all_data_btn")
                    ) {
                        Text("YES, CLEAR ALL DATA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showResetDataDialog = false },
                        border = BorderStroke(0.75.dp, MonoOutlineVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("cancel_clear_all_data_btn")
                    ) {
                        Text("CANCEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }
                },
                containerColor = MonoSurfaceLowest,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
