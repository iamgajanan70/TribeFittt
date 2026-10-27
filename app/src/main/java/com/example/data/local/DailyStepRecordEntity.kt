package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_step_records",
    indices = [Index(value = ["userId", "dateText"], unique = true)]
)
data class DailyStepRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 1,
    val dateText: String, // "yyyy-MM-dd"
    val stepCount: Int = 0,
    val stepGoal: Int = 10000,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
