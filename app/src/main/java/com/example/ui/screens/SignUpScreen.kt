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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onSignUpComplete: (
        fullName: String,
        email: String,
        pass: String,
        age: Int,
        gender: String,
        heightCm: Int,
        weightKg: Int
    ) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("25") }
    var gender by remember { mutableStateOf("Male") }
    var heightText by remember { mutableStateOf("175") }
    var weightText by remember { mutableStateOf("70") }
    var formError by remember { mutableStateOf<String?>(null) }

    var genderExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MonoBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .testTag("signup_screen")
        ) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MonoOnSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MonoPrimary)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MonoOutlineVariant)
                    )
                }

                Spacer(modifier = Modifier.width(40.dp))
            }

            Text(
                text = "First Time Registration",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MonoOnSurface
            )
            Text(
                text = "Enter your details to create your profile & initialize all fitness records.",
                fontSize = 13.sp,
                color = MonoOnSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (!formError.isNullOrBlank()) {
                Text(
                    text = formError!!,
                    fontSize = 13.sp,
                    color = MonoPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Account Details Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceContainerLow),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MonoOutlineVariant, RoundedCornerShape(12.dp))
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Account Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonoOnSurface
                    )
                    HorizontalDivider(color = MonoOutlineVariant)

                    Column {
                        Text("FULL NAME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = { Text("e.g. John Doe", color = MonoOnSurfaceVariant) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth().testTag("signup_fullname_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MonoOnSurface,
                                unfocusedTextColor = MonoOnSurface,
                                focusedContainerColor = MonoSurfaceLowest,
                                unfocusedContainerColor = MonoSurfaceLowest,
                                focusedBorderColor = MonoPrimary
                            )
                        )
                    }

                    Column {
                        Text("EMAIL ADDRESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("e.g. john@example.com", color = MonoOnSurfaceVariant) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth().testTag("signup_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MonoOnSurface,
                                unfocusedTextColor = MonoOnSurface,
                                focusedContainerColor = MonoSurfaceLowest,
                                unfocusedContainerColor = MonoSurfaceLowest,
                                focusedBorderColor = MonoPrimary
                            )
                        )
                    }

                    Column {
                        Text("PASSWORD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Create a password", color = MonoOnSurfaceVariant) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("signup_password_input"),
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
            }

            // Personal Stats Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceContainerLow),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MonoOutlineVariant, RoundedCornerShape(12.dp))
                    .padding(bottom = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Personal Stats",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonoOnSurface
                    )
                    HorizontalDivider(color = MonoOutlineVariant)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AGE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            OutlinedTextField(
                                value = ageText,
                                onValueChange = { ageText = it },
                                textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("signup_age_input"),
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
                            Text("GENDER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            ExposedDropdownMenuBox(
                                expanded = genderExpanded,
                                onExpandedChange = { genderExpanded = !genderExpanded }
                            ) {
                                OutlinedTextField(
                                    value = gender,
                                    onValueChange = {},
                                    readOnly = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                        .testTag("signup_gender_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = MonoOnSurface,
                                        unfocusedTextColor = MonoOnSurface,
                                        focusedContainerColor = MonoSurfaceLowest,
                                        unfocusedContainerColor = MonoSurfaceLowest,
                                        focusedBorderColor = MonoPrimary
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = genderExpanded,
                                    onDismissRequest = { genderExpanded = false }
                                ) {
                                    listOf("Male", "Female", "Other").forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                gender = option
                                                genderExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("HEIGHT (CM)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            OutlinedTextField(
                                value = heightText,
                                onValueChange = { heightText = it },
                                textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("signup_height_input"),
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
                            Text("WEIGHT (KG)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it },
                                textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("signup_weight_input"),
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
                }
            }

            Button(
                onClick = {
                    if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                        formError = "Please enter your Full Name, Email, and Password."
                    } else {
                        formError = null
                        val age = ageText.toIntOrNull() ?: 25
                        val h = heightText.toIntOrNull() ?: 175
                        val w = weightText.toIntOrNull() ?: 70
                        onSignUpComplete(fullName, email, password, age, gender, h, w)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("continue_signup_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("COMPLETE REGISTRATION & PRINT RECORDS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 0.5.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ", fontSize = 13.sp, color = MonoOnSurfaceVariant)
                Text(
                    text = "Log in",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonoPrimary,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}
