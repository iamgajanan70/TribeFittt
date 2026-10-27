package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.data.local.UserProfileEntity
import com.example.ui.components.EditSleepScheduleDialog
import com.example.ui.components.UserAvatar
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest

@Composable
fun ProfileScreen(
    userProfile: UserProfileEntity?,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onUpdateSleepSchedule: (wakeUpTime: String, sleepTime: String, sleepGoalHours: Float, actualSleepHours: Float) -> Unit = { _, _, _, _ -> },
    onLogOut: () -> Unit,
    statusMessage: String? = null
) {
    val name = if (userProfile?.fullName.isNullOrBlank()) "Fitness User" else userProfile!!.fullName
    val email = if (userProfile?.email.isNullOrBlank()) "user@example.com" else userProfile!!.email
    val username = if (userProfile?.username.isNullOrBlank()) "user" else userProfile!!.username
    val avatarUrl = userProfile?.avatarUrl

    val heightCm = userProfile?.heightCm ?: 180
    val weightKg = userProfile?.weightKg ?: 75
    val bmi = String.format("%.1f", weightKg / ((heightCm / 100f) * (heightCm / 100f)))

    var showSleepScheduleDialog by remember { mutableStateOf(false) }
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
                .testTag("profile_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Header Card
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        UserAvatar(
                            avatarUrl = avatarUrl,
                            fullName = name,
                            size = 60.dp,
                            onClick = onNavigateToEditProfile
                        )
                        Column {
                            Text(name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            Text("@$username", fontSize = 12.sp, color = MonoOnSurfaceVariant)
                            Text(email, fontSize = 11.sp, color = MonoOnSurfaceVariant)
                        }
                    }

                    OutlinedButton(
                        onClick = onNavigateToEditProfile,
                        border = BorderStroke(0.75.dp, MonoOutlineVariant),
                        modifier = Modifier.height(34.dp).testTag("profile_edit_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MonoOnSurface, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }
                }
            }

            // Body Metrics Overview
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("HEIGHT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                        Text("${heightCm}cm", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("WEIGHT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                        Text("${weightKg}kg", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BMI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                        Text(bmi, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("AGE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                        Text("${userProfile?.age ?: 25}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }
                }
            }

            // Sleep & Wake Times Card (Interactive and Editable)
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSleepScheduleDialog = true }
                    .testTag("profile_sleep_schedule_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = MonoPrimary, modifier = Modifier.size(18.dp))
                            Text("SLEEP & WAKE SCHEDULE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MonoSurfaceContainerLow)
                                .clickable { showSleepScheduleDialog = true }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("EDIT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoPrimary)
                        }
                    }

                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
                            Column {
                                Text("Wake Up", fontSize = 10.sp, color = MonoOnSurfaceVariant)
                                Text(userProfile?.wakeUpTime ?: "04:30 AM", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Bedtime, contentDescription = null, tint = Color(0xFF5C6BC0), modifier = Modifier.size(16.dp))
                            Column {
                                Text("Bedtime", fontSize = 10.sp, color = MonoOnSurfaceVariant)
                                Text(userProfile?.sleepTime ?: "10:00 PM", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Sleep Target", fontSize = 10.sp, color = MonoOnSurfaceVariant)
                            Text("${userProfile?.actualSleepHours ?: 0.0f} / ${userProfile?.sleepHoursGoal ?: 6.0f} hrs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                        }
                    }
                }
            }

            // Quick Nav List
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToSettings() }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = MonoPrimary, modifier = Modifier.size(18.dp))
                            Text("App Settings & Notifications", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MonoOnSurface)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MonoOnSurfaceVariant, modifier = Modifier.size(18.dp))
                    }

                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLogOut() }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                            Text("Reset Data / Log Out", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFC62828))
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MonoOnSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showSleepScheduleDialog) {
            EditSleepScheduleDialog(
                initialWakeUpTime = userProfile?.wakeUpTime ?: "04:30 AM",
                initialSleepTime = userProfile?.sleepTime ?: "10:00 PM",
                initialGoalHours = userProfile?.sleepHoursGoal ?: 6.0f,
                initialActualHours = userProfile?.actualSleepHours ?: 0.0f,
                onDismiss = { showSleepScheduleDialog = false },
                onSave = { wake, sleep, goalH, actualH ->
                    onUpdateSleepSchedule(wake, sleep, goalH, actualH)
                    showSleepScheduleDialog = false
                }
            )
        }
    }
}
