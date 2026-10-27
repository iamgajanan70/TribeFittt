package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val age: Int = 25,
    val gender: String = "Male",
    val heightCm: Int = 175,
    val weightKg: Int = 70,
    val avatarUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuADcPd1ixDcUyX7QKNaEN-STrZm2kPRpnvhvmia3QeFPk0EuGuzxn8GyUYW_6VRjnUGetdb7vBBPe0eGj4Fcz_VxhArYJOdHHv0Ei4EYf80rGav5cKkcma4TiNjLNHCgUy2E-_OPXA1wuutd7vtnrpM0a_TMASWtoYOag_1OfPKYzFt3OiN8l5sAyGG3lVLZpAbyxvAzymG4CcCBKAjZ4CflT267t5w_1NR172ykeGKnfb34m5wyp6l",
    val avatarHash: String = "",
    val isLoggedIn: Boolean = false,
    val goal: String = "Fitness & Health",
    val calorieGoal: Int = 2000,
    val proteinGoal: Int = 150,
    val carbGoal: Int = 250,
    val fatGoal: Int = 65,
    val waterGoalLiters: Float = 3.0f,
    val waterDrankLiters: Float = 0.0f,
    val waterReminderIntervalHours: Int = 2,
    val lastWaterReminderTimestamp: Long = 0L,
    val stepGoal: Int = 10000,
    val currentSteps: Int = 0,
    val calorieBurnGoal: Int = 500,
    val caloriesBurnedToday: Int = 0,
    val wakeUpTime: String = "04:30 AM",
    val sleepTime: String = "10:00 PM",
    val sleepHoursGoal: Float = 6.0f,
    val actualSleepHours: Float = 0.0f,
    val dailyReadingPagesGoal: Int = 10,
    val pagesReadToday: Int = 0,
    val digitalWellbeingMinutesToday: Int = 0,
    val focusModeActive: Boolean = false,
    val habitNotificationsEnabled: Boolean = true,
    val waterRemindersEnabled: Boolean = true,
    val isDarkMode: Boolean = false,
    val lastActiveDate: String = ""
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val durationMinutes: Int,
    val exerciseCount: Int,
    val exercisesText: String,
    val status: String, // Scheduled, Completed
    val dateText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "nutrition_meals")
data class NutritionMealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mealType: String, // Breakfast, Lunch, Dinner, Snacks
    val foodName: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val imageUrl: String? = null,
    val imageHash: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_habits")
data class DailyHabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0,
    val category: String = "General",
    val reminderTime: String = "08:00 AM",
    val reminderEnabled: Boolean = true
)

@Entity(tableName = "weight_logs")
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Float,
    val dateText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "habit_records",
    indices = [androidx.room.Index(value = ["habitId", "dateText"], unique = true)]
)
data class HabitRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val dateText: String, // format "yyyy-MM-dd" e.g. "2026-08-19"
    val isCompleted: Boolean = true,
    val completedTimestamp: Long = System.currentTimeMillis()
)

