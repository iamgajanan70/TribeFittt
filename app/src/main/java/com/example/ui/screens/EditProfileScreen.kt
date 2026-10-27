package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnPrimary
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    userProfile: UserProfileEntity?,
    onSaveProfile: (
        fullName: String,
        email: String,
        username: String,
        age: Int,
        gender: String,
        heightCm: Int,
        weightKg: Int
    ) -> Unit,
    onSaveSleepSchedule: (
        wakeUpTime: String,
        sleepTime: String,
        sleepHoursGoal: Float,
        actualSleepHours: Float
    ) -> Unit = { _, _, _, _ -> },
    onUploadAvatar: (Bitmap) -> Unit,
    isUploadingImage: Boolean = false
) {
    val context = LocalContext.current

    var fullName by remember(userProfile) { mutableStateOf(userProfile?.fullName ?: "Alex") }
    var email by remember(userProfile) { mutableStateOf(userProfile?.email ?: "alex@fitness.com") }
    var username by remember(userProfile) { mutableStateOf(userProfile?.username ?: "alexfit") }
    var age by remember(userProfile) { mutableStateOf((userProfile?.age ?: 25).toString()) }
    var gender by remember(userProfile) { mutableStateOf(userProfile?.gender ?: "Male") }
    var heightCm by remember(userProfile) { mutableStateOf((userProfile?.heightCm ?: 180).toString()) }
    var weightKg by remember(userProfile) { mutableStateOf((userProfile?.weightKg ?: 75).toString()) }

    var wakeUpTime by remember(userProfile) { mutableStateOf(userProfile?.wakeUpTime ?: "04:30 AM") }
    var sleepTime by remember(userProfile) { mutableStateOf(userProfile?.sleepTime ?: "10:00 PM") }
    var sleepGoalText by remember(userProfile) { mutableStateOf((userProfile?.sleepHoursGoal ?: 6.0f).toString()) }
    var actualSleepText by remember(userProfile) { mutableStateOf((userProfile?.actualSleepHours ?: 0.0f).toString()) }

    var expandedGender by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")

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
                .testTag("edit_profile_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        UserAvatar(
                            avatarUrl = userProfile?.avatarUrl,
                            fullName = fullName,
                            size = 80.dp,
                            onClick = { photoPickerLauncher.launch("image/*") }
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MonoPrimary)
                                .clickable { photoPickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Upload Avatar",
                                tint = MonoOnPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = if (isUploadingImage) "Uploading photo..." else "Tap to change profile picture",
                        fontSize = 11.sp,
                        color = MonoOnSurfaceVariant
                    )
                }
            }

            // Sleep & Wake Times Schedule Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = subtleBorder,
                modifier = Modifier.fillMaxWidth().testTag("edit_sleep_schedule_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = MonoPrimary)
                        Text("EDIT SLEEP & WAKE SCHEDULE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }

                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))

                    // Wake Up Time Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
                            Text("WAKE UP TIME (Selected: $wakeUpTime)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
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
                }
            }

            // General Profile Details
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
                    Text("ACCOUNT DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_fullname_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MonoOnSurface,
                            unfocusedTextColor = MonoOnSurface,
                            focusedContainerColor = MonoSurfaceLowest,
                            unfocusedContainerColor = MonoSurfaceLowest,
                            focusedBorderColor = MonoPrimary,
                            unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_username_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MonoOnSurface,
                            unfocusedTextColor = MonoOnSurface,
                            focusedContainerColor = MonoSurfaceLowest,
                            unfocusedContainerColor = MonoSurfaceLowest,
                            focusedBorderColor = MonoPrimary,
                            unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MonoOnSurface,
                            unfocusedTextColor = MonoOnSurface,
                            focusedContainerColor = MonoSurfaceLowest,
                            unfocusedContainerColor = MonoSurfaceLowest,
                            focusedBorderColor = MonoPrimary,
                            unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = heightCm,
                            onValueChange = { heightCm = it },
                            label = { Text("Height (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("edit_height_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MonoOnSurface,
                                unfocusedTextColor = MonoOnSurface,
                                focusedContainerColor = MonoSurfaceLowest,
                                unfocusedContainerColor = MonoSurfaceLowest,
                                focusedBorderColor = MonoPrimary,
                                unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                            )
                        )

                        OutlinedTextField(
                            value = weightKg,
                            onValueChange = { weightKg = it },
                            label = { Text("Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("edit_weight_input"),
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

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it },
                            label = { Text("Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("edit_age_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MonoOnSurface,
                                unfocusedTextColor = MonoOnSurface,
                                focusedContainerColor = MonoSurfaceLowest,
                                unfocusedContainerColor = MonoSurfaceLowest,
                                focusedBorderColor = MonoPrimary,
                                unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                            )
                        )

                        ExposedDropdownMenuBox(
                            expanded = expandedGender,
                            onExpandedChange = { expandedGender = !expandedGender },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = gender,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Gender") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                                modifier = Modifier.menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MonoOnSurface,
                                    unfocusedTextColor = MonoOnSurface,
                                    focusedContainerColor = MonoSurfaceLowest,
                                    unfocusedContainerColor = MonoSurfaceLowest,
                                    focusedBorderColor = MonoPrimary,
                                    unfocusedBorderColor = MonoOutlineVariant.copy(alpha = 0.5f)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedGender,
                                onDismissRequest = { expandedGender = false }
                            ) {
                                genderOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            gender = option
                                            expandedGender = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Save Action Button
            Button(
                onClick = {
                    val a = age.toIntOrNull() ?: 25
                    val h = heightCm.toIntOrNull() ?: 180
                    val w = weightKg.toIntOrNull() ?: 75
                    val sGoal = sleepGoalText.toFloatOrNull() ?: 6.0f
                    val sAct = actualSleepText.toFloatOrNull() ?: 0.0f

                    onSaveProfile(fullName, email, username, a, gender, h, w)
                    onSaveSleepSchedule(wakeUpTime, sleepTime, sGoal, sAct)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_profile_button")
            ) {
                Text("SAVE ALL CHANGES", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnPrimary)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
