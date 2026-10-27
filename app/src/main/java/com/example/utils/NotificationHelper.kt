package com.example.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

object NotificationHelper {
    const val CHANNEL_ID_TRACKER = "tribefit_background_tracker"
    const val CHANNEL_NAME_TRACKER = "🏃 TribeFit Continuous Step & Activity Tracking"

    const val CHANNEL_ID_WATER = "tribefit_water_reminders"
    const val CHANNEL_NAME_WATER = "💧 Water & Hydration (2-Hour Reminders)"

    const val CHANNEL_ID_HABITS = "tribefit_habit_reminders"
    const val CHANNEL_NAME_HABITS = "⭐ Daily Habits & Tasks Reminders"

    const val CHANNEL_ID_HABITS_URGENT = "tribefit_habit_urgent_2x"
    const val CHANNEL_NAME_HABITS_URGENT = "⚠️ 2X Incomplete Habit Streak Alerts"

    const val CHANNEL_ID_WORKOUTS = "tribefit_workout_reminders"
    const val CHANNEL_NAME_WORKOUTS = "🏋️ Workout & Fitness Reminders"

    const val TRACKER_NOTIFICATION_ID = 9001

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    fun ensureNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            // 1. Background Step Tracking Channel (Low/Silent so it doesn't beep on every step)
            val trackerChannel = NotificationChannel(
                CHANNEL_ID_TRACKER,
                CHANNEL_NAME_TRACKER,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps TribeFit tracking your physical steps, movement, and daily goals in real time continuously in the background."
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // 2. Water Channel
            val waterChannel = NotificationChannel(
                CHANNEL_ID_WATER,
                CHANNEL_NAME_WATER,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Periodic 2-hour hydration alerts to keep you on track with your water goal."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // 3. Habits Channel
            val habitChannel = NotificationChannel(
                CHANNEL_ID_HABITS,
                CHANNEL_NAME_HABITS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Scheduled reminders for your daily habits and tasks."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 200)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // 4. Urgent 2X Habit Channel
            val habitUrgentChannel = NotificationChannel(
                CHANNEL_ID_HABITS_URGENT,
                CHANNEL_NAME_HABITS_URGENT,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent 2X daily reminder for incomplete habits and tasks to maintain streak."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300, 150, 400)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // 5. Workout Channel
            val workoutChannel = NotificationChannel(
                CHANNEL_ID_WORKOUTS,
                CHANNEL_NAME_WORKOUTS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for scheduled workouts and daily movement goals."
                enableVibration(true)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            notificationManager.createNotificationChannel(trackerChannel)
            notificationManager.createNotificationChannel(waterChannel)
            notificationManager.createNotificationChannel(habitChannel)
            notificationManager.createNotificationChannel(habitUrgentChannel)
            notificationManager.createNotificationChannel(workoutChannel)
        }
    }

    fun buildTrackerNotification(
        context: Context,
        steps: Int,
        stepGoal: Int = 10000,
        completedGoals: Int = 0,
        totalGoals: Int = 10
    ): Notification {
        ensureNotificationChannels(context)

        val percent = if (stepGoal > 0) ((steps.toFloat() / stepGoal) * 100).toInt().coerceIn(0, 100) else 0
        val title = "🏃 $steps / $stepGoal Steps ($percent%)"
        val message = "TribeFit Background Service • $completedGoals/$totalGoals Goals Completed Today"

        return NotificationCompat.Builder(context, CHANNEL_ID_TRACKER)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "$title\n$message\nContinuous physical motion & health tracking active."
                )
            )
            .setProgress(stepGoal, steps.coerceAtMost(stepGoal), false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(getPendingIntent(context))
            .build()
    }

    fun updateTrackerNotification(
        context: Context,
        steps: Int,
        stepGoal: Int = 10000,
        completedGoals: Int = 0,
        totalGoals: Int = 10
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            TRACKER_NOTIFICATION_ID,
            buildTrackerNotification(context, steps, stepGoal, completedGoals, totalGoals)
        )
    }

    fun showWaterHydrationNotification(
        context: Context,
        waterDrankLiters: Float,
        waterGoalLiters: Float,
        intervalHours: Int = 2
    ) {
        ensureNotificationChannels(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val litersFormatted = String.format("%.2f", waterDrankLiters)
        val goalFormatted = String.format("%.1f", waterGoalLiters)
        val percent = if (waterGoalLiters > 0) ((waterDrankLiters / waterGoalLiters) * 100).toInt() else 0

        val title = "💧 Hydration Time! ($intervalHours-Hour Alert)"
        val message = "Logged: ${litersFormatted}L / ${goalFormatted}L today ($percent%). Drink 300-500ml now to stay hydrated and energized!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_WATER)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$message\n\n💡 Health Tip: Proper hydration boosts brain focus, muscle recovery, and stamina.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(getPendingIntent(context))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }

    fun showHabitReminderNotification(
        context: Context,
        habitTitle: String,
        category: String,
        reminderTime: String? = null,
        isSecondReminder: Boolean = false
    ) {
        ensureNotificationChannels(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = if (isSecondReminder) CHANNEL_ID_HABITS_URGENT else CHANNEL_ID_HABITS
        val timeStr = if (!reminderTime.isNullOrBlank()) " • $reminderTime" else ""

        val title = if (isSecondReminder) {
            "⚠️ 2X REMINDER: '$habitTitle' Pending!"
        } else {
            "⭐ Habit Reminder: $habitTitle"
        }

        val message = if (isSecondReminder) {
            "You haven't completed '$habitTitle' ($category) yet today! Tap to check it off and protect your streak!"
        } else {
            "Time to complete '$habitTitle' ($category$timeStr). Maintain your daily streak!"
        }

        val notificationId = (2000 + (habitTitle.hashCode() % 1000)).coerceAtLeast(2000) + if (isSecondReminder) 5000 else 0

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(if (isSecondReminder) android.R.drawable.ic_dialog_alert else android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        if (isSecondReminder) {
                            "$message\n\n🔥 2X Daily Pending Alert: Habit consistency is the foundation of high performance. Check it off now!"
                        } else {
                            "$message\n\n🎯 Consistency is what transforms average into excellence."
                        }
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(getPendingIntent(context))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun showIncompleteHabitsSummaryNotification(
        context: Context,
        incompleteCount: Int,
        habitTitles: List<String>
    ) {
        ensureNotificationChannels(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val title = "⚠️ 2X REMINDER: $incompleteCount Habits Still Pending!"
        val habitsText = habitTitles.take(4).joinToString(", ") { "• $it" } + if (habitTitles.size > 4) " (+${habitTitles.size - 4} more)" else ""
        val message = "You have $incompleteCount unfinished tasks today:\n$habitsText\n\nComplete them before bedtime to maintain 100% daily discipline!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_HABITS_URGENT)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText("You have $incompleteCount habits still pending today. Keep your streak!")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(getPendingIntent(context))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(8888, notification)
    }

    fun showReminderNotification(context: Context, title: String, message: String) {
        ensureNotificationChannels(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_HABITS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(getPendingIntent(context))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
