package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_log_entries")
data class WaterLogEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 1,
    val dateText: String, // "yyyy-MM-dd"
    val amountLiters: Float,
    val timestamp: Long = System.currentTimeMillis()
)
