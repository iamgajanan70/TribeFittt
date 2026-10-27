package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DailyTaskRecordEntity
import com.example.data.model.PermanentTask
import com.example.data.model.getIcon
import com.example.ui.theme.MonoOnPrimary
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerHigh
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest
import com.example.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HabitContributionGraph(
    allDailyTaskRecords: List<DailyTaskRecordEntity>,
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    val todayStr = remember { DateUtils.getTodayDateString() }
    val weeksGrid = remember { DateUtils.getContributionGridDates(numWeeks = 16) }
    val scrollState = rememberScrollState()

    // Auto-scroll to the right (most recent week / today)
    LaunchedEffect(Unit) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    // Map: dateText -> count of completed permanent tasks
    val completedRecordsByDate = remember(allDailyTaskRecords) {
        allDailyTaskRecords.filter { it.isCompleted }.groupBy { it.dateText }
    }

    // Days where at least 1 task was completed
    val activeDatesSet = remember(completedRecordsByDate) {
        completedRecordsByDate.keys
    }

    // Days where ALL 10 permanent tasks were completed (100% perfect days)
    val perfectDatesSet = remember(completedRecordsByDate) {
        completedRecordsByDate.filter { it.value.size >= 10 }.keys
    }

    val currentStreak = remember(activeDatesSet) { DateUtils.calculateStreak(activeDatesSet) }
    val longestStreak = remember(activeDatesSet) { DateUtils.calculateLongestStreak(activeDatesSet) }
    val perfectStreak = remember(perfectDatesSet) { DateUtils.calculateStreak(perfectDatesSet) }
    val totalCompletions = remember(allDailyTaskRecords) { allDailyTaskRecords.count { it.isCompleted } }

    // 30-Day Completion Rate
    val past30Days = remember { DateUtils.getPastDays(30) }
    val last30Completions = remember(completedRecordsByDate) {
        past30Days.sumOf { completedRecordsByDate[it]?.size ?: 0 }
    }
    val possible30Completions = past30Days.size * 10
    val completionRate30 = if (possible30Completions > 0) {
        (last30Completions * 100) / possible30Completions
    } else 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("permanent_goals_contribution_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
        border = BorderStroke(0.75.dp, MonoOutlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Matrix Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MonoPrimary, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "10 Goals Heatmap",
                            tint = MonoOnPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "10 GOALS CONTRIBUTION GRAPH",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MonoOnSurface,
                            letterSpacing = 0.5.sp,
                            fontSize = 13.5.sp
                        )
                        Text(
                            text = "Daily check-ins across 10 permanent goals",
                            style = MaterialTheme.typography.bodySmall,
                            color = MonoOnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MonoSurfaceContainerHigh,
                    border = BorderStroke(0.5.dp, MonoOutlineVariant)
                ) {
                    Text(
                        text = "16 WEEKS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MonoOnSurface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4-Card Stats Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContributionStatPill(
                    icon = Icons.Default.LocalFireDepartment,
                    label = "STREAK",
                    value = "$currentStreak days",
                    modifier = Modifier.weight(1f)
                )
                ContributionStatPill(
                    icon = Icons.Default.EmojiEvents,
                    label = "BEST STREAK",
                    value = "$longestStreak days",
                    modifier = Modifier.weight(1f)
                )
                ContributionStatPill(
                    icon = Icons.Default.TrendingUp,
                    label = "30D RATE",
                    value = "$completionRate30%",
                    modifier = Modifier.weight(1f)
                )
                ContributionStatPill(
                    icon = Icons.Default.CheckCircle,
                    label = "TOTAL GOALS",
                    value = "$totalCompletions",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contribution Matrix Heatmap
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MonoSurfaceContainerLow, RoundedCornerShape(12.dp))
                    .border(0.75.dp, MonoOutlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Day of week labels on left (Mon, Wed, Fri)
                    Column(
                        modifier = Modifier
                            .width(28.dp)
                            .padding(top = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                        dayLabels.forEachIndexed { index, label ->
                            Box(
                                modifier = Modifier
                                    .height(14.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (index == 1 || index == 3 || index == 5) {
                                    Text(
                                        text = label.take(1),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MonoOnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Horizontally scrollable weeks matrix
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        weeksGrid.forEachIndexed { weekIndex, weekDays ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                val firstDayOfWeek = weekDays.firstOrNull() ?: ""
                                val monthName = if (firstDayOfWeek.endsWith("-01") || firstDayOfWeek.endsWith("-02") || firstDayOfWeek.endsWith("-03") || weekIndex == 0) {
                                    DateUtils.getMonthName(firstDayOfWeek)
                                } else {
                                    ""
                                }

                                Box(
                                    modifier = Modifier
                                        .height(14.dp)
                                        .width(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (monthName.isNotBlank()) {
                                        Text(
                                            text = monthName,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MonoOnSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }

                                // 7 Day squares for this week
                                weekDays.forEach { dateStr ->
                                    val completedCount = completedRecordsByDate[dateStr]?.size ?: 0
                                    val ratio = completedCount.toFloat() / 10f
                                    val isSelected = dateStr == selectedDate
                                    val isToday = dateStr == todayStr

                                    val tileColor = getMonochromeTileColor(ratio, isDarkMode)

                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(RoundedCornerShape(2.5.dp))
                                            .background(tileColor)
                                            .then(
                                                if (isSelected) {
                                                    Modifier.border(1.5.dp, MonoPrimary, RoundedCornerShape(2.5.dp))
                                                } else if (isToday) {
                                                    Modifier.border(1.dp, MonoOnSurfaceVariant, RoundedCornerShape(2.5.dp))
                                                } else {
                                                    Modifier.border(0.35.dp, MonoOutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(2.5.dp))
                                                }
                                            )
                                            .clickable {
                                                onSelectDate(dateStr)
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Monochrome Legend: Less [■ ■ ■ ■ ■] More
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Less",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = MonoOnSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                listOf(0f, 0.25f, 0.5f, 0.75f, 1.0f).forEach { r ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(getMonochromeTileColor(r, isDarkMode))
                            .border(0.35.dp, MonoOutlineVariant, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "More (10/10)",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = MonoOnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MonoOutlineVariant, thickness = 0.75.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Selected Day Summary
            val selectedRecords = completedRecordsByDate[selectedDate] ?: emptyList()
            val selectedCompletedTaskIds = selectedRecords.map { it.taskId }.toSet()
            val isSelectedToday = selectedDate == todayStr

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isSelectedToday) "TODAY'S 10 GOALS SUMMARY" else "SUMMARY FOR ${DateUtils.formatDateForDisplay(selectedDate).uppercase()}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MonoOnSurface,
                                fontSize = 13.sp
                            )
                            if (isSelectedToday) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MonoPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MonoOnPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${selectedCompletedTaskIds.size} of 10 goals completed (${(selectedCompletedTaskIds.size * 10)}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MonoOnSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    // Navigation buttons to step through days
                    Row {
                        IconButton(
                            onClick = {
                                onSelectDate(getOffsetDate(selectedDate, -1))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Day",
                                tint = MonoOnSurface
                            )
                        }
                        IconButton(
                            onClick = {
                                onSelectDate(getOffsetDate(selectedDate, 1))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Day",
                                tint = MonoOnSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Breakdown list of the 10 permanent tasks for this date
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PermanentTask.ALL_PERMANENT_TASKS.forEach { task ->
                        val isTaskCompleted = selectedCompletedTaskIds.contains(task.id)

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isTaskCompleted) MonoSurfaceContainerHigh else MonoSurfaceContainerLow,
                            border = BorderStroke(
                                0.75.dp,
                                if (isTaskCompleted) Color(0xFF2E7D32).copy(alpha = 0.6f) else MonoOutlineVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isTaskCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = if (isTaskCompleted) "Completed" else "Pending",
                                        tint = if (isTaskCompleted) Color(0xFF2E7D32) else MonoOnSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${task.orderIndex}. ${task.title}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isTaskCompleted) FontWeight.Bold else FontWeight.Normal,
                                            color = MonoOnSurface,
                                            fontSize = 12.5.sp
                                        )
                                        Text(
                                            text = "${task.category} • Target: ${task.description}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MonoOnSurfaceVariant,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isTaskCompleted) Color(0xFF2E7D32) else MonoSurfaceLowest,
                                    border = BorderStroke(0.5.dp, if (isTaskCompleted) Color(0xFF2E7D32) else MonoOutlineVariant)
                                ) {
                                    Text(
                                        text = if (isTaskCompleted) "COMPLETED" else "PENDING",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTaskCompleted) Color.White else MonoOnSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContributionStatPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MonoSurfaceContainerLow,
        border = BorderStroke(0.75.dp, MonoOutlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MonoPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = MonoOnSurface,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MonoOnSurfaceVariant,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )
        }
    }
}

private fun getMonochromeTileColor(ratio: Float, isDarkMode: Boolean): Color {
    return when {
        ratio <= 0.001f -> {
            if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFEEEEEE)
        }
        ratio <= 0.30f -> {
            if (isDarkMode) Color(0xFF383838) else Color(0xFFD0D0D0)
        }
        ratio <= 0.60f -> {
            if (isDarkMode) Color(0xFF666666) else Color(0xFF9E9E9E)
        }
        ratio <= 0.85f -> {
            if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF616161)
        }
        else -> {
            if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF111111)
        }
    }
}

private fun getOffsetDate(baseDateStr: String, offsetDays: Int): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(baseDateStr) ?: return baseDateStr
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, offsetDays)
        sdf.format(cal.time)
    } catch (e: Exception) {
        baseDateStr
    }
}
