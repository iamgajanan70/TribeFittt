package com.example.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.example.data.local.DailyStepRecordEntity
import com.example.data.local.DailyTaskRecordEntity
import com.example.data.local.DailyWaterRecordEntity
import com.example.data.local.NutritionMealEntity
import com.example.data.local.WeightLogEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.PermanentTask
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    fun exportAllDataToCsv(
        context: Context,
        workouts: List<WorkoutLogEntity>,
        meals: List<NutritionMealEntity>,
        weightLogs: List<WeightLogEntity>,
        dailyTaskRecords: List<DailyTaskRecordEntity> = emptyList(),
        waterRecords: List<DailyWaterRecordEntity> = emptyList(),
        stepRecords: List<DailyStepRecordEntity> = emptyList()
    ): String {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                ?: context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.filesDir

            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

            // 1. Export Workouts
            val workoutFile = File(downloadsDir, "TribeFit_Workouts_$timestamp.csv")
            FileOutputStream(workoutFile).use { out ->
                val sb = StringBuilder()
                sb.append("ID,Title,Duration (Mins),Exercise Count,Status,Date,Timestamp,Exercises\n")
                workouts.forEach { w ->
                    val cleanExercises = w.exercisesText.replace("\"", "'").replace("\n", " | ")
                    sb.append("${w.id},\"${w.title}\",${w.durationMinutes},${w.exerciseCount},\"${w.status}\",\"${w.dateText}\",${w.timestamp},\"$cleanExercises\"\n")
                }
                out.write(sb.toString().toByteArray())
            }

            // 2. Export Nutrition
            val nutritionFile = File(downloadsDir, "TribeFit_Nutrition_$timestamp.csv")
            FileOutputStream(nutritionFile).use { out ->
                val sb = StringBuilder()
                sb.append("ID,Meal Type,Food Name,Calories (kcal),Protein (g),Carbs (g),Fat (g),Timestamp\n")
                meals.forEach { m ->
                    val cleanFood = m.foodName.replace("\"", "'")
                    sb.append("${m.id},\"${m.mealType}\",\"$cleanFood\",${m.calories},${m.proteinGrams},${m.carbsGrams},${m.fatGrams},${m.timestamp}\n")
                }
                out.write(sb.toString().toByteArray())
            }

            // 3. Export Weight Logs
            val weightFile = File(downloadsDir, "TribeFit_WeightLogs_$timestamp.csv")
            FileOutputStream(weightFile).use { out ->
                val sb = StringBuilder()
                sb.append("ID,Weight (kg),Date,Timestamp\n")
                weightLogs.forEach { wl ->
                    sb.append("${wl.id},${wl.weightKg},\"${wl.dateText}\",${wl.timestamp}\n")
                }
                out.write(sb.toString().toByteArray())
            }

            // 4. Export Daily Water Records
            val waterFile = File(downloadsDir, "TribeFit_WaterRecords_$timestamp.csv")
            FileOutputStream(waterFile).use { out ->
                val sb = StringBuilder()
                sb.append("ID,Date,Consumed (L),Goal (L),Timestamp\n")
                waterRecords.forEach { wr ->
                    sb.append("${wr.id},\"${wr.dateText}\",${wr.waterConsumedLiters},${wr.waterGoalLiters},${wr.lastUpdatedTimestamp}\n")
                }
                out.write(sb.toString().toByteArray())
            }

            // 5. Export Daily Step Records
            val stepFile = File(downloadsDir, "TribeFit_StepRecords_$timestamp.csv")
            FileOutputStream(stepFile).use { out ->
                val sb = StringBuilder()
                sb.append("ID,Date,Steps,Goal,Timestamp\n")
                stepRecords.forEach { sr ->
                    sb.append("${sr.id},\"${sr.dateText}\",${sr.stepCount},${sr.stepGoal},${sr.lastUpdatedTimestamp}\n")
                }
                out.write(sb.toString().toByteArray())
            }

            // 6. Export 10 Permanent Tasks Daily Records
            val taskFile = File(downloadsDir, "TribeFit_10Goals_Records_$timestamp.csv")
            FileOutputStream(taskFile).use { out ->
                val sb = StringBuilder()
                sb.append("Record ID,Task ID,Task Title,Category,Date,Actual Value,Target Value,Unit,Is Completed,Progress %,Display,Extra Data,Timestamp\n")
                dailyTaskRecords.forEach { tr ->
                    val def = PermanentTask.getById(tr.taskId)
                    val title = def?.title ?: tr.taskId
                    val cat = def?.category ?: "Protocol"
                    val unit = def?.unit ?: ""
                    sb.append("${tr.id},\"${tr.taskId}\",\"$title\",\"$cat\",\"${tr.dateText}\",${tr.actualValue},${tr.targetValue},\"$unit\",${tr.isCompleted},${tr.completionPercentage},\"${tr.valueDisplay}\",\"${tr.extraData}\",${tr.lastUpdatedTimestamp}\n")
                }
                out.write(sb.toString().toByteArray())
            }

            val successMsg = "Successfully exported CSV database backup to Downloads folder!"
            Toast.makeText(context, successMsg, Toast.LENGTH_LONG).show()
            workoutFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMsg = "Failed to export CSV: ${e.localizedMessage}"
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            ""
        }
    }
}
