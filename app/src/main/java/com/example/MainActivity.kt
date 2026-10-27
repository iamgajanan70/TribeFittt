package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.PermanentTask
import com.example.ui.components.BottomNavBar
import com.example.ui.components.BottomTab
import com.example.ui.screens.BootScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EditProfileScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NutritionScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.WorkoutsScreen
import com.example.ui.theme.FitnessOSTheme
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.data.viewmodel.FitnessViewModel
import com.example.utils.DailyReminderWorker
import com.example.utils.StepTrackingService
import com.example.utils.WaterReminderWorker

enum class AppScreen {
    BOOT,
    LOGIN,
    SIGNUP,
    MAIN,
    EDIT_PROFILE,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitnessOSApp()
        }
    }
}

@Composable
fun FitnessOSApp(viewModel: FitnessViewModel = viewModel()) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val activityGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: false
        if (activityGranted || ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            StepTrackingService.startTracking(context)
        }
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE_HEALTH)
        }
        permissionLauncher.launch(permissionsToRequest.toTypedArray())

        // Start continuous background step service and daily reminders
        StepTrackingService.startTracking(context)
        DailyReminderWorker.scheduleDailyReminder(context)
        WaterReminderWorker.scheduleWaterReminder(context, 2)
    }

    val liveStepsFromService by StepTrackingService.liveSteps.collectAsState()

    val userProfile by viewModel.userProfile.collectAsState()
    val workouts by viewModel.workouts.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val weightLogs by viewModel.weightLogs.collectAsState()
    val waterRecords by viewModel.waterRecords.collectAsState()
    val stepRecords by viewModel.stepRecords.collectAsState()
    val allDailyTaskRecords by viewModel.allDailyTaskRecords.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentDayTaskRecords by viewModel.currentDayTaskRecords.collectAsState()
    val isStudyTimerRunning by viewModel.isStudyTimerRunning.collectAsState()
    val studyTimerSeconds by viewModel.studyTimerSeconds.collectAsState()
    val nutritionSummary by viewModel.nutritionSummary.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val isUploadingImage by viewModel.isUploadingImage.collectAsState()

    var currentScreen by remember { mutableStateOf(AppScreen.BOOT) }
    var currentTab by remember { mutableStateOf(BottomTab.DASHBOARD) }

    val snackbarHostState = remember { SnackbarHostState() }

    FitnessOSTheme(darkTheme = userProfile?.isDarkMode == true) {
        LaunchedEffect(statusMessage) {
            statusMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg)
                viewModel.clearStatusMessage()
            }
        }

        when (currentScreen) {
            AppScreen.BOOT -> {
                BootScreen(
                    onBootFinished = {
                        if (userProfile?.isLoggedIn == true && !userProfile?.fullName.isNullOrBlank()) {
                            currentScreen = AppScreen.MAIN
                        } else {
                            currentScreen = AppScreen.SIGNUP
                        }
                    }
                )
            }

            AppScreen.LOGIN -> {
                LoginScreen(
                    userProfile = userProfile,
                    onLoginSuccess = { email, pass ->
                        viewModel.logIn(email, pass) {
                            currentScreen = AppScreen.MAIN
                        }
                    },
                    onNavigateToSignUp = {
                        currentScreen = AppScreen.SIGNUP
                    },
                    statusMessage = statusMessage
                )
            }

            AppScreen.SIGNUP -> {
                SignUpScreen(
                    onBack = { currentScreen = AppScreen.LOGIN },
                    onSignUpComplete = { fullName, email, pass, age, gender, h, w ->
                        viewModel.signUp(fullName, email, pass, age, gender, h, w) {
                            currentScreen = AppScreen.MAIN
                        }
                    },
                    onNavigateToLogin = { currentScreen = AppScreen.LOGIN }
                )
            }

            AppScreen.EDIT_PROFILE -> {
                Scaffold(
                    topBar = {
                        TopNavBar(
                            title = "EDIT PROFILE",
                            showBack = true,
                            showCheck = true,
                            onBackClick = { currentScreen = AppScreen.MAIN },
                            onCheckClick = { currentScreen = AppScreen.MAIN }
                        )
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        EditProfileScreen(
                            userProfile = userProfile,
                            onSaveProfile = { fullName, email, username, age, gender, heightCm, weightKg ->
                                viewModel.updateProfile(fullName, email, username, age, gender, heightCm, weightKg)
                                currentScreen = AppScreen.MAIN
                            },
                            onSaveSleepSchedule = { wake, sleep, sGoal, sActual ->
                                viewModel.updateSleepSchedule(wake, sleep, sGoal, sActual)
                            },
                            onUploadAvatar = { bitmap ->
                                viewModel.uploadAvatarBitmap(bitmap)
                            },
                            isUploadingImage = isUploadingImage
                        )
                    }
                }
            }

            AppScreen.SETTINGS -> {
                Scaffold(
                    topBar = {
                        TopNavBar(
                            title = "SETTINGS",
                            showBack = true,
                            showCheck = false,
                            onBackClick = { currentScreen = AppScreen.MAIN }
                        )
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        SettingsScreen(
                            userProfile = userProfile,
                            onSaveGoals = { steps, water, cal, burn, sleep, reading ->
                                viewModel.updateGoals(steps, water, cal, burn, sleep, reading)
                            },
                            onSaveSleepSchedule = { wake, sleep, sGoal, sActual ->
                                viewModel.updateSleepSchedule(wake, sleep, sGoal, sActual)
                            },
                            onUploadAvatar = { bitmap ->
                                viewModel.uploadAvatarBitmap(bitmap)
                            },
                            onNavigateToEditProfile = {
                                currentScreen = AppScreen.EDIT_PROFILE
                            },
                            onTriggerTestNotification = {
                                viewModel.triggerTestNotification()
                            },
                            onTriggerTestWaterReminder = {
                                viewModel.triggerTestWaterReminder()
                            },
                            onTriggerTestHabitReminder = { isSecondReminder ->
                                viewModel.triggerTestHabitReminder(isSecondReminder)
                            },
                            onTriggerIncompleteHabits2x = {
                                viewModel.triggerIncompleteHabitsDoubleReminder()
                            },
                            onToggleWaterReminders = {
                                viewModel.toggleWaterReminders()
                            },
                            onSetWaterInterval = { hours ->
                                viewModel.setWaterReminderInterval(hours)
                            },
                            onToggleFocusMode = {
                                viewModel.toggleFocusMode()
                            },
                            onToggleDarkMode = {
                                viewModel.toggleDarkMode()
                            },
                            onLogOut = {
                                viewModel.logOut()
                                currentScreen = AppScreen.LOGIN
                            },
                            onResetAllData = {
                                viewModel.resetAllLocalData()
                                currentScreen = AppScreen.SIGNUP
                            },
                            onExportCsv = {
                                viewModel.exportDataToCsv(context)
                            }
                        )
                    }
                }
            }

            AppScreen.MAIN -> {
                Scaffold(
                    topBar = {
                        TopNavBar(
                            title = when (currentTab) {
                                BottomTab.DASHBOARD -> "TRIBEFIT"
                                BottomTab.WORKOUTS -> "WORKOUTS"
                                BottomTab.NUTRITION -> "NUTRITION"
                                BottomTab.PROGRESS -> "PROGRESS & STATS"
                                BottomTab.PROFILE -> "PROFILE"
                            },
                            showBack = false,
                            onNotificationClick = {
                                viewModel.triggerTestNotification()
                            },
                            onSettingsClick = {
                                currentScreen = AppScreen.SETTINGS
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        when (currentTab) {
                            BottomTab.DASHBOARD -> {
                                DashboardScreen(
                                    userProfile = userProfile,
                                    nutritionSummary = nutritionSummary,
                                    selectedDate = selectedDate,
                                    dayTaskRecords = currentDayTaskRecords,
                                    allDailyTaskRecords = allDailyTaskRecords,
                                    onSelectDate = { viewModel.selectDate(it) },
                                    onGoToToday = { viewModel.goToToday() },
                                    onGoToYesterday = { viewModel.goToYesterday() },
                                    onShiftDate = { viewModel.shiftDate(it) },
                                    onAddSteps = { viewModel.addSteps(it) },
                                    onAddWater = { viewModel.addWater(it) },
                                    onToggleNoJunkFood = { viewModel.toggleNoJunkFood() },
                                    onToggleProteinMeal = { viewModel.toggleProteinMeal(it) },
                                    onLogSleep = { viewModel.logSleepHours(it) },
                                    onToggleWorkout = { viewModel.toggleWorkout() },
                                    onAddStudyMinutes = { viewModel.addStudyMinutes(it) },
                                    isStudyTimerRunning = isStudyTimerRunning,
                                    studyTimerSeconds = studyTimerSeconds,
                                    onStartStudyTimer = { viewModel.startStudyTimer() },
                                    onPauseStudyTimer = { viewModel.pauseStudyTimer() },
                                    onSaveStudyTimer = { viewModel.saveStudyTimer() },
                                    onAddReadingPages = { viewModel.addReadingPages(it) },
                                    onLogBedtime = { viewModel.logBedtime(it) },
                                    onLogWakeUpTime = { viewModel.logWakeUpTime(it) },
                                    onOpenLiveWorkout = { title ->
                                        viewModel.addWorkout(
                                            title = title,
                                            durationMinutes = 45,
                                            exerciseCount = 6,
                                            exercisesText = "Hypertrophy routine completed",
                                            status = "Completed"
                                        )
                                    },
                                    onTriggerNotification = { viewModel.triggerTestNotification() },
                                    onTriggerIncompleteHabits2x = { viewModel.triggerIncompleteHabitsDoubleReminder() },
                                    onNavigateToWorkouts = { currentTab = BottomTab.WORKOUTS },
                                    onNavigateToNutrition = { currentTab = BottomTab.NUTRITION },
                                    onNavigateToSettings = { currentScreen = AppScreen.SETTINGS },
                                    isDarkMode = userProfile?.isDarkMode == true
                                )
                            }

                            BottomTab.WORKOUTS -> {
                                WorkoutsScreen(
                                    workouts = workouts,
                                    onLogWorkout = { title, duration, exercises ->
                                        viewModel.addWorkout(title, duration, 6, exercises, status = "Completed")
                                    },
                                    onStartActiveWorkout = { title ->
                                        viewModel.addWorkout(
                                            title = title,
                                            durationMinutes = 45,
                                            exerciseCount = 6,
                                            exercisesText = "High intensity training protocol",
                                            status = "Completed"
                                        )
                                    }
                                )
                            }

                            BottomTab.NUTRITION -> {
                                NutritionScreen(
                                    meals = meals,
                                    nutritionSummary = nutritionSummary,
                                    onAddMeal = { type, food, cal, p, c, f ->
                                        viewModel.addMeal(type, food, cal, p, c, f)
                                    },
                                    onDeleteMeal = { id ->
                                        viewModel.deleteMeal(id)
                                    }
                                )
                            }

                            BottomTab.PROGRESS -> {
                                ProgressScreen(
                                    weightLogs = weightLogs,
                                    dailyTaskRecords = allDailyTaskRecords,
                                    waterRecords = waterRecords,
                                    stepRecords = stepRecords,
                                    workouts = workouts,
                                    meals = meals,
                                    selectedDate = selectedDate,
                                    onSelectDate = { viewModel.selectDate(it) },
                                    currentWeightKg = (userProfile?.weightKg ?: 70).toFloat(),
                                    targetWeightKg = 70f,
                                    onAddWeightLog = { w ->
                                        viewModel.addWeightLog(w)
                                    },
                                    onExportCsv = {
                                        viewModel.exportDataToCsv(context)
                                    },
                                    isDarkMode = userProfile?.isDarkMode == true
                                )
                            }

                            BottomTab.PROFILE -> {
                                ProfileScreen(
                                    userProfile = userProfile,
                                    onNavigateToEditProfile = { currentScreen = AppScreen.EDIT_PROFILE },
                                    onNavigateToSettings = { currentScreen = AppScreen.SETTINGS },
                                    onUpdateSleepSchedule = { wake, sleep, sGoal, sActual ->
                                        viewModel.updateSleepSchedule(wake, sleep, sGoal, sActual)
                                    },
                                    onLogOut = {
                                        currentScreen = AppScreen.SETTINGS
                                    },
                                    statusMessage = statusMessage
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    title: String,
    showBack: Boolean = false,
    showCheck: Boolean = false,
    onBackClick: () -> Unit = {},
    onCheckClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MonoOnSurface
            )
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MonoOnSurface
                    )
                }
            }
        },
        actions = {
            if (showCheck) {
                IconButton(onClick = onCheckClick) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Confirm",
                        tint = MonoOnSurface
                    )
                }
            } else if (!showBack) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.testTag("top_nav_notification_btn")
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MonoOnSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.testTag("top_nav_settings_btn")
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MonoOnSurfaceVariant
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MonoBackground
        )
    )
}
