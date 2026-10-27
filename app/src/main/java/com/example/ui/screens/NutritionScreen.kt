package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.local.NutritionMealEntity
import com.example.data.model.NutritionSummary
import com.example.ui.components.LogMealDialog
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerHigh
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest

@Composable
fun NutritionScreen(
    meals: List<NutritionMealEntity>,
    nutritionSummary: NutritionSummary,
    onAddMeal: (type: String, food: String, cal: Int, p: Int, c: Int, f: Int) -> Unit,
    onDeleteMeal: (id: Long) -> Unit = {}
) {
    var activeMealTypeForDialog by remember { mutableStateOf<String?>(null) }

    val breakfastMeals = meals.filter { it.mealType.equals("Breakfast", true) }
    val lunchMeals = meals.filter { it.mealType.equals("Lunch", true) }
    val dinnerMeals = meals.filter { it.mealType.equals("Dinner", true) }
    val snackMeals = meals.filter { it.mealType.equals("Snacks", true) || it.mealType.equals("Snack", true) }

    val subtleBorder = BorderStroke(0.75.dp, MonoOutlineVariant.copy(alpha = 0.45f))

    val calProgressRatio by animateFloatAsState(
        targetValue = if (nutritionSummary.targetCalories > 0) (nutritionSummary.totalCalories.toFloat() / nutritionSummary.targetCalories).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "cal_progress"
    )

    val proRatio by animateFloatAsState(
        targetValue = if (nutritionSummary.targetProtein > 0) (nutritionSummary.totalProtein.toFloat() / nutritionSummary.targetProtein).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "pro_ratio"
    )

    val carbRatio by animateFloatAsState(
        targetValue = if (nutritionSummary.targetCarbs > 0) (nutritionSummary.totalCarbs.toFloat() / nutritionSummary.targetCarbs).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "carb_ratio"
    )

    val fatRatio by animateFloatAsState(
        targetValue = if (nutritionSummary.targetFat > 0) (nutritionSummary.totalFat.toFloat() / nutritionSummary.targetFat).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "fat_ratio"
    )

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
                .testTag("nutrition_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Energy Balance Card
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
                    Text(
                        text = "Energy Balance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonoOnSurface
                    )
                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "${nutritionSummary.caloriesLeft}",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = MonoPrimary
                            )
                            Text(
                                text = "KCAL REMAINING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MonoOnSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${nutritionSummary.targetCalories}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                Text("TARGET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${nutritionSummary.totalCalories}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                Text("CONSUMED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoOnSurfaceVariant)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(MonoSurfaceContainerHigh)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(calProgressRatio)
                                .height(6.dp)
                                .background(MonoPrimary)
                        )
                    }
                }
            }

            // Macro Nutrients Breakdown Card
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
                    Text("Macronutrients", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))

                    // Protein
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Protein", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MonoOnSurface)
                            Text("${nutritionSummary.totalProtein}g / ${nutritionSummary.targetProtein}g", fontSize = 11.sp, color = MonoOnSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(MonoSurfaceContainerHigh)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(proRatio)
                                    .height(6.dp)
                                    .background(Color(0xFFE53935))
                            )
                        }
                    }

                    // Carbs
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Carbohydrates", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MonoOnSurface)
                            Text("${nutritionSummary.totalCarbs}g / ${nutritionSummary.targetCarbs}g", fontSize = 11.sp, color = MonoOnSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(MonoSurfaceContainerHigh)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(carbRatio)
                                    .height(6.dp)
                                    .background(Color(0xFF1E88E5))
                            )
                        }
                    }

                    // Fat
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Fats", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MonoOnSurface)
                            Text("${nutritionSummary.totalFat}g / ${nutritionSummary.targetFat}g", fontSize = 11.sp, color = MonoOnSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(MonoSurfaceContainerHigh)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fatRatio)
                                    .height(6.dp)
                                    .background(Color(0xFFFB8C00))
                            )
                        }
                    }
                }
            }

            // Meals Timeline (Breakfast, Lunch, Dinner, Snacks)
            Text("Daily Meals Log", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)

            val mealSections = listOf(
                Pair("Breakfast", breakfastMeals),
                Pair("Lunch", lunchMeals),
                Pair("Dinner", dinnerMeals),
                Pair("Snacks", snackMeals)
            )

            mealSections.forEach { (type, sectionMeals) ->
                val totalSectionCal = sectionMeals.sumOf { it.calories }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                    shape = RoundedCornerShape(16.dp),
                    border = subtleBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(type, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MonoOnSurface)
                                Text("• $totalSectionCal kcal", fontSize = 11.sp, color = MonoOnSurfaceVariant)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MonoSurfaceContainerLow)
                                    .clickable { activeMealTypeForDialog = type }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = MonoPrimary, modifier = Modifier.size(14.dp))
                                    Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoPrimary)
                                }
                            }
                        }

                        if (sectionMeals.isNotEmpty()) {
                            HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))
                            sectionMeals.forEach { meal ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(meal.foodName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MonoOnSurface)
                                        Text("${meal.calories} kcal • P: ${meal.proteinGrams}g | C: ${meal.carbsGrams}g | F: ${meal.fatGrams}g", fontSize = 10.sp, color = MonoOnSurfaceVariant)
                                    }
                                    IconButton(
                                        onClick = { onDeleteMeal(meal.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Meal", tint = MonoOnSurfaceVariant, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (activeMealTypeForDialog != null) {
            LogMealDialog(
                initialMealType = activeMealTypeForDialog!!,
                onDismiss = { activeMealTypeForDialog = null },
                onSave = { type, food, c, p, cb, f ->
                    onAddMeal(type, food, c, p, cb, f)
                    activeMealTypeForDialog = null
                }
            )
        }
    }
}
