package com.example.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val ISO_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val DISPLAY_DATE_FORMAT = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val SHORT_DISPLAY_FORMAT = SimpleDateFormat("MMM d", Locale.getDefault())
    private val DAY_OF_WEEK_FORMAT = SimpleDateFormat("EEE", Locale.getDefault())
    private val MONTH_FORMAT = SimpleDateFormat("MMM", Locale.getDefault())

    fun getTodayDateString(): String {
        return ISO_DATE_FORMAT.format(Date())
    }

    fun formatDateForDisplay(dateStr: String): String {
        return try {
            val date = ISO_DATE_FORMAT.parse(dateStr)
            if (date != null) DISPLAY_DATE_FORMAT.format(date) else dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatShortDisplay(dateStr: String): String {
        return try {
            val date = ISO_DATE_FORMAT.parse(dateStr)
            if (date != null) SHORT_DISPLAY_FORMAT.format(date) else dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getMonthName(dateStr: String): String {
        return try {
            val date = ISO_DATE_FORMAT.parse(dateStr)
            if (date != null) MONTH_FORMAT.format(date) else ""
        } catch (e: Exception) {
            ""
        }
    }

    fun getDayOfWeek(dateStr: String): String {
        return try {
            val date = ISO_DATE_FORMAT.parse(dateStr)
            if (date != null) DAY_OF_WEEK_FORMAT.format(date) else ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Returns a list of past date strings (format yyyy-MM-dd) ending at today,
     * count = daysCount.
     */
    fun getPastDays(daysCount: Int): List<String> {
        val dates = mutableListOf<String>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysCount + 1)
        for (i in 0 until daysCount) {
            dates.add(ISO_DATE_FORMAT.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dates
    }

    /**
     * Generates a matrix of dates aligned to weeks (columns) and days of week (rows: 0=Sun/Mon to 6=Sat/Sun)
     * for the GitHub-style contribution graph.
     * numWeeks is typically 12 to 16.
     */
    fun getContributionGridDates(numWeeks: Int = 16): List<List<String>> {
        val totalDays = numWeeks * 7
        val cal = Calendar.getInstance()
        
        // Find the day of week for today (1 = Sunday, 2 = Monday ... 7 = Saturday)
        val currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 (Sun) to 7 (Sat)
        // Offset to end the grid on the current week's Saturday (or Sunday)
        val daysUntilEndOfWeek = Calendar.SATURDAY - currentDayOfWeek
        cal.add(Calendar.DAY_OF_YEAR, daysUntilEndOfWeek)
        
        // Now go back (totalDays - 1) days to start at the top-left of the matrix
        cal.add(Calendar.DAY_OF_YEAR, -(totalDays - 1))
        
        // Build weeks columns: List of weeks, each containing 7 day strings
        val weeks = mutableListOf<List<String>>()
        for (w in 0 until numWeeks) {
            val weekDays = mutableListOf<String>()
            for (d in 0 until 7) {
                weekDays.add(ISO_DATE_FORMAT.format(cal.time))
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            weeks.add(weekDays)
        }
        return weeks
    }

    /**
     * Computes consecutive streak up to today or yesterday.
     */
    fun calculateStreak(completedDates: Set<String>): Int {
        if (completedDates.isEmpty()) return 0
        val cal = Calendar.getInstance()
        var streak = 0
        
        val todayStr = ISO_DATE_FORMAT.format(cal.time)
        val todayCompleted = completedDates.contains(todayStr)
        
        if (todayCompleted) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            // Check if streak was active yesterday
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = ISO_DATE_FORMAT.format(cal.time)
            if (!completedDates.contains(yesterdayStr)) {
                return 0
            }
        }
        
        while (true) {
            val dateStr = ISO_DATE_FORMAT.format(cal.time)
            if (completedDates.contains(dateStr)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    /**
     * Calculates the longest streak in history.
     */
    fun calculateLongestStreak(completedDates: Set<String>): Int {
        if (completedDates.isEmpty()) return 0
        val sortedDates = completedDates.mapNotNull {
            try { ISO_DATE_FORMAT.parse(it) } catch (e: Exception) { null }
        }.sorted()

        if (sortedDates.isEmpty()) return 0

        var maxStreak = 1
        var currentStreak = 1
        val cal = Calendar.getInstance()

        for (i in 1 until sortedDates.size) {
            cal.time = sortedDates[i - 1]
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val expectedNext = ISO_DATE_FORMAT.format(cal.time)
            val actualNext = ISO_DATE_FORMAT.format(sortedDates[i])

            if (expectedNext == actualNext) {
                currentStreak++
                if (currentStreak > maxStreak) {
                    maxStreak = currentStreak
                }
            } else if (ISO_DATE_FORMAT.format(sortedDates[i - 1]) != actualNext) {
                currentStreak = 1
            }
        }
        return maxStreak
    }
}
