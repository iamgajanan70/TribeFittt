# TribeFit: Fitness Operating System & Real-Time Step Tracker

> **A native Android fitness operating system engineered with Kotlin, Jetpack Compose, and Room Database for offline-first telemetry, real-time 10,000-step tracking, habit discipline, and comprehensive health analytics.**

---

## 1. Executive Summary

**TribeFit** is a high-performance, distraction-free fitness operating system designed for Android. Built on the philosophy of zero synthetic data, complete user privacy, and resilient offline execution, TribeFit unifies physical movement telemetry, daily habit discipline (the 10 Permanent Tasks), progressive workout logging, hydration tracking, sleep hygiene, and body metric analytics into a unified, high-contrast monochrome interface.

---

## 2. Core Architecture & Technology Stack

### 2.1 Technology Stack

| Layer | Technologies & Libraries |
| :--- | :--- |
| **Language** | Kotlin 1.9+ (100% Kotlin DSL, Coroutines, StateFlow) |
| **UI Framework** | Android Jetpack Compose, Material Design 3 (M3) |
| **Architecture** | Clean Architecture / Model-View-ViewModel (MVVM) |
| **Local Persistence** | SQLite via AndroidX Room Database, AndroidX DataStore |
| **Telemetry & Sensors** | Android Sensor Framework (`Sensor.TYPE_STEP_COUNTER`, `Sensor.TYPE_STEP_DETECTOR`, Accelerometer Fallback) |
| **Background Processing** | Android Foreground Service (`FOREGROUND_SERVICE_HEALTH`), `WorkManager`, `BroadcastReceiver` |
| **Visualizations** | Canvas-based D3-inspired Weight History Charts & Habit Contribution Heatmaps |
| **Build System** | Gradle (Kotlin DSL, `build.gradle.kts`, Version Catalog) |

### 2.2 Architectural Layers

```
                     ┌──────────────────────────────────────────────┐
                     │            Jetpack Compose UI                │
                     │  (Dashboard, Workouts, Nutrition, Progress)   │
                     └──────────────────────▲───────────────────────┘
                                            │ StateFlow / Actions
                     ┌──────────────────────┴───────────────────────┐
                     │              FitnessViewModel                │
                     │   (UI State Management, Coroutine Dispatch)  │
                     └──────────────────────▲───────────────────────┘
                                            │ Flows / Suspend Fns
                     ┌──────────────────────┴───────────────────────┐
                     │             FitnessRepository                │
                     │       (Single Source of Truth Mediator)      │
                     └──────────────▲────────────────▲──────────────┘
                                    │                │
            ┌───────────────────────┴──────┐  ┌──────┴──────────────────────┐
            │        FitnessDatabase       │  │    StepTrackingService      │
            │     (Room SQLite / DAOs)     │  │  (Hardware Sensors HAL)    │
            │ - UserProfileEntity          │  │ - Sensor.TYPE_STEP_COUNTER  │
            │ - DailyStepRecordEntity      │  │ - Sensor.TYPE_STEP_DETECTOR │
            │ - DailyTaskRecordEntity      │  │ - Accelerometer Fallback    │
            │ - DailyWaterRecordEntity     │  │ - Reboot / Rollover Logic   │
            │ - WorkoutLogEntity           │  │ - Notification Engine       │
            └──────────────────────────────┘  └─────────────────────────────┘
```

---

## 3. Real-Time 10,000-Step Tracking Engine

The step tracking system is built directly on physical hardware sensors to guarantee accuracy and eliminate synthetic or random step increments.

### 3.1 Hardware Sensor Hierarchy
1. **Primary Sensor (`Sensor.TYPE_STEP_COUNTER`):** Low-power on-chip pedometer providing cumulative steps since last hardware boot.
2. **Secondary Sensor (`Sensor.TYPE_STEP_DETECTOR`):** Instantaneous step events triggered on each heel-strike.
3. **Tertiary Fallback (Accelerometer Motion Peak Detection):** Three-axis vector magnitude filtering with dynamic cadence debouncing ($> 11.6 \text{ m/s}^2$ with $\ge 280\text{ms}$ interval) for devices lacking dedicated hardware pedometers.

