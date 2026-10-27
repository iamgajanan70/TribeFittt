package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(profile: UserProfileEntity)

    // Daily Permanent Task Records (The 10 System Goals)
    @Query("SELECT * FROM daily_task_records ORDER BY dateText DESC, id ASC")
    fun getAllDailyTaskRecords(): Flow<List<DailyTaskRecordEntity>>

    @Query("SELECT * FROM daily_task_records WHERE dateText = :dateText")
    fun getDailyTaskRecordsForDate(dateText: String): Flow<List<DailyTaskRecordEntity>>

    @Query("SELECT * FROM daily_task_records WHERE dateText = :dateText")
    suspend fun getDailyTaskRecordsForDateDirect(dateText: String): List<DailyTaskRecordEntity>

    @Query("SELECT * FROM daily_task_records WHERE taskId = :taskId AND dateText = :dateText LIMIT 1")
    suspend fun getDailyTaskRecord(taskId: String, dateText: String): DailyTaskRecordEntity?

    @Query("SELECT * FROM daily_task_records WHERE taskId = :taskId ORDER BY dateText DESC")
    fun getRecordsForTask(taskId: String): Flow<List<DailyTaskRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyTaskRecord(record: DailyTaskRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyTaskRecords(records: List<DailyTaskRecordEntity>)

    @Query("DELETE FROM daily_task_records")
    suspend fun clearAllDailyTaskRecords()

    // Workouts (100% Real user-logged workouts)
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<WorkoutLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutLogEntity)

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteWorkout(id: Long)

    @Query("DELETE FROM workout_logs")
    suspend fun clearAllWorkouts()

    // Nutrition Meals (100% Real user-logged meals)
    @Query("SELECT * FROM nutrition_meals ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<NutritionMealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: NutritionMealEntity)

    @Query("DELETE FROM nutrition_meals WHERE id = :id")
    suspend fun deleteMeal(id: Long)

    @Query("DELETE FROM nutrition_meals")
    suspend fun clearAllMeals()

    // Body Weight Logs (100% Real user-recorded weight)
    @Query("SELECT * FROM weight_logs ORDER BY timestamp DESC")
    fun getAllWeightLogs(): Flow<List<WeightLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(weightLog: WeightLogEntity)

    @Query("DELETE FROM weight_logs")
    suspend fun clearAllWeightLogs()

    // Daily Water Records (Independent per calendar date)
    @Query("SELECT * FROM daily_water_records ORDER BY dateText DESC")
    fun getAllWaterRecords(): Flow<List<DailyWaterRecordEntity>>

    @Query("SELECT * FROM daily_water_records WHERE userId = :userId AND dateText = :dateText LIMIT 1")
    fun getWaterRecordForDate(userId: Long, dateText: String): Flow<DailyWaterRecordEntity?>

    @Query("SELECT * FROM daily_water_records WHERE userId = :userId AND dateText = :dateText LIMIT 1")
    suspend fun getWaterRecordForDateDirect(userId: Long, dateText: String): DailyWaterRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWaterRecord(record: DailyWaterRecordEntity)

    @Query("DELETE FROM daily_water_records")
    suspend fun clearAllWaterRecords()

    // Individual Water Log Entries
    @Query("SELECT * FROM water_log_entries WHERE userId = :userId AND dateText = :dateText ORDER BY timestamp DESC")
    fun getWaterEntriesForDate(userId: Long, dateText: String): Flow<List<WaterLogEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLogEntry(entry: WaterLogEntryEntity)

    @Query("DELETE FROM water_log_entries")
    suspend fun clearAllWaterLogEntries()

    // Daily Step Records (Independent per calendar date)
    @Query("SELECT * FROM daily_step_records ORDER BY dateText DESC")
    fun getAllStepRecords(): Flow<List<DailyStepRecordEntity>>

    @Query("SELECT * FROM daily_step_records WHERE userId = :userId AND dateText = :dateText LIMIT 1")
    fun getStepRecordForDate(userId: Long, dateText: String): Flow<DailyStepRecordEntity?>

    @Query("SELECT * FROM daily_step_records WHERE userId = :userId AND dateText = :dateText LIMIT 1")
    suspend fun getStepRecordForDateDirect(userId: Long, dateText: String): DailyStepRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStepRecord(record: DailyStepRecordEntity)

    @Query("DELETE FROM daily_step_records")
    suspend fun clearAllStepRecords()
}
