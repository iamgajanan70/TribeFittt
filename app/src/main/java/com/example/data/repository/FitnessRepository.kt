package com.example.data.repository

import com.example.data.local.DailyStepRecordEntity
import com.example.data.local.DailyTaskRecordEntity
import com.example.data.local.DailyWaterRecordEntity
import com.example.data.local.FitnessDao
import com.example.data.local.NutritionMealEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WaterLogEntryEntity
import com.example.data.local.WeightLogEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.PermanentTask
import com.example.data.model.TaskType
import com.example.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class FitnessRepository(
    private val dao: FitnessDao
) {
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val workouts: Flow<List<WorkoutLogEntity>> = dao.getAllWorkouts()
    val meals: Flow<List<NutritionMealEntity>> = dao.getAllMeals()
    val weightLogs: Flow<List<WeightLogEntity>> = dao.getAllWeightLogs()
    val waterRecords: Flow<List<DailyWaterRecordEntity>> = dao.getAllWaterRecords()
    val stepRecords: Flow<List<DailyStepRecordEntity>> = dao.getAllStepRecords()
    val allDailyTaskRecords: Flow<List<DailyTaskRecordEntity>> = dao.getAllDailyTaskRecords()

    fun getDailyRecordsForDate(dateText: String): Flow<List<DailyTaskRecordEntity>> {
        return dao.getDailyTaskRecordsForDate(dateText)
    }

    suspend fun updateUserProfile(profile: UserProfileEntity) {
        dao.insertOrUpdateUserProfile(profile)
    }

    // Workouts (100% Real)
    suspend fun addWorkout(workout: WorkoutLogEntity) {
        dao.insertWorkout(workout)
        // Also update PERMANENT_WORKOUT task for today
        val todayStr = DateUtils.getTodayDateString()
        updateTaskProgress(
            taskId = PermanentTask.ID_WORKOUT,
            dateText = todayStr,
            actualValue = 1.0f,
            extraData = workout.title
        )
    }

    suspend fun deleteWorkout(id: Long) {
        dao.deleteWorkout(id)
    }

    // Nutrition Meals (100% Real)
    suspend fun addMeal(meal: NutritionMealEntity) {
        dao.insertMeal(meal)
    }

    suspend fun deleteMeal(id: Long) {
        dao.deleteMeal(id)
    }

    // Body Weight (100% Real)
    suspend fun addWeightLog(weightKg: Float, dateText: String = "Today") {
        val actualDate = if (dateText == "Today") DateUtils.getTodayDateString() else dateText
        dao.insertWeightLog(WeightLogEntity(weightKg = weightKg, dateText = actualDate))
    }

    /**
     * Updates or creates the daily record for one of the 10 permanent tasks on [dateText].
     */
    suspend fun updateTaskProgress(
        taskId: String,
        dateText: String,
        actualValue: Float,
        extraData: String = ""
    ) {
        val def = PermanentTask.getById(taskId) ?: return
        val existing = dao.getDailyTaskRecord(taskId, dateText)

        val target = def.targetValue
        val isCompleted: Boolean
        val percentage: Float
        val display: String

        when (def.taskType) {
            TaskType.STEPS -> {
                isCompleted = actualValue >= target
                percentage = if (target > 0) (actualValue / target * 100f).coerceIn(0f, 100f) else 0f
                display = "${String.format(Locale.US, "%,d", actualValue.toInt())} / ${String.format(Locale.US, "%,d", target.toInt())} steps"
            }
            TaskType.WATER -> {
                isCompleted = actualValue >= target
                percentage = if (target > 0) (actualValue / target * 100f).coerceIn(0f, 100f) else 0f
                display = "${String.format(Locale.US, "%.1f", actualValue)} / ${String.format(Locale.US, "%.1f", target)} L"
            }
            TaskType.NO_JUNK_FOOD -> {
                isCompleted = actualValue >= 1.0f
                percentage = if (isCompleted) 100f else 0f
                display = if (isCompleted) "Clean Eating Maintained ✓" else "Not Checked"
            }
            TaskType.PROTEIN_MEALS -> {
                val mealsCount = actualValue.toInt().coerceIn(0, 3)
                isCompleted = mealsCount >= 3
                percentage = (mealsCount / 3f * 100f).coerceIn(0f, 100f)
                display = "$mealsCount/3 meals with protein"
            }
            TaskType.SLEEP -> {
                isCompleted = actualValue >= target
                percentage = if (target > 0) (actualValue / target * 100f).coerceIn(0f, 100f) else 0f
                val hrs = actualValue.toInt()
                val mins = ((actualValue - hrs) * 60).toInt()
                display = if (mins > 0) "${hrs}h ${mins}m / ${target.toInt()}h" else "${hrs}h / ${target.toInt()}h"
            }
            TaskType.WORKOUT -> {
                isCompleted = actualValue >= 1.0f
                percentage = if (isCompleted) 100f else 0f
                display = if (isCompleted) (if (extraData.isNotBlank()) "Completed: $extraData" else "Daily Workout Completed ✓") else "Pending"
            }
            TaskType.STUDY -> {
                val mins = actualValue.toInt()
                isCompleted = mins >= target.toInt()
                percentage = if (target > 0) (mins.toFloat() / target * 100f).coerceIn(0f, 100f) else 0f
                val studyHrs = mins / 60
                val studyRemMins = mins % 60
                display = "${studyHrs}h ${studyRemMins}m / 2h 00m"
            }
            TaskType.READING -> {
                val pages = actualValue.toInt()
                isCompleted = pages >= target.toInt()
                percentage = if (target > 0) (pages.toFloat() / target * 100f).coerceIn(0f, 100f) else 0f
                display = "$pages / ${target.toInt()} pages"
            }
            TaskType.BEDTIME -> {
                // actualValue represents decimal hour, e.g. 21.8 = 9:48 PM, 22.0 = 10:00 PM
                isCompleted = actualValue in 0.1f..target
                percentage = if (isCompleted) 100f else 0f
                display = if (extraData.isNotBlank()) "$extraData ${if (isCompleted) "✓" else "✗"}" else if (actualValue > 0) "${String.format(Locale.US, "%.2f", actualValue)} ${if (isCompleted) "✓" else "✗"}" else "Not Logged"
            }
            TaskType.WAKEUP -> {
                // actualValue represents decimal hour, e.g. 4.3 = 4:18 AM, 4.5 = 4:30 AM
                isCompleted = actualValue in 0.1f..target
                percentage = if (isCompleted) 100f else 0f
                display = if (extraData.isNotBlank()) "$extraData ${if (isCompleted) "✓" else "✗"}" else if (actualValue > 0) "${String.format(Locale.US, "%.2f", actualValue)} ${if (isCompleted) "✓" else "✗"}" else "Not Logged"
            }
        }

        val recordToSave = DailyTaskRecordEntity(
            id = existing?.id ?: 0,
            taskId = taskId,
            dateText = dateText,
            actualValue = actualValue,
            targetValue = target,
            isCompleted = isCompleted,
            completionPercentage = percentage,
            valueDisplay = display,
            extraData = if (extraData.isNotBlank()) extraData else existing?.extraData ?: "",
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        dao.insertOrUpdateDailyTaskRecord(recordToSave)
    }

    /**
     * Toggles binary completion for a task (e.g. No Junk Food, Workout).
     */
    suspend fun toggleTaskCompletion(taskId: String, dateText: String) {
        val existing = dao.getDailyTaskRecord(taskId, dateText)
        val currentlyCompleted = existing?.isCompleted ?: false
        val newValue = if (currentlyCompleted) 0.0f else 1.0f
        updateTaskProgress(taskId, dateText, newValue)
    }

    // Daily Water Intake (Tracked independently per calendar date in Room)
    suspend fun addWater(amountLiters: Float, goalLiters: Float = 3.0f, dateText: String = DateUtils.getTodayDateString()) {
        val existing = dao.getWaterRecordForDateDirect(userId = 1, dateText = dateText)
        val currentConsumed = existing?.waterConsumedLiters ?: 0.0f
        val newConsumed = (currentConsumed + amountLiters).coerceAtLeast(0.0f)
        val rounded = Math.round(newConsumed * 10f) / 10f

        val updatedRecord = DailyWaterRecordEntity(
            id = existing?.id ?: 0,
            userId = 1,
            dateText = dateText,
            waterConsumedLiters = rounded,
            waterGoalLiters = if (existing != null && existing.waterGoalLiters > 0) existing.waterGoalLiters else goalLiters,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        dao.insertOrUpdateWaterRecord(updatedRecord)

        // Save individual entry log
        if (amountLiters > 0f) {
            dao.insertWaterLogEntry(
                WaterLogEntryEntity(
                    userId = 1,
                    dateText = dateText,
                    amountLiters = amountLiters,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // Sync with PERMANENT_WATER_3L daily task record
        updateTaskProgress(PermanentTask.ID_WATER, dateText, rounded)
    }

    // Daily Steps (Tracked independently per calendar date in Room)
    suspend fun addSteps(deltaSteps: Int, stepGoal: Int = 10000, dateText: String = DateUtils.getTodayDateString()) {
        val existing = dao.getStepRecordForDateDirect(userId = 1, dateText = dateText)
        val currentSteps = existing?.stepCount ?: 0
        val newSteps = (currentSteps + deltaSteps).coerceAtLeast(0)

        val updatedRecord = DailyStepRecordEntity(
            id = existing?.id ?: 0,
            userId = 1,
            dateText = dateText,
            stepCount = newSteps,
            stepGoal = if (existing != null && existing.stepGoal > 0) existing.stepGoal else stepGoal,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        dao.insertOrUpdateStepRecord(updatedRecord)

        // Sync with PERMANENT_STEPS_10K daily task record
        updateTaskProgress(PermanentTask.ID_STEPS, dateText, newSteps.toFloat())
    }

    /**
     * Completely resets local database to clean initial state.
     * Zero demo data, zero mock weights, zero fake records.
     */
    suspend fun resetAllData() {
        dao.clearAllWorkouts()
        dao.clearAllMeals()
        dao.clearAllWeightLogs()
        dao.clearAllDailyTaskRecords()
        dao.clearAllWaterRecords()
        dao.clearAllWaterLogEntries()
        dao.clearAllStepRecords()

        val todayStr = DateUtils.getTodayDateString()
        dao.insertOrUpdateUserProfile(
            UserProfileEntity(
                id = 1,
                fullName = "",
                email = "",
                username = "",
                isLoggedIn = false,
                currentSteps = 0,
                stepGoal = 10000,
                waterDrankLiters = 0.0f,
                waterGoalLiters = 3.0f,
                waterReminderIntervalHours = 2,
                caloriesBurnedToday = 0,
                calorieBurnGoal = 500,
                wakeUpTime = "04:30 AM",
                sleepTime = "10:00 PM",
                sleepHoursGoal = 6.0f,
                actualSleepHours = 0.0f,
                dailyReadingPagesGoal = 10,
                pagesReadToday = 0,
                habitNotificationsEnabled = true,
                waterRemindersEnabled = true,
                isDarkMode = false,
                lastActiveDate = todayStr
            )
        )
    }
}