### 3.2 Reboot & Midnight Rollover Algorithm
Hardware step counters report total steps since boot and do not reset at midnight. TribeFit solves this via baseline epoch tracking in `StepTrackingService`:
- **Midnight Rollover:** When the system date advances to a new calendar day (`YYYY-MM-DD`), a new baseline is captured from the current hardware reading. Today's counter begins at `0`, while yesterday's record remains sealed in the Room database.
- **Mid-Day Phone Reboot:** If the user reboots their phone mid-day, the hardware counter resets to `0`. TribeFit detects `rawBootSteps < lastBootSteps`, saves the pre-reboot steps as an offset, and resets the baseline to `0`, ensuring zero step loss or double-counting.
- **Uncapped Storage:** Progress displays relative to the 10,000-step target (`6,842 / 10,000 steps` or `15,432 / 10,000 steps` with $>100\%$ completion). Values are never artificially capped.

### 3.3 Background Execution & Health Service
- Runs an Android Foreground Service with `foregroundServiceType="health"`.
- Uses an ongoing status bar notification displaying live step count, active percentage, and completion of daily goals.
- Wakes automatically on system startup via `BootReceiver` listening for `BOOT_COMPLETED` and `MY_PACKAGE_REPLACED`.

---

## 4. The 10 Permanent Tasks Discipline Framework

TribeFit incorporates a non-negotiable set of 10 daily core disciplines:

| Index | Task ID | Name | Target Goal | Category |
| :---: | :--- | :--- | :---: | :--- |
| **1** | `PERMANENT_STEPS_10K` | **10K Steps** | 10,000 steps | Activity |
| **2** | `PERMANENT_WATER_3L` | **3L+ Water Intake** | 3.0 Liters | Hydration |
| **3** | `PERMANENT_NO_JUNK_FOOD` | **No Junk Food** | 100% adherence | Nutrition |
| **4** | `PERMANENT_PROTEIN_MEALS` | **Protein With Every Meal** | 3 meals (B/L/D) | Nutrition |
| **5** | `PERMANENT_SLEEP_6H` | **6+ Hours Sleep** | $\ge 6.0$ hours | Recovery |
| **6** | `PERMANENT_WORKOUT` | **Workout** | 1 complete session | Training |
| **7** | `PERMANENT_STUDY_2H` | **2+ Hours Study** | 120 minutes | Mind & Growth |
| **8** | `PERMANENT_READING_10P` | **Read 10 Pages** | 10 pages | Mind & Growth |
| **9** | `PERMANENT_BEDTIME_10PM` | **Bedtime Before 10:00 PM** | $\le 22:00$ | Discipline |
| **10** | `PERMANENT_WAKEUP_430AM` | **Wake Up Before 4:30 AM** | $\le 04:30$ AM | Discipline |

Every task tracks real values, computes daily completion percentage, calculates consecutive day streaks, and saves into individual calendar date records.

---

## 5. Comprehensive Module Breakdown

### 5.1 Dashboard & Home Screen (`DashboardScreen.kt`)
- Real-time step counter circle displaying current steps, 10,000-step goal, and animated progress ring.
- Daily habit checklist showing live progress for all 10 Permanent Tasks.
- Quick hydration widget (+250ml, +500ml, +1L) with live daily intake tracking.
- Interactive streak indicators and date navigation.

### 5.2 Workouts & Training (`WorkoutsScreen.kt` & `LiveWorkoutModal.kt`)
- Structured training templates (Push, Pull, Legs, Upper, Lower, Cardio, Custom).
- Live workout execution modal with built-in rest timer, set counter, and rep/weight logger.
- Historical workout review with volume load and personal records.

### 5.3 Nutrition & Meal Tracking (`NutritionScreen.kt`)
- Breakfast, Lunch, Dinner, and Snack meal categorization.
- Protein-per-meal tracking ensuring nutritional discipline.
- Macro summary calculators (Protein, Carbohydrates, Fats, Total Calories).
- Daily "No Junk Food" compliance verification.

