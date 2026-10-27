package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Daily Task Record storing progress and completion state for one of the 10 permanent tasks
 * on a specific calendar day (YYYY-MM-DD).
 *
 * Unique combination of (taskId, dateText) ensures exactly 1 record per permanent task per day.
 */
@Entity(
    tableName = "daily_task_records",
    indices = [Index(value = ["taskId", "dateText"], unique = true)]
)
data class DailyTaskRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: String,
    val dateText: String, // format "yyyy-MM-dd" e.g. "2026-08-19"
    val actualValue: Float = 0.0f,
    val targetValue: Float,
    val isCompleted: Boolean = false,
    val completionPercentage: Float = 0.0f,
    val valueDisplay: String = "",
    val extraData: String = "", // Holds secondary details: e.g. "Breakfast,Lunch" or "09:45 PM"
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
