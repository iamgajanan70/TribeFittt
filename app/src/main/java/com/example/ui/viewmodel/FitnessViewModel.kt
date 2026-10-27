package com.example.data.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DailyStepRecordEntity
import com.example.data.local.DailyTaskRecordEntity
import com.example.data.local.DailyWaterRecordEntity
import com.example.data.local.FitnessDatabase
import com.example.data.local.NutritionMealEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WeightLogEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.NutritionSummary
import com.example.data.model.PermanentTask
import com.example.data.model.TaskType
import com.example.data.repository.FitnessRepository
import com.example.utils.DateUtils
import com.example.utils.NotificationHelper
import com.example.utils.WaterReminderWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FitnessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitnessRepository
    private val appContext: Context = application.applicationContext

    val userProfile: StateFlow<UserProfileEntity?>
    val workouts: StateFlow<List<WorkoutLogEntity>>
    val meals: StateFlow<List<NutritionMealEntity>>
    val weightLogs: StateFlow<List<WeightLogEntity>>
    val waterRecords: StateFlow<List<DailyWaterRecordEntity>>
    val stepRecords: StateFlow<List<DailyStepRecordEntity>>
    val allDailyTaskRecords: StateFlow<List<DailyTaskRecordEntity>>

    val selectedDate = MutableStateFlow(DateUtils.getTodayDateString())

    /**
     * The 10 Permanent Task records for the currently selected calendar date.
     * Always outputs exactly 10 records corresponding to the 10 permanent goals.
     */
    val currentDayTaskRecords: StateFlow<List<DailyTaskRecordEntity>>

    val nutritionSummary: StateFlow<NutritionSummary>

    // Study Timer State
    private val _isStudyTimerRunning = MutableStateFlow(false)
    val isStudyTimerRunning: StateFlow<Boolean> = _isStudyTimerRunning.asStateFlow()

    private val _studyTimerSeconds = MutableStateFlow(0)
    val studyTimerSeconds: StateFlow<Int> = _studyTimerSeconds.asStateFlow()
    private var studyTimerJob: Job? = null

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage.asStateFlow()

    init {
        val database = FitnessDatabase.getDatabase(application)
        repository = FitnessRepository(database.fitnessDao())

        // Ensure notification channels on startup
        NotificationHelper.ensureNotificationChannels(application.applicationContext)

        userProfile = repository.userProfile
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

        workouts = repository.workouts
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        meals = repository.meals
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        weightLogs = repository.weightLogs
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        waterRecords = repository.waterRecords
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        stepRecords = repository.stepRecords
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        allDailyTaskRecords = repository.allDailyTaskRecords
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Reactive 10 Permanent Task Records for the selected date
        currentDayTaskRecords = combine(
            allDailyTaskRecords,
            selectedDate
        ) { records, date ->
            val recordsForDate = records.filter { it.dateText == date }.associateBy { it.taskId }

            PermanentTask.ALL_PERMANENT_TASKS.map { def ->
                recordsForDate[def.id] ?: DailyTaskRecordEntity(
                    id = 0,
                    taskId = def.id,
                    dateText = date,
                    actualValue = 0.0f,
                    targetValue = def.targetValue,
                    isCompleted = false,
                    completionPercentage = 0.0f,
                    valueDisplay = getDefaultDisplayForTask(def),
                    extraData = "",
                    lastUpdatedTimestamp = 0L
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PermanentTask.ALL_PERMANENT_TASKS.map { def ->
                DailyTaskRecordEntity(
                    taskId = def.id,
                    dateText = DateUtils.getTodayDateString(),
                    actualValue = 0.0f,
                    targetValue = def.targetValue,
                    isCompleted = false,
                    completionPercentage = 0.0f,
                    valueDisplay = getDefaultDisplayForTask(def)
                )
            }
        )

        nutritionSummary = combine(meals, userProfile) { mealList, profile ->
            var cal = 0
            var pro = 0
            var car = 0
            var fat = 0
            var bfCal = 0
            var luCal = 0
            var diCal = 0
            var snCal = 0

            mealList.forEach { m ->
                cal += m.calories
                pro += m.proteinGrams
                car += m.carbsGrams
                fat += m.fatGrams

                when (m.mealType.lowercase()) {
                    "breakfast" -> bfCal += m.calories
                    "lunch" -> luCal += m.calories
                    "dinner" -> diCal += m.calories
                    else -> snCal += m.calories
                }
            }

            val targetCal = profile?.calorieGoal ?: 2000
            val targetPro = profile?.proteinGoal ?: 150
            val targetCar = profile?.carbGoal ?: 250
            val targetFat = profile?.fatGoal ?: 65

            NutritionSummary(
                totalCalories = cal,
                targetCalories = targetCal,
                caloriesLeft = (targetCal - cal).coerceAtLeast(0),
                totalProtein = pro,
                targetProtein = targetPro,
                totalCarbs = car,
                targetCarbs = targetCar,
                totalFat = fat,
                targetFat = targetFat,
                breakfastCalories = bfCal,
                lunchCalories = luCal,
                dinnerCalories = diCal,
                snackCalories = snCal
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NutritionSummary()
        )

        viewModelScope.launch {
            delay(300)
            _isLoading.value = false
        }
    }

    private fun getDefaultDisplayForTask(def: PermanentTask): String {
        return when (def.taskType) {
            TaskType.STEPS -> "0 / 10,000 steps"
            TaskType.WATER -> "0.0 / 3.0 L"
            TaskType.NO_JUNK_FOOD -> "Not Checked"
            TaskType.PROTEIN_MEALS -> "0/3 meals with protein"
            TaskType.SLEEP -> "0h / 6h"
            TaskType.WORKOUT -> "Pending"
            TaskType.STUDY -> "0h 0m / 2h 00m"
            TaskType.READING -> "0 / 10 pages"
            TaskType.BEDTIME -> "Target: < 10:00 PM"
            TaskType.WAKEUP -> "Target: < 04:30 AM"
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun selectDate(date: String) {
        selectedDate.value = date
    }

    fun goToToday() {
        selectedDate.value = DateUtils.getTodayDateString()
    }

    fun goToYesterday() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate.value = sdf.format(cal.time)
    }

    fun shiftDate(offsetDays: Int) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val current = sdf.parse(selectedDate.value) ?: return
            val cal = Calendar.getInstance()
            cal.time = current
            cal.add(Calendar.DAY_OF_YEAR, offsetDays)
            selectedDate.value = sdf.format(cal.time)
        } catch (e: Exception) {
            // ignore
        }
    }

    // 1. Task: 10K Steps
    fun addSteps(deltaSteps: Int, date: String = selectedDate.value) {
        if (deltaSteps <= 0) return
        viewModelScope.launch {
            val currentRec = currentDayTaskRecords.value.find { it.taskId == PermanentTask.ID_STEPS && it.dateText == date }
            val currentVal = currentRec?.actualValue ?: 0f
            val newVal = currentVal + deltaSteps
            repository.updateTaskProgress(PermanentTask.ID_STEPS, date, newVal)

            // If today, also update User Profile & DailyStepRecord
            if (date == DateUtils.getTodayDateString()) {
                repository.addSteps(deltaSteps, 10000, date)
                val profile = userProfile.value ?: UserProfileEntity()
                val updatedSteps = profile.currentSteps + deltaSteps
                val calBurn = profile.caloriesBurnedToday + (deltaSteps * 0.04f).toInt()
                repository.updateUserProfile(profile.copy(currentSteps = updatedSteps, caloriesBurnedToday = calBurn))
            }
            _statusMessage.value = "+$deltaSteps Steps recorded for ${DateUtils.formatShortDisplay(date)}"
        }
    }

    fun setSteps(totalSteps: Int, date: String = selectedDate.value) {
        viewModelScope.launch {
            val validSteps = totalSteps.coerceAtLeast(0).toFloat()
            repository.updateTaskProgress(PermanentTask.ID_STEPS, date, validSteps)
            _statusMessage.value = "Steps set to $totalSteps for ${DateUtils.formatShortDisplay(date)}"
        }
    }

    // 2. Task: 3L+ Water Intake
    fun addWater(liters: Float, date: String = selectedDate.value) {
        viewModelScope.launch {
            val currentRec = currentDayTaskRecords.value.find { it.taskId == PermanentTask.ID_WATER && it.dateText == date }
            val currentVal = currentRec?.actualValue ?: 0f
            val newVal = Math.max(0.0f, currentVal + liters)
            val rounded = Math.round(newVal * 10f) / 10f
            repository.updateTaskProgress(PermanentTask.ID_WATER, date, rounded)

            if (date == DateUtils.getTodayDateString()) {
                repository.addWater(liters, 3.0f, date)
                val profile = userProfile.value ?: UserProfileEntity()
                repository.updateUserProfile(profile.copy(waterDrankLiters = rounded))
            }
            _statusMessage.value = "Hydration: +${String.format("%.2f", liters)}L (Total: ${String.format("%.1f", rounded)}L)"
        }
    }

    // 3. Task: No Junk Food
    fun toggleNoJunkFood(date: String = selectedDate.value) {
        viewModelScope.launch {
            val currentRec = currentDayTaskRecords.value.find { it.taskId == PermanentTask.ID_NO_JUNK_FOOD && it.dateText == date }
            val wasCompleted = currentRec?.isCompleted ?: false
            val newVal = if (wasCompleted) 0.0f else 1.0f
            repository.updateTaskProgress(PermanentTask.ID_NO_JUNK_FOOD, date, newVal)
            _statusMessage.value = if (!wasCompleted) "No Junk Food checked! Clean diet victory 🥗" else "No Junk Food uncompleted"
        }
    }

    // 4. Task: Protein With Every Meal
    fun toggleProteinMeal(mealName: String, date: String = selectedDate.value) {
        viewModelScope.launch {
            val currentRec = currentDayTaskRecords.value.find { it.taskId == PermanentTask.ID_PROTEIN_MEALS && it.dateText == date }
            val extra = currentRec?.extraData ?: ""
            val activeMeals = extra.split(",").map { it.trim() }.filter { it.isNotBlank() }.toMutableSet()

            if (activeMeals.contains(mealName)) {
                activeMeals.remove(mealName)
            } else {
                activeMeals.add(mealName)
            }

            val newCount = activeMeals.size.toFloat()
            val newExtra = activeMeals.joinToString(",")
            repository.updateTaskProgress(PermanentTask.ID_PROTEIN_MEALS, date, newCount, newExtra)
            _statusMessage.value = "Protein tracked for $mealName (${activeMeals.size}/3 meals)"
        }
    }

    // 5. Task: 6+ Hours Sleep
    fun logSleepHours(hours: Float, date: String = selectedDate.value) {
        viewModelScope.launch {
            val validH = hours.coerceIn(0f, 24f)
            repository.updateTaskProgress(PermanentTask.ID_SLEEP, date, validH)
            if (date == DateUtils.getTodayDateString()) {
                val profile = userProfile.value ?: UserProfileEntity()
                repository.updateUserProfile(profile.copy(actualSleepHours = validH))
            }
            _statusMessage.value = "Sleep logged: ${String.format("%.1f", validH)} hours"
        }
    }

    // 6. Task: Workout
    fun toggleWorkout(date: String = selectedDate.value) {
        viewModelScope.launch {
            val currentRec = currentDayTaskRecords.value.find { it.taskId == PermanentTask.ID_WORKOUT && it.dateText == date }
            val wasCompleted = currentRec?.isCompleted ?: false
            val newVal = if (wasCompleted) 0.0f else 1.0f
            repository.updateTaskProgress(PermanentTask.ID_WORKOUT, date, newVal, if (newVal > 0) "Daily Workout" else "")
            _statusMessage.value = if (!wasCompleted) "Daily Workout marked complete! 💪" else "Workout status pending"
        }
    }

    fun addWorkout(
        title: String,
        durationMinutes: Int,
        exerciseCount: Int,
        exercisesText: String,
        status: String = "Completed",
        dateText: String = selectedDate.value
    ) {
        viewModelScope.launch {
            repository.addWorkout(
                WorkoutLogEntity(
                    title = title,
                    durationMinutes = durationMinutes,
                    exerciseCount = exerciseCount,
                    exercisesText = exercisesText,
                    status = status,
                    dateText = dateText
                )
            )
            repository.updateTaskProgress(PermanentTask.ID_WORKOUT, dateText, 1.0f, title)
            _statusMessage.value = "Workout logged & Goal completed!"
        }
    }

    // 7. Task: 2+ Hours Study
    fun addStudyMinutes(minutes: Int, date: String = selectedDate.value) {
        viewModelScope.launch {
            val currentRec = currentDayTaskRecords.value.find { it.taskId == PermanentTask.ID_STUDY && it.dateText == date }
            val currentMins = currentRec?.actualValue ?: 0f
            val newMins = (currentMins + minutes).coerceAtLeast(0f)
            repository.updateTaskProgress(PermanentTask.ID_STUDY, date, newMins)
            val studyH = newMins.toInt() / 60
            val studyM = newMins.toInt() % 60
            _statusMessage.value = "Study logged: +${minutes}m (Total: ${studyH}h ${studyM}m / 2h)"
        }
    }

    fun startStudyTimer() {
        if (_isStudyTimerRunning.value) return
        _isStudyTimerRunning.value = true
        studyTimerJob = viewModelScope.launch {
            while (_isStudyTimerRunning.value) {
                delay(1000)
                _studyTimerSeconds.value += 1
            }
        }
    }

    fun pauseStudyTimer() {
        _isStudyTimerRunning.value = false
        studyTimerJob?.cancel()
    }

    fun saveStudyTimer(date: String = selectedDate.value) {
        val totalSecs = _studyTimerSeconds.value
        val mins = (totalSecs / 60).coerceAtLeast(1)
        addStudyMinutes(mins, date)
        _isStudyTimerRunning.value = false
        studyTimerJob?.cancel()
        _studyTimerSeconds.value = 0
    }

    // 8. Task: Read 10 Pages
    fun addReadingPages(pages: Int, date: String = selectedDate.value) {
        viewModelScope.launch {
            val currentRec = currentDayTaskRecords.value.find { it.taskId == PermanentTask.ID_READING && it.dateText == date }
            val currentPages = currentRec?.actualValue ?: 0f
            val newPages = (currentPages + pages).coerceAtLeast(0f)
            repository.updateTaskProgress(PermanentTask.ID_READING, date, newPages)
            if (date == DateUtils.getTodayDateString()) {
                val profile = userProfile.value ?: UserProfileEntity()
                repository.updateUserProfile(profile.copy(pagesReadToday = newPages.toInt()))
            }
            _statusMessage.value = "Reading logged: +$pages pages (Total: ${newPages.toInt()}/10 pages)"
        }
    }

    // 9. Task: Bedtime Before 10:00 PM
    fun logBedtime(timeStr: String, date: String = selectedDate.value) {
        // e.g. "09:48 PM" or "21:48" or "10:15 PM"
        val decimalHour = parseTimeToDecimalHour(timeStr)
        viewModelScope.launch {
            repository.updateTaskProgress(PermanentTask.ID_BEDTIME, date, decimalHour, timeStr)
            val isSuccess = decimalHour in 0.1f..22.0f
            if (date == DateUtils.getTodayDateString()) {
                val profile = userProfile.value ?: UserProfileEntity()
                repository.updateUserProfile(profile.copy(sleepTime = timeStr))
            }
            _statusMessage.value = if (isSuccess) "Bedtime $timeStr logged! Goal Achieved (Before 10 PM) 🌙" else "Bedtime $timeStr logged (Missed < 10 PM target)"
        }
    }

    // 10. Task: Wake Up Before 4:30 AM
    fun logWakeUpTime(timeStr: String, date: String = selectedDate.value) {
        // e.g. "04:18 AM" or "05:15 AM"
        val decimalHour = parseTimeToDecimalHour(timeStr)
        viewModelScope.launch {
            repository.updateTaskProgress(PermanentTask.ID_WAKEUP, date, decimalHour, timeStr)
            val isSuccess = decimalHour in 0.1f..4.5f
            if (date == DateUtils.getTodayDateString()) {
                val profile = userProfile.value ?: UserProfileEntity()
                repository.updateUserProfile(profile.copy(wakeUpTime = timeStr))
            }
            _statusMessage.value = if (isSuccess) "Wake-up $timeStr logged! Discipline Champion (Before 4:30 AM) ☀️" else "Wake-up $timeStr logged (Missed < 4:30 AM target)"
        }
    }

    private fun parseTimeToDecimalHour(timeStr: String): Float {
        return try {
            val upper = timeStr.trim().uppercase()
            val isPM = upper.endsWith("PM")
            val isAM = upper.endsWith("AM")
            val clean = upper.replace("AM", "").replace("PM", "").trim()
            val parts = clean.split(":")
            var h = parts[0].toInt()
            val m = if (parts.size > 1) parts[1].toInt() else 0

            if (isPM && h < 12) h += 12
            if (isAM && h == 12) h = 0

            h.toFloat() + (m.toFloat() / 60f)
        } catch (e: Exception) {
            0.0f
        }
    }

    fun updateGoals(
        stepGoal: Int,
        waterGoalLiters: Float,
        calorieGoal: Int,
        calorieBurnGoal: Int,
        sleepHoursGoal: Float,
        dailyReadingPagesGoal: Int
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val updated = current.copy(
                stepGoal = stepGoal,
                waterGoalLiters = waterGoalLiters,
                calorieGoal = calorieGoal,
                calorieBurnGoal = calorieBurnGoal,
                sleepHoursGoal = sleepHoursGoal,
                dailyReadingPagesGoal = dailyReadingPagesGoal
            )
            repository.updateUserProfile(updated)
            _statusMessage.value = "System targets updated."
        }
    }

    fun triggerTestHabitReminder(isSecondReminder: Boolean = false) {
        val incomplete = currentDayTaskRecords.value.filter { !it.isCompleted }
        val target = incomplete.firstOrNull() ?: currentDayTaskRecords.value.firstOrNull()
        if (target != null) {
            val def = PermanentTask.getById(target.taskId)
            val title = def?.title ?: target.taskId
            val cat = def?.category ?: "Protocol"
            NotificationHelper.showHabitReminderNotification(
                context = appContext,
                habitTitle = title,
                category = cat,
                reminderTime = "Today",
                isSecondReminder = isSecondReminder
            )
            _statusMessage.value = if (isSecondReminder) "2X Reminder Sent for $title" else "Reminder Sent for $title"
        }
    }

    fun toggleFocusMode() {
        _statusMessage.value = "Focus Mode Activated: Distractions muted."
    }

    // General app operations
    fun toggleDarkMode() {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val newDarkMode = !current.isDarkMode
            repository.updateUserProfile(current.copy(isDarkMode = newDarkMode))
            _statusMessage.value = if (newDarkMode) "Dark Theme Applied" else "Light Theme Applied"
        }
    }

    fun toggleWaterReminders() {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val newState = !current.waterRemindersEnabled
            repository.updateUserProfile(current.copy(waterRemindersEnabled = newState))
            if (newState) {
                WaterReminderWorker.scheduleWaterReminder(appContext, current.waterReminderIntervalHours)
                _statusMessage.value = "Water Reminders Enabled"
            } else {
                WaterReminderWorker.cancelWaterReminder(appContext)
                _statusMessage.value = "Water Reminders Paused"
            }
        }
    }

    fun setWaterReminderInterval(intervalHours: Int) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateUserProfile(current.copy(waterReminderIntervalHours = intervalHours))
            if (current.waterRemindersEnabled) {
                WaterReminderWorker.scheduleWaterReminder(appContext, intervalHours)
            }
            _statusMessage.value = "Water reminder interval set to every $intervalHours hours."
        }
    }

    fun triggerTestWaterReminder() {
        val current = userProfile.value ?: UserProfileEntity()
        NotificationHelper.showWaterHydrationNotification(
            context = appContext,
            waterDrankLiters = current.waterDrankLiters,
            waterGoalLiters = current.waterGoalLiters,
            intervalHours = current.waterReminderIntervalHours
        )
        _statusMessage.value = "Water Reminder Notification triggered!"
    }

    fun triggerIncompleteHabitsDoubleReminder() {
        val incomplete = currentDayTaskRecords.value.filter { !it.isCompleted }
        if (incomplete.isNotEmpty()) {
            val task = incomplete.first()
            val def = PermanentTask.getById(task.taskId) ?: return
            NotificationHelper.showHabitReminderNotification(
                context = appContext,
                habitTitle = def.title,
                category = def.category,
                reminderTime = "Today",
                isSecondReminder = false
            )
            NotificationHelper.showHabitReminderNotification(
                context = appContext,
                habitTitle = def.title,
                category = def.category,
                reminderTime = "Today",
                isSecondReminder = true
            )
            _statusMessage.value = "⚠️ 2X Urgent Reminder sent for pending goal: '${def.title}'"
        } else {
            NotificationHelper.showReminderNotification(
                context = appContext,
                title = "⭐ 10/10 Permanent Goals Crushed!",
                message = "All 10 permanent goals completed for today! Unstoppable consistency!"
            )
            _statusMessage.value = "All 10 goals already completed for today!"
        }
    }

    fun triggerTestNotification() {
        val completedCount = currentDayTaskRecords.value.count { it.isCompleted }
        NotificationHelper.showReminderNotification(
            context = appContext,
            title = "🏆 10 Permanent Goals Daily Check-In",
            message = "$completedCount/10 goals completed today. Keep pushing!"
        )
        _statusMessage.value = "Test Notification Sent!"
    }

    fun addMeal(
        mealType: String,
        foodName: String,
        calories: Int,
        proteinGrams: Int,
        carbsGrams: Int,
        fatGrams: Int
    ) {
        viewModelScope.launch {
            repository.addMeal(
                NutritionMealEntity(
                    mealType = mealType,
                    foodName = foodName,
                    calories = calories,
                    proteinGrams = proteinGrams,
                    carbsGrams = carbsGrams,
                    fatGrams = fatGrams
                )
            )
            // If meal has substantial protein, track protein meal goal
            if (proteinGrams >= 15) {
                toggleProteinMeal(mealType)
            }
            _statusMessage.value = "Meal added to $mealType."
        }
    }

    fun deleteMeal(mealId: Long) {
        viewModelScope.launch {
            repository.deleteMeal(mealId)
            _statusMessage.value = "Meal record deleted."
        }
    }

    fun deleteWorkout(workoutId: Long) {
        viewModelScope.launch {
            repository.deleteWorkout(workoutId)
            _statusMessage.value = "Workout record deleted."
        }
    }

    fun addWeightLog(weightKg: Float) {
        viewModelScope.launch {
            repository.addWeightLog(weightKg)
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateUserProfile(current.copy(weightKg = weightKg.toInt()))
            _statusMessage.value = "Weight logged: $weightKg kg."
        }
    }

    fun updateProfile(
        fullName: String,
        email: String,
        username: String,
        age: Int,
        gender: String,
        heightCm: Int,
        weightKg: Int
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val updated = current.copy(
                fullName = fullName.trim(),
                email = email.trim(),
                username = username.trim(),
                age = age,
                gender = gender,
                heightCm = heightCm,
                weightKg = weightKg
            )
            repository.updateUserProfile(updated)
            _statusMessage.value = "Profile updated successfully."
        }
    }

    fun updateSleepSchedule(
        wakeUpTime: String,
        sleepTime: String,
        sleepHoursGoal: Float,
        actualSleepHours: Float
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val updated = current.copy(
                wakeUpTime = wakeUpTime.trim(),
                sleepTime = sleepTime.trim(),
                sleepHoursGoal = sleepHoursGoal,
                actualSleepHours = actualSleepHours
            )
            repository.updateUserProfile(updated)
            logBedtime(sleepTime)
            logWakeUpTime(wakeUpTime)
            logSleepHours(actualSleepHours)
            _statusMessage.value = "Sleep schedule & goals updated."
        }
    }

    fun exportDataToCsv(context: Context) {
        viewModelScope.launch {
            val result = com.example.utils.CsvExporter.exportAllDataToCsv(
                context = context,
                workouts = workouts.value,
                meals = meals.value,
                weightLogs = weightLogs.value,
                dailyTaskRecords = allDailyTaskRecords.value,
                waterRecords = waterRecords.value,
                stepRecords = stepRecords.value
            )
            if (result.isNotEmpty()) {
                _statusMessage.value = "Exported CSV files to Downloads folder!"
            }
        }
    }

    fun resetAllLocalData() {
        viewModelScope.launch {
            repository.resetAllData()
            _statusMessage.value = "Application reset! All records are set to clean initial state."
        }
    }

    fun logOut() {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateUserProfile(current.copy(isLoggedIn = false))
        }
    }

    fun logIn(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val cleanEmail = email.trim()
            val cleanPass = pass.trim()

            if (cleanEmail.isBlank() || cleanPass.isBlank()) {
                _statusMessage.value = "Please enter both Email and Password."
                return@launch
            }

            val current = userProfile.value ?: UserProfileEntity()
            val displayName = if (current.email.equals(cleanEmail, ignoreCase = true) && current.fullName.isNotBlank()) {
                current.fullName
            } else {
                cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            }

            val updated = current.copy(
                email = cleanEmail,
                password = cleanPass,
                fullName = displayName,
                username = current.username.ifBlank { displayName.lowercase().replace(" ", "_") },
                isLoggedIn = true
            )
            repository.updateUserProfile(updated)
            _statusMessage.value = "Logged in."
            onSuccess()
        }
    }

    fun signUp(
        fullName: String,
        email: String,
        pass: String,
        age: Int,
        gender: String,
        heightCm: Int,
        weightKg: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val cleanName = fullName.trim()
            val cleanEmail = email.trim()
            val cleanPass = pass.trim()

            if (cleanName.isBlank() || cleanEmail.isBlank() || cleanPass.isBlank()) {
                _statusMessage.value = "Please enter your Full Name, Email, and Password."
                return@launch
            }

            val generatedUsername = cleanName.lowercase().replace(" ", "_")
            val current = userProfile.value ?: UserProfileEntity()
            val updated = current.copy(
                fullName = cleanName,
                email = cleanEmail,
                password = cleanPass,
                username = generatedUsername,
                age = age,
                gender = gender,
                heightCm = heightCm,
                weightKg = weightKg,
                isLoggedIn = true
            )

            repository.updateUserProfile(updated)
            if (weightKg > 0) {
                repository.addWeightLog(weightKg.toFloat())
            }
            _statusMessage.value = "Welcome, $cleanName! Your 10 Permanent Goals are ready."
            onSuccess()
        }
    }

    fun uploadAvatarBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _isUploadingImage.value = true
            try {
                val scaledWidth: Int
                val scaledHeight: Int
                if (bitmap.width > bitmap.height) {
                    scaledWidth = 320
                    scaledHeight = ((320f / bitmap.width) * bitmap.height).toInt().coerceAtLeast(1)
                } else {
                    scaledHeight = 320
                    scaledWidth = ((320f / bitmap.height) * bitmap.width).toInt().coerceAtLeast(1)
                }
                val resized = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)

                val avatarFile = File(appContext.filesDir, "user_avatar_${System.currentTimeMillis()}.jpg")
                FileOutputStream(avatarFile).use { out ->
                    resized.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }

                val current = userProfile.value ?: UserProfileEntity()
                val updated = current.copy(avatarUrl = avatarFile.absolutePath)
                repository.updateUserProfile(updated)

                _statusMessage.value = "Profile photo updated successfully!"
            } catch (e: Exception) {
                _statusMessage.value = "Image processing error: ${e.localizedMessage}"
            } finally {
                _isUploadingImage.value = false
            }
        }
    }
}
