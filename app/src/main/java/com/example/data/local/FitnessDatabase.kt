package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        WorkoutLogEntity::class,
        NutritionMealEntity::class,
        DailyTaskRecordEntity::class,
        WeightLogEntity::class,
        DailyWaterRecordEntity::class,
        DailyStepRecordEntity::class,
        WaterLogEntryEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class FitnessDatabase : RoomDatabase() {
    abstract fun fitnessDao(): FitnessDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessDatabase? = null

        fun getDatabase(context: Context): FitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessDatabase::class.java,
                    "fitness_os_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                initializeCleanUserProfile(database.fitnessDao())
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Real Data from Day 1:
         * Zero demo habits, zero fake weights, zero mock historical records.
         * Only initializes a clean user profile schema for tracking real metrics.
         */
        suspend fun initializeCleanUserProfile(dao: FitnessDao) {
            val todayStr = com.example.utils.DateUtils.getTodayDateString()
            dao.insertOrUpdateUserProfile(
                UserProfileEntity(
                    id = 1,
                    fullName = "",
                    email = "",
                    username = "",
                    isLoggedIn = false,
                    currentSteps = 0,
                    stepGoal = 10000,
                    waterDrankLiters = 0.0f,
                    waterGoalLiters = 3.0f,
                    waterReminderIntervalHours = 2,
                    caloriesBurnedToday = 0,
                    calorieBurnGoal = 500,
                    wakeUpTime = "04:30 AM",
                    sleepTime = "10:00 PM",
                    sleepHoursGoal = 6.0f,
                    actualSleepHours = 0.0f,
                    dailyReadingPagesGoal = 10,
                    pagesReadToday = 0,
                    habitNotificationsEnabled = true,
                    waterRemindersEnabled = true,
                    isDarkMode = false,
                    lastActiveDate = todayStr
                )
            )
        }
    }
}
