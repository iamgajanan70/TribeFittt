package com.example.data.model

data class NutritionSummary(
    val totalCalories: Int = 0,
    val targetCalories: Int = 2000,
    val caloriesLeft: Int = 2000,
    val totalProtein: Int = 0,
    val targetProtein: Int = 150,
    val totalCarbs: Int = 0,
    val targetCarbs: Int = 250,
    val totalFat: Int = 0,
    val targetFat: Int = 65,
    val breakfastCalories: Int = 0,
    val lunchCalories: Int = 0,
    val dinnerCalories: Int = 0,
    val snackCalories: Int = 0
)
