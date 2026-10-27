package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The 10 Permanent Tasks permanently built into the application.
 * System-defined goals that can never be deleted, removed, replaced, or renamed.
 */
enum class TaskType {
    STEPS,
    WATER,
    NO_JUNK_FOOD,
    PROTEIN_MEALS,
    SLEEP,
    WORKOUT,
    STUDY,
    READING,
    BEDTIME,
    WAKEUP
}

data class PermanentTask(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val targetValue: Float,
    val unit: String,
    val orderIndex: Int,
    val taskType: TaskType
) {
    companion object {
        const val ID_STEPS = "PERMANENT_STEPS_10K"
        const val ID_WATER = "PERMANENT_WATER_3L"
        const val ID_NO_JUNK_FOOD = "PERMANENT_NO_JUNK_FOOD"
        const val ID_PROTEIN_MEALS = "PERMANENT_PROTEIN_MEALS"
        const val ID_SLEEP = "PERMANENT_SLEEP_6H"
        const val ID_WORKOUT = "PERMANENT_WORKOUT"
        const val ID_STUDY = "PERMANENT_STUDY_2H"
        const val ID_READING = "PERMANENT_READING_10P"
        const val ID_BEDTIME = "PERMANENT_BEDTIME_10PM"
        const val ID_WAKEUP = "PERMANENT_WAKEUP_430AM"

        val ALL_PERMANENT_TASKS = listOf(
            PermanentTask(
                id = ID_STEPS,
                title = "10K Steps",
                description = "Complete 10,000 steps every day",
                category = "Activity",
                targetValue = 10000f,
                unit = "steps",
                orderIndex = 1,
                taskType = TaskType.STEPS
            ),
            PermanentTask(
                id = ID_WATER,
                title = "3L+ Water Intake",
                description = "Drink at least 3 liters of water every day",
                category = "Hydration",
                targetValue = 3.0f,
                unit = "L",
                orderIndex = 2,
                taskType = TaskType.WATER
            ),
            PermanentTask(
                id = ID_NO_JUNK_FOOD,
                title = "No Junk Food",
                description = "Avoid junk food every day",
                category = "Nutrition",
                targetValue = 1.0f,
                unit = "goal",
                orderIndex = 3,
                taskType = TaskType.NO_JUNK_FOOD
            ),
            PermanentTask(
                id = ID_PROTEIN_MEALS,
                title = "Protein With Every Meal",
                description = "Consume a protein source with every meal (Breakfast, Lunch, Dinner)",
                category = "Nutrition",
                targetValue = 3.0f,
                unit = "meals",
                orderIndex = 4,
                taskType = TaskType.PROTEIN_MEALS
            ),
            PermanentTask(
                id = ID_SLEEP,
                title = "6+ Hours Sleep",
                description = "Get at least 6 hours of sleep every day",
                category = "Recovery",
                targetValue = 6.0f,
                unit = "hrs",
                orderIndex = 5,
                taskType = TaskType.SLEEP
            ),
            PermanentTask(
                id = ID_WORKOUT,
                title = "Workout",
                description = "Complete the daily workout session",
                category = "Training",
                targetValue = 1.0f,
                unit = "session",
                orderIndex = 6,
                taskType = TaskType.WORKOUT
            ),
            PermanentTask(
                id = ID_STUDY,
                title = "2+ Hours Study",
                description = "Study for at least 2 hours (120 minutes) every day",
                category = "Mind & Growth",
                targetValue = 120.0f,
                unit = "mins",
                orderIndex = 7,
                taskType = TaskType.STUDY
            ),
            PermanentTask(
                id = ID_READING,
                title = "Read 10 Pages",
                description = "Read at least 10 pages every day",
                category = "Mind & Growth",
                targetValue = 10.0f,
                unit = "pages",
                orderIndex = 8,
                taskType = TaskType.READING
            ),
            PermanentTask(
                id = ID_BEDTIME,
                title = "Bedtime Before 10:00 PM",
                description = "Go to sleep before 10:00 PM",
                category = "Discipline",
                targetValue = 22.0f, // 22:00 in 24h
                unit = "time",
                orderIndex = 9,
                taskType = TaskType.BEDTIME
            ),
            PermanentTask(
                id = ID_WAKEUP,
                title = "Wake Up Before 4:30 AM",
                description = "Wake up before 4:30 AM",
                category = "Discipline",
                targetValue = 4.5f, // 04:30 in 24h
                unit = "time",
                orderIndex = 10,
                taskType = TaskType.WAKEUP
            )
        )

        fun getById(id: String): PermanentTask? {
            return ALL_PERMANENT_TASKS.find { it.id == id }
        }
    }
}

fun PermanentTask.getIcon(): ImageVector {
    return when (taskType) {
        TaskType.STEPS -> Icons.Default.DirectionsWalk
        TaskType.WATER -> Icons.Default.WaterDrop
        TaskType.NO_JUNK_FOOD -> Icons.Default.NoFood
        TaskType.PROTEIN_MEALS -> Icons.Default.Egg
        TaskType.SLEEP -> Icons.Default.Bedtime
        TaskType.WORKOUT -> Icons.Default.FitnessCenter
        TaskType.STUDY -> Icons.Default.School
        TaskType.READING -> Icons.Default.Book
        TaskType.BEDTIME -> Icons.Default.Bedtime
        TaskType.WAKEUP -> Icons.Default.WbSunny
    }
}
