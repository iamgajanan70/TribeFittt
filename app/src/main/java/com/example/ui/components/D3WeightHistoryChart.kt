package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WeightLogEntity
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOnPrimary
import com.example.ui.theme.MonoOnSurface
import com.example.ui.theme.MonoOnSurfaceVariant
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerHigh
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest
import com.example.utils.DateUtils
import kotlin.math.roundToInt

@Composable
fun D3WeightHistoryChart(
    weightLogs: List<WeightLogEntity>,
    targetWeightKg: Float,
    onOpenAddWeightDialog: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    var selectedTimeRange by remember { mutableStateOf("30D") }
    var hoveredLogIndex by remember { mutableStateOf<Int?>(null) }

    // Resolve theme colors in Composable scope
    val primaryColor = MonoPrimary
    val onPrimaryColor = MonoOnPrimary
    val onSurfaceColor = MonoOnSurface
    val onSurfaceVariantColor = MonoOnSurfaceVariant
    val outlineVariantColor = MonoOutlineVariant
    val backgroundColor = MonoBackground
    val surfaceLowestColor = MonoSurfaceLowest
    val surfaceContainerLowColor = MonoSurfaceContainerLow
    val surfaceContainerHighColor = MonoSurfaceContainerHigh

    // Filter and sort weight logs chronologically (oldest to newest)
    val sortedLogs = remember(weightLogs, selectedTimeRange) {
        val chronological = weightLogs.sortedBy { it.timestamp }
        when (selectedTimeRange) {
            "7D" -> chronological.takeLast(7)
            "30D" -> chronological.takeLast(30)
            "90D" -> chronological.takeLast(90)
            else -> chronological
        }
    }

    val currentWeight = sortedLogs.lastOrNull()?.weightKg ?: (if (weightLogs.isNotEmpty()) weightLogs.first().weightKg else 70f)
    val startWeight = sortedLogs.firstOrNull()?.weightKg ?: currentWeight
    val netChange = currentWeight - startWeight
    val minWeight = (sortedLogs.minOfOrNull { it.weightKg } ?: (currentWeight - 2f)).coerceAtMost(targetWeightKg - 1f)
    val maxWeight = (sortedLogs.maxOfOrNull { it.weightKg } ?: (currentWeight + 2f)).coerceAtLeast(targetWeightKg + 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("d3_recharts_weight_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLowColor),
        border = BorderStroke(0.75.dp, outlineVariantColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Time Range Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(primaryColor, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Weight History D3",
                            tint = onPrimaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "BODY WEIGHT HISTORY",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = onSurfaceColor,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Room Database • D3 / Recharts Trend Engine",
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurfaceVariantColor,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onOpenAddWeightDialog,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log Weight",
                        tint = primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Time Range Tabs (7D, 30D, 90D, ALL)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("7D", "30D", "90D", "ALL").forEach { range ->
                    val isSelected = selectedTimeRange == range
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) primaryColor else surfaceLowestColor,
                        border = BorderStroke(
                            0.75.dp,
                            if (isSelected) primaryColor else outlineVariantColor
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        onClick = {
                            selectedTimeRange = range
                            hoveredLogIndex = null
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = range,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) onPrimaryColor else onSurfaceColor,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats KPI Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeightMetricPill(
                    label = "CURRENT",
                    value = "${String.format("%.1f", currentWeight)} kg",
                    modifier = Modifier.weight(1f)
                )
                WeightMetricPill(
                    label = "TARGET",
                    value = "${String.format("%.1f", targetWeightKg)} kg",
                    modifier = Modifier.weight(1f)
                )
                WeightMetricPill(
                    label = "NET CHANGE",
                    value = "${if (netChange > 0) "+" else ""}${String.format("%.1f", netChange)} kg",
                    modifier = Modifier.weight(1f)
                )
                WeightMetricPill(
                    label = "LOGS",
                    value = "${sortedLogs.size}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive D3 / Recharts Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(surfaceLowestColor, RoundedCornerShape(12.dp))
                    .border(0.75.dp, outlineVariantColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                if (sortedLogs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No weight logs recorded yet.\nTap '+' above to log your first weight!",
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurfaceVariantColor,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    val activeHoverIndex = hoveredLogIndex
                    val activeHoveredLog = activeHoverIndex?.let { sortedLogs.getOrNull(it) }

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(sortedLogs) {
                                detectTapGestures(
                                    onTap = { offset ->
                                        if (sortedLogs.isNotEmpty()) {
                                            val stepX = size.width / (sortedLogs.size - 1).coerceAtLeast(1)
                                            val index = (offset.x / stepX).roundToInt().coerceIn(0, sortedLogs.lastIndex)
                                            hoveredLogIndex = if (hoveredLogIndex == index) null else index
                                        }
                                    }
                                )
                            }
                            .pointerInput(sortedLogs) {
                                detectDragGestures(
                                    onDrag = { change, _ ->
                                        if (sortedLogs.isNotEmpty()) {
                                            val stepX = size.width / (sortedLogs.size - 1).coerceAtLeast(1)
                                            val index = (change.position.x / stepX).roundToInt().coerceIn(0, sortedLogs.lastIndex)
                                            hoveredLogIndex = index
                                        }
                                    },
                                    onDragEnd = { /* keep last or clear */ }
                                )
                            }
                    ) {
                        val width = size.width
                        val height = size.height
                        val paddingBottom = 24.dp.toPx()
                        val paddingTop = 16.dp.toPx()
                        val plotHeight = height - paddingBottom - paddingTop

                        val weightRange = (maxWeight - minWeight).coerceAtLeast(1.0f)
                        val points = mutableListOf<Offset>()
                        val stepX = if (sortedLogs.size > 1) width / (sortedLogs.size - 1) else width / 2

                        // Calculate points
                        sortedLogs.forEachIndexed { i, log ->
                            val x = if (sortedLogs.size > 1) i * stepX else width / 2
                            val normalizedY = (log.weightKg - minWeight) / weightRange
                            val y = paddingTop + plotHeight * (1f - normalizedY)
                            points.add(Offset(x, y))
                        }

                        // 1. Draw horizontal guide lines (Min, Max, Target)
                        val targetNormalized = (targetWeightKg - minWeight) / weightRange
                        val targetY = paddingTop + plotHeight * (1f - targetNormalized.coerceIn(0f, 1f))

                        // Target reference line (Dashed)
                        drawLine(
                            color = primaryColor.copy(alpha = 0.4f),
                            start = Offset(0f, targetY),
                            end = Offset(width, targetY),
                            strokeWidth = 1.2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )

                        // 2. Draw smooth Bezier curve & gradient fill
                        if (points.size >= 2) {
                            val curvePath = Path()
                            val fillPath = Path()

                            curvePath.moveTo(points[0].x, points[0].y)
                            fillPath.moveTo(points[0].x, height - paddingBottom)
                            fillPath.lineTo(points[0].x, points[0].y)

                            for (i in 0 until points.size - 1) {
                                val p0 = points[i]
                                val p1 = points[i + 1]
                                val controlX = (p0.x + p1.x) / 2
                                curvePath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                                fillPath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                            }

                            fillPath.lineTo(points.last().x, height - paddingBottom)
                            fillPath.close()

                            // Draw gradient under curve
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.25f),
                                        primaryColor.copy(alpha = 0.02f)
                                    ),
                                    startY = paddingTop,
                                    endY = height - paddingBottom
                                )
                            )

                            // Draw stroke curve
                            drawPath(
                                path = curvePath,
                                color = primaryColor,
                                style = Stroke(
                                    width = 2.5.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            )
                        }

                        // 3. Draw circular data node points
                        points.forEachIndexed { idx, pt ->
                            val isHovered = activeHoverIndex == idx
                            val radius = if (isHovered) 5.5.dp.toPx() else 3.5.dp.toPx()

                            // Outer glow if hovered
                            if (isHovered) {
                                drawCircle(
                                    color = primaryColor.copy(alpha = 0.3f),
                                    radius = radius + 4.dp.toPx(),
                                    center = pt
                                )
                                // Vertical crosshair
                                drawLine(
                                    color = onSurfaceVariantColor.copy(alpha = 0.6f),
                                    start = Offset(pt.x, paddingTop),
                                    end = Offset(pt.x, height - paddingBottom),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                )
                            }

                            // Center dot
                            drawCircle(
                                color = if (isHovered) primaryColor else backgroundColor,
                                radius = radius,
                                center = pt
                            )
                            drawCircle(
                                color = primaryColor,
                                radius = radius,
                                center = pt,
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }
                    }

                    // Hover tooltip overlay
                    if (activeHoveredLog != null && activeHoverIndex != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = primaryColor,
                            border = BorderStroke(0.5.dp, outlineVariantColor),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 4.dp)
                        ) {
                            Text(
                                text = "${String.format("%.1f", activeHoveredLog.weightKg)} kg • ${DateUtils.formatShortDisplay(activeHoveredLog.dateText)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = onPrimaryColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Target legend tag
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = surfaceContainerHighColor,
                        border = BorderStroke(0.5.dp, outlineVariantColor),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 2.dp)
                    ) {
                        Text(
                            text = "Target: ${String.format("%.1f", targetWeightKg)}kg",
                            style = MaterialTheme.typography.labelSmall,
                            color = onSurfaceVariantColor,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    // Date range labels at bottom
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = sortedLogs.firstOrNull()?.let { DateUtils.formatShortDisplay(it.dateText) } ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 9.sp,
                            color = onSurfaceVariantColor
                        )
                        Text(
                            text = sortedLogs.lastOrNull()?.let { DateUtils.formatShortDisplay(it.dateText) } ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 9.sp,
                            color = onSurfaceVariantColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action: Log today's weight button
            OutlinedButton(
                onClick = onOpenAddWeightDialog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(0.75.dp, primaryColor)
            ) {
                Icon(
                    imageVector = Icons.Default.MonitorWeight,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LOG NEW WEIGHT ENTRY",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun WeightMetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MonoSurfaceLowest,
        border = BorderStroke(0.75.dp, MonoOutlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = MonoOnSurface,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MonoOnSurfaceVariant,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
        }
    }
}
