package com.example.utils

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.data.local.DailyStepRecordEntity
import com.example.data.local.DailyTaskRecordEntity
import com.example.data.local.FitnessDatabase
import com.example.data.model.PermanentTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class StepTrackingService : Service(), SensorEventListener {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var sensorManager: SensorManager
    private lateinit var prefs: SharedPreferences

    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null

    // Accelerometer peak detection variables
    private var lastAccelMagnitude = 9.8f
    private var lastStepTimestampMs = 0L
    private val accelStepThreshold = 11.6f
    private val minStepIntervalMs = 280L // Max ~3.5 steps/sec (running cadence)

    private var hasHardwareStepCounter = false
    private var lastDbWriteTime = 0L

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        NotificationHelper.ensureNotificationChannels(this)
        val initialSteps = getTodayPersistedSteps()
        _liveSteps.value = initialSteps

        val notification = NotificationHelper.buildTrackerNotification(
            context = this,
            steps = initialSteps,
            stepGoal = 10000,
            completedGoals = 0,
            totalGoals = 10
        )
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NotificationHelper.TRACKER_NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                )
            } else {
                startForeground(NotificationHelper.TRACKER_NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting foreground service: ${e.message}")
        }

        initSensors()
        syncWithDatabase()
    }

    private fun initSensors() {
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (stepCounterSensor != null) {
            hasHardwareStepCounter = true
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
            Log.d(TAG, "Registered Hardware TYPE_STEP_COUNTER")
        }

        if (stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI)
            Log.d(TAG, "Registered Hardware TYPE_STEP_DETECTOR")
        }

        // If hardware counter is absent, register accelerometer fallback for motion tracking
        if (stepCounterSensor == null && accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
            Log.d(TAG, "Registered Accelerometer Motion Step Fallback")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val currentSteps = getTodayPersistedSteps()
        _liveSteps.value = currentSteps
        updateNotificationAndDb(currentSteps, forceDb = true)
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                handleHardwareStepCounter(event.values[0].toInt())
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                if (!hasHardwareStepCounter) {
                    handleIncrementalStep()
                }
            }
            Sensor.TYPE_ACCELEROMETER -> {
                if (!hasHardwareStepCounter && stepDetectorSensor == null) {
                    handleAccelerometerStep(event.values[0], event.values[1], event.values[2])
                }
            }
        }
    }

    private fun handleHardwareStepCounter(rawBootSteps: Int) {
        val today = DateUtils.getTodayDateString()
        val savedDate = prefs.getString(KEY_DATE, "") ?: ""
        val savedBaseline = prefs.getInt(KEY_BASELINE_BOOT_STEPS, -1)
        val savedLastBootSteps = prefs.getInt(KEY_LAST_BOOT_STEPS, rawBootSteps)
        val savedOffset = prefs.getInt(KEY_OFFSET_STEPS, 0)

        val newBaseline: Int
        val newOffset: Int

        if (savedDate != today || savedBaseline < 0) {
            // New day rollover
            newBaseline = rawBootSteps
            newOffset = 0
            prefs.edit()
                .putString(KEY_DATE, today)
                .putInt(KEY_BASELINE_BOOT_STEPS, newBaseline)
                .putInt(KEY_OFFSET_STEPS, 0)
                .putInt(KEY_LAST_BOOT_STEPS, rawBootSteps)
                .apply()
        } else if (rawBootSteps < savedLastBootSteps) {
            // Phone rebooted mid-day: hardware step counter reset to 0
            val priorStepsToday = (savedLastBootSteps - savedBaseline).coerceAtLeast(0) + savedOffset
            newBaseline = 0
            newOffset = priorStepsToday
            prefs.edit()
                .putInt(KEY_BASELINE_BOOT_STEPS, 0)
                .putInt(KEY_OFFSET_STEPS, priorStepsToday)
                .putInt(KEY_LAST_BOOT_STEPS, rawBootSteps)
                .apply()
        } else {
            newBaseline = savedBaseline
            newOffset = savedOffset
            prefs.edit().putInt(KEY_LAST_BOOT_STEPS, rawBootSteps).apply()
        }

        val calculatedSteps = (rawBootSteps - newBaseline).coerceAtLeast(0) + newOffset
        _liveSteps.value = calculatedSteps
        prefs.edit().putInt(KEY_TODAY_STEPS, calculatedSteps).apply()

        val now = System.currentTimeMillis()
        if (now - lastDbWriteTime > 4000) {
            updateNotificationAndDb(calculatedSteps, forceDb = false)
            lastDbWriteTime = now
        }
    }

    private fun handleIncrementalStep() {
        val today = DateUtils.getTodayDateString()
        val savedDate = prefs.getString(KEY_DATE, "") ?: ""
        val currentSteps = if (savedDate == today) prefs.getInt(KEY_TODAY_STEPS, 0) else 0

        val newSteps = currentSteps + 1
        _liveSteps.value = newSteps
        prefs.edit()
            .putString(KEY_DATE, today)
            .putInt(KEY_TODAY_STEPS, newSteps)
            .apply()

        val now = System.currentTimeMillis()
        if (now - lastDbWriteTime > 4000) {
            updateNotificationAndDb(newSteps, forceDb = false)
            lastDbWriteTime = now
        }
    }

    private fun handleAccelerometerStep(x: Float, y: Float, z: Float) {
        val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val now = System.currentTimeMillis()

        // Check if movement passed threshold and adequate debounce time elapsed
        if (magnitude > accelStepThreshold && lastAccelMagnitude <= accelStepThreshold) {
            if (now - lastStepTimestampMs > minStepIntervalMs) {
                lastStepTimestampMs = now
                handleIncrementalStep()
            }
        }
        lastAccelMagnitude = magnitude
    }

    private fun updateNotificationAndDb(stepCount: Int, forceDb: Boolean = false) {
        serviceScope.launch {
            try {
                val today = DateUtils.getTodayDateString()
                val dao = FitnessDatabase.getDatabase(applicationContext).fitnessDao()
                val profile = dao.getUserProfile().firstOrNull()
                val goal = profile?.stepGoal ?: 10000

                val taskRecords = dao.getAllDailyTaskRecords().firstOrNull() ?: emptyList()
                val todayRecords = taskRecords.filter { it.dateText == today }
                val completedGoals = todayRecords.count { it.isCompleted }

                NotificationHelper.updateTrackerNotification(
                    context = applicationContext,
                    steps = stepCount,
                    stepGoal = goal,
                    completedGoals = completedGoals,
                    totalGoals = 10
                )

                // Persist step record
                dao.insertOrUpdateStepRecord(
                    DailyStepRecordEntity(
                        dateText = today,
                        stepCount = stepCount,
                        stepGoal = goal,
                        lastUpdatedTimestamp = System.currentTimeMillis()
                    )
                )

                // Persist 10 permanent goals Step Record
                val isCompleted = stepCount >= goal
                val pct = if (goal > 0) ((stepCount.toFloat() / goal) * 100).toInt().coerceIn(0, 100) else 0
                val display = "$stepCount / $goal steps"

                dao.insertOrUpdateDailyTaskRecord(
                    DailyTaskRecordEntity(
                        taskId = PermanentTask.ID_STEPS,
                        dateText = today,
                        actualValue = stepCount.toFloat(),
                        targetValue = goal.toFloat(),
                        isCompleted = isCompleted,
                        completionPercentage = pct.toFloat(),
                        valueDisplay = display,
                        extraData = if (isCompleted) "Target Achieved" else "$pct%",
                        lastUpdatedTimestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error persisting steps to database: ${e.message}")
            }
        }
    }

    private fun syncWithDatabase() {
        serviceScope.launch {
            try {
                val today = DateUtils.getTodayDateString()
                val dao = FitnessDatabase.getDatabase(applicationContext).fitnessDao()
                val existingRecord = dao.getStepRecordForDateDirect(1L, today)
                if (existingRecord != null && existingRecord.stepCount > _liveSteps.value) {
                    _liveSteps.value = existingRecord.stepCount
                    prefs.edit()
                        .putString(KEY_DATE, today)
                        .putInt(KEY_TODAY_STEPS, existingRecord.stepCount)
                        .apply()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing initial steps: ${e.message}")
            }
        }
    }

    private fun getTodayPersistedSteps(): Int {
        val today = DateUtils.getTodayDateString()
        val savedDate = prefs.getString(KEY_DATE, "") ?: ""
        return if (savedDate == today) prefs.getInt(KEY_TODAY_STEPS, 0) else 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceScope.cancel()
    }

    companion object {
        private const val TAG = "StepTrackingService"
        private const val PREFS_NAME = "tribefit_step_prefs"
        private const val KEY_DATE = "pref_date"
        private const val KEY_BASELINE_BOOT_STEPS = "pref_baseline_boot"
        private const val KEY_LAST_BOOT_STEPS = "pref_last_boot"
        private const val KEY_OFFSET_STEPS = "pref_offset_steps"
        private const val KEY_TODAY_STEPS = "pref_today_steps"

        private val _liveSteps = MutableStateFlow(0)
        val liveSteps: StateFlow<Int> = _liveSteps.asStateFlow()

        fun startTracking(context: Context) {
            try {
                val intent = Intent(context, StepTrackingService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start StepTrackingService: ${e.message}")
            }
        }

        fun stopTracking(context: Context) {
            try {
                val intent = Intent(context, StepTrackingService::class.java)
                context.stopService(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop StepTrackingService: ${e.message}")
            }
        }

        fun addManualSteps(context: Context, delta: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val today = DateUtils.getTodayDateString()
            val current = if (prefs.getString(KEY_DATE, "") == today) prefs.getInt(KEY_TODAY_STEPS, 0) else 0
            val newSteps = (current + delta).coerceAtLeast(0)
            prefs.edit()
                .putString(KEY_DATE, today)
                .putInt(KEY_TODAY_STEPS, newSteps)
                .apply()
            _liveSteps.value = newSteps
            startTracking(context)
        }
    }
}