### 5.4 Hydration Tracking (`DailyWaterRecordEntity` & `WaterLogEntryEntity`)
- Daily target of 3.0 Liters with granular time-stamped log entries.
- Automated hydration reminders powered by `WorkManager` (`WaterReminderWorker`).

### 5.5 Progress & Analytics (`ProgressScreen.kt`)
- **Habit Contribution Graph:** GitHub-style annual activity heatmap illustrating daily consistency across all 10 goals.
- **D3-Inspired Weight Chart:** Smooth Bézier curve visualization rendering historical weight trends against user target weight.
- **Data Export:** Built-in `CsvExporter` for exporting workout and habit logs into standard CSV spreadsheets.

---

## 6. Database Schema & Room Entities

TribeFit stores all data in a local SQLite database named `fitness_os_database` (version 10):

```kotlin
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
abstract class FitnessDatabase : RoomDatabase()
```

### Key Entities
- **`UserProfileEntity`**: User identity, step goal, water target, sleep schedule, notifications, and dark mode toggles.
- **`DailyStepRecordEntity`**: `dateText` (Primary Key), `stepCount`, `stepGoal`, `lastUpdatedTimestamp`.
- **`DailyTaskRecordEntity`**: Compound key (`taskId`, `dateText`), `actualValue`, `targetValue`, `isCompleted`, `completionPercentage`, `valueDisplay`.
- **`DailyWaterRecordEntity`**: `dateText` (Primary Key), `litersDrank`, `goalLiters`, `completed`.
- **`WaterLogEntryEntity`**: `id`, `dateText`, `amountMl`, `timeText`, `timestamp`.
- **`WorkoutLogEntity`**: `id`, `dateText`, `workoutTitle`, `durationMinutes`, `exercisesJson`, `caloriesBurned`.
- **`WeightLogEntity`**: `id`, `dateText`, `weightKg`, `notes`, `timestamp`.

---

## 7. Permissions & Device Integration

Configured in `app/src/main/AndroidManifest.xml`:

| Permission | Purpose |
| :--- | :--- |
| `android.permission.ACTIVITY_RECOGNITION` | Access to low-power hardware step sensors and motion detectors. |
| `android.permission.BODY_SENSORS` | Access to wearable or on-device health telemetry. |
| `android.permission.FOREGROUND_SERVICE` | Uninterrupted background operation for step tracking. |
| `android.permission.FOREGROUND_SERVICE_HEALTH` | Android 14+ requirement for health-related background services. |
| `android.permission.RECEIVE_BOOT_COMPLETED` | Automatic re-initialization of step tracking on device power-on. |
| `android.permission.POST_NOTIFICATIONS` | Ongoing tracking notifications and scheduled hydration alerts. |
| `android.permission.SCHEDULE_EXACT_ALARM` | Accurate habit and water reminder timing. |

---

## 8. Build & Execution Instructions

### Prerequisites
- Android Studio Iguana / Jellyfish or later.
- Android SDK 34 (compileSdk 34, minSdk 26).
- JDK 17.

### Running via Command Line
```bash
# Compile and build debug APK
gradle :app:assembleDebug

# Run unit and Robolectric tests
gradle :app:testDebugUnitTest
```

The compiled output will be generated at `app/build/outputs/apk/debug/app-debug.apk`.

---

## 9. Summary & Design Philosophy

TribeFit was engineered around five core commitments:
1. **Real Data Only:** No fake steps, no synthetic graphs, no placeholder data.
2. **Offline Resilience:** Your fitness journey belongs on your device, accessible anytime without server downtime.
3. **Zero Distraction:** Clean, high-legibility monochrome aesthetic focused on consistency and discipline.
4. **Permanent Standards:** Uncompromising daily benchmarks that build lasting physical and mental resilience.
5. **Architectural Excellence:** Built with modern Android best practices—Kotlin Coroutines, Jetpack Compose, Room, and foreground service lifecycle management.
