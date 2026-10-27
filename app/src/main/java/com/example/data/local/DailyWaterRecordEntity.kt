package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_water_records",
    indices = [Index(value = ["userId", "dateText"], unique = true)]
)
data class DailyWaterRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 1,
    val dateText: String, // "yyyy-MM-dd"
    val waterConsumedLiters: Float = 0.0f,
    val waterGoalLiters: Float = 3.0f,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
