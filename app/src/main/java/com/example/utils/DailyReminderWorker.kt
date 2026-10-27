package com.example.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.local.FitnessDatabase
import com.example.data.model.PermanentTask
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

class DailyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val dao = FitnessDatabase.getDatabase(applicationContext).fitnessDao()
            val profile = dao.getUserProfile().firstOrNull()

            if (profile?.habitNotificationsEnabled != false) {
                val todayStr = DateUtils.getTodayDateString()
                val allTaskRecords = dao.getAllDailyTaskRecords().firstOrNull() ?: emptyList()
                val todayRecords = allTaskRecords.filter { it.dateText == todayStr }

                val completedTaskIds = todayRecords.filter { it.isCompleted }.map { it.taskId }.toSet()
                val incompleteTasks = PermanentTask.ALL_PERMANENT_TASKS.filter { !completedTaskIds.contains(it.id) }

                if (incompleteTasks.isNotEmpty()) {
                    val primaryHabit = incompleteTasks.first()
                    NotificationHelper.showHabitReminderNotification(
                        context = applicationContext,
                        habitTitle = primaryHabit.title,
                        category = primaryHabit.category,
                        reminderTime = "Today",
                        isSecondReminder = false
                    )

                    if (incompleteTasks.size > 1) {
                        NotificationHelper.showIncompleteHabitsSummaryNotification(
                            context = applicationContext,
                            incompleteCount = incompleteTasks.size,
                            habitTitles = incompleteTasks.map { it.title }
                        )
                    } else {
                        NotificationHelper.showHabitReminderNotification(
                            context = applicationContext,
                            habitTitle = primaryHabit.title,
                            category = primaryHabit.category,
                            reminderTime = "Today",
                            isSecondReminder = true
                        )
                    }
                } else {
                    NotificationHelper.showReminderNotification(
                        context = applicationContext,
                        title = "🏆 10/10 Perfect Day!",
                        message = "All 10 permanent goals completed for today! Unstoppable discipline!"
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "DailyFitnessReminderWork"

        fun scheduleDailyReminder(context: Context) {
            val reminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
                4, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                reminderRequest
            )
        }

        fun cancelDailyReminder(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
