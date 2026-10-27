package com.example.ui.screens

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceLowest

@Composable
fun LoginScreen(
    userProfile: com.example.data.local.UserProfileEntity? = null,
    onLoginSuccess: (email: String, pass: String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    statusMessage: String? = null
) {
    var email by remember(userProfile?.email) { mutableStateOf(userProfile?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .testTag("login_screen")
        ) {
            // Top Section (Black Header)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome Back",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Log in to continue your fitness journey.",
                    fontSize = 14.sp,
                    color = Color(0xFFD0D0D0)
                )
            }

            // Bottom Section (White Form Surface)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MonoSurfaceLowest, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (!statusMessage.isNullOrBlank() || !localError.isNullOrBlank()) {
                        Text(
                            text = localError ?: statusMessage ?: "",
                            fontSize = 12.sp,
                            color = MonoPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "EMAIL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoOnSurface,
                            letterSpacing = 0.5.sp
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("Enter your email", color = MonoOnSurfaceVariant) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth().testTag("email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MonoOnSurface,
                                unfocusedTextColor = MonoOnSurface,
                                focusedContainerColor = MonoSurfaceLowest,
                                unfocusedContainerColor = MonoSurfaceLowest,
                                focusedBorderColor = MonoPrimary,
                                unfocusedBorderColor = MonoOutlineVariant
                            )
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "PASSWORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoOnSurface,
                            letterSpacing = 0.5.sp
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Enter your password", color = MonoOnSurfaceVariant) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth().testTag("password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MonoOnSurface,
                                unfocusedTextColor = MonoOnSurface,
                                focusedContainerColor = MonoSurfaceLowest,
                                unfocusedContainerColor = MonoSurfaceLowest,
                                focusedBorderColor = MonoPrimary,
                                unfocusedBorderColor = MonoOutlineVariant
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                localError = "Please enter both Email and Password."
                            } else {
                                localError = null
                                onLoginSuccess(email, password)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "LOG IN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MonoOutlineVariant)
                        Text(
                            text = "OR CONTINUE WITH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoOnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MonoOutlineVariant)
                    }

                    OutlinedButton(
                        onClick = { onLoginSuccess("google.user@example.com", "google") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("google_login_button"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MonoOutlineVariant)
                    ) {
                        Text("GOOGLE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Don't have an account? ",
                            fontSize = 13.sp,
                            color = MonoOnSurfaceVariant
                        )
                        Text(
                            text = "Sign Up",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoPrimary,
                            modifier = Modifier.clickable { onNavigateToSignUp() }.testTag("go_to_signup")
                        )
                    }
                }
            }
        }
    }
}
