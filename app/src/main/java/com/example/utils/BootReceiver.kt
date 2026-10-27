package com.example.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            StepTrackingService.startTracking(context)
            DailyReminderWorker.scheduleDailyReminder(context)
            WaterReminderWorker.scheduleWaterReminder(context, 2)
        }
    }
}
