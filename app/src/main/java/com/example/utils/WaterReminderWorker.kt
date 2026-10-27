package com.example.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.local.FitnessDatabase
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

class WaterReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val dao = FitnessDatabase.getDatabase(applicationContext).fitnessDao()
            val profile = dao.getUserProfile().firstOrNull()

            if (profile?.waterRemindersEnabled == true) {
                NotificationHelper.showWaterHydrationNotification(
                    context = applicationContext,
                    waterDrankLiters = profile.waterDrankLiters,
                    waterGoalLiters = profile.waterGoalLiters,
                    intervalHours = profile.waterReminderIntervalHours
                )
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "Water2HourReminderWork"

        fun scheduleWaterReminder(context: Context, intervalHours: Int = 2) {
            val validInterval = intervalHours.coerceIn(1, 12).toLong()
            val reminderRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
                validInterval, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                reminderRequest
            )
        }

        fun cancelWaterReminder(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
