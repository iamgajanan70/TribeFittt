package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceLowest

@Composable
fun LogWorkoutDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, durationMin: Int, exercisesText: String) -> Unit
) {
    var title by remember { mutableStateOf("Push Day: Hypertrophy") }
    var durationText by remember { mutableStateOf("60") }
    var exercises by remember { mutableStateOf("Bench Press, Overhead Press, Tricep Dips") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MonoOutlineVariant, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "LOG WORKOUT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonoOnSurface
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Workout Title", fontSize = 12.sp, color = MonoOnSurfaceVariant) },
                    textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth().testTag("workout_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MonoOnSurface,
                        unfocusedTextColor = MonoOnSurface,
                        focusedContainerColor = MonoSurfaceLowest,
                        unfocusedContainerColor = MonoSurfaceLowest,
                        focusedBorderColor = MonoPrimary
                    )
                )

                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (minutes)", fontSize = 12.sp, color = MonoOnSurfaceVariant) },
                    textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("workout_duration_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MonoOnSurface,
                        unfocusedTextColor = MonoOnSurface,
                        focusedContainerColor = MonoSurfaceLowest,
                        unfocusedContainerColor = MonoSurfaceLowest,
                        focusedBorderColor = MonoPrimary
                    )
                )

                OutlinedTextField(
                    value = exercises,
                    onValueChange = { exercises = it },
                    label = { Text("Exercises (comma separated)", fontSize = 12.sp, color = MonoOnSurfaceVariant) },
                    textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth().testTag("workout_exercises_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MonoOnSurface,
                        unfocusedTextColor = MonoOnSurface,
                        focusedContainerColor = MonoSurfaceLowest,
                        unfocusedContainerColor = MonoSurfaceLowest,
                        focusedBorderColor = MonoPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MonoOutlineVariant)
                    ) {
                        Text("CANCEL", color = MonoOnSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val duration = durationText.toIntOrNull() ?: 45
                            onSave(title, duration, exercises)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                        modifier = Modifier.testTag("save_workout_button")
                    ) {
                        Text("SAVE WORKOUT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LogMealDialog(
    initialMealType: String = "Breakfast",
    onDismiss: () -> Unit,
    onSave: (mealType: String, foodName: String, calories: Int, protein: Int, carbs: Int, fat: Int) -> Unit
) {
    var mealType by remember { mutableStateOf(initialMealType) }
    var foodName by remember { mutableStateOf("Protein Shake") }
    var caloriesText by remember { mutableStateOf("250") }
    var proteinText by remember { mutableStateOf("30") }
    var carbsText by remember { mutableStateOf("20") }
    var fatText by remember { mutableStateOf("5") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MonoOutlineVariant, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "LOG MEAL ($mealType)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonoOnSurface
                )

                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = { Text("Food / Item Name", fontSize = 12.sp, color = MonoOnSurfaceVariant) },
                    textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth().testTag("meal_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MonoOnSurface,
                        unfocusedTextColor = MonoOnSurface,
                        focusedContainerColor = MonoSurfaceLowest,
                        unfocusedContainerColor = MonoSurfaceLowest,
                        focusedBorderColor = MonoPrimary
                    )
                )

                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it },
                    label = { Text("Calories (kcal)", fontSize = 12.sp, color = MonoOnSurfaceVariant) },
                    textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("meal_calories_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MonoOnSurface,
                        unfocusedTextColor = MonoOnSurface,
                        focusedContainerColor = MonoSurfaceLowest,
                        unfocusedContainerColor = MonoSurfaceLowest,
                        focusedBorderColor = MonoPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = proteinText,
                        onValueChange = { proteinText = it },
                        label = { Text("Protein (g)", fontSize = 10.sp, color = MonoOnSurfaceVariant) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MonoOnSurface,
                            unfocusedTextColor = MonoOnSurface,
                            focusedContainerColor = MonoSurfaceLowest,
                            unfocusedContainerColor = MonoSurfaceLowest,
                            focusedBorderColor = MonoPrimary
                        )
                    )
                    OutlinedTextField(
                        value = carbsText,
                        onValueChange = { carbsText = it },
                        label = { Text("Carbs (g)", fontSize = 10.sp, color = MonoOnSurfaceVariant) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MonoOnSurface,
                            unfocusedTextColor = MonoOnSurface,
                            focusedContainerColor = MonoSurfaceLowest,
                            unfocusedContainerColor = MonoSurfaceLowest,
                            focusedBorderColor = MonoPrimary
                        )
                    )
                    OutlinedTextField(
                        value = fatText,
                        onValueChange = { fatText = it },
                        label = { Text("Fat (g)", fontSize = 10.sp, color = MonoOnSurfaceVariant) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MonoOnSurface,
                            unfocusedTextColor = MonoOnSurface,
                            focusedContainerColor = MonoSurfaceLowest,
                            unfocusedContainerColor = MonoSurfaceLowest,
                            focusedBorderColor = MonoPrimary
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MonoOutlineVariant)
                    ) {
                        Text("CANCEL", color = MonoOnSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val c = caloriesText.toIntOrNull() ?: 200
                            val p = proteinText.toIntOrNull() ?: 20
                            val cb = carbsText.toIntOrNull() ?: 20
                            val f = fatText.toIntOrNull() ?: 5
                            onSave(mealType, foodName, c, p, cb, f)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                        modifier = Modifier.testTag("save_meal_button")
                    ) {
                        Text("ADD MEAL", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddWeightDialog(
    onDismiss: () -> Unit,
    onSave: (weightKg: Float) -> Unit
) {
    var weightText by remember { mutableStateOf("75.0") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MonoOutlineVariant, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "RECORD BODY WEIGHT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonoOnSurface
                )

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight (kg)", fontSize = 12.sp, color = MonoOnSurfaceVariant) },
                    textStyle = androidx.compose.ui.text.TextStyle(color = MonoOnSurface, fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("weight_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MonoOnSurface,
                        unfocusedTextColor = MonoOnSurface,
                        focusedContainerColor = MonoSurfaceLowest,
                        unfocusedContainerColor = MonoSurfaceLowest,
                        focusedBorderColor = MonoPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MonoOutlineVariant)
                    ) {
                        Text("CANCEL", color = MonoOnSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val w = weightText.toFloatOrNull() ?: 75.0f
                            onSave(w)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MonoPrimary),
                        modifier = Modifier.testTag("save_weight_button")
                    ) {
                        Text("SAVE WEIGHT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
