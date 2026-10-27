package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalFitnessColors
import com.example.ui.theme.MonoBackground
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoSurfaceContainerHigh
import com.example.ui.theme.MonoSurfaceContainerLow
import com.example.ui.theme.MonoSurfaceLowest

@Composable
fun shimmerBrush(targetValue: Float = 1000f): Brush {
    val isDark = LocalFitnessColors.current.isDark
    val shimmerColors = if (isDark) {
        listOf(
            Color(0xFF22222A),
            Color(0xFF33333E),
            Color(0xFF22222A)
        )
    } else {
        listOf(
            Color(0xFFE8E8E8),
            Color(0xFFF6F6F6),
            Color(0xFFE8E8E8)
        )
    }

    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation, y = translateAnimation)
    )
}

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val brush = shimmerBrush()
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun DashboardSkeletonScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MonoBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .testTag("dashboard_skeleton"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Profile Skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonBox(modifier = Modifier.width(180.dp).height(24.dp))
                    SkeletonBox(modifier = Modifier.width(120.dp).height(16.dp), shape = RoundedCornerShape(12.dp))
                }
                SkeletonBox(modifier = Modifier.size(48.dp), shape = CircleShape)
            }

            // Steps & Activity Card Skeleton
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(0.75.dp, MonoOutlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SkeletonBox(modifier = Modifier.width(130.dp).height(16.dp))
                        SkeletonBox(modifier = Modifier.width(80.dp).height(24.dp), shape = RoundedCornerShape(6.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            SkeletonBox(modifier = Modifier.width(110.dp).height(32.dp))
                            SkeletonBox(modifier = Modifier.width(140.dp).height(12.dp))
                        }
                        SkeletonBox(modifier = Modifier.width(90.dp).height(20.dp))
                    }
                    SkeletonBox(modifier = Modifier.fillMaxWidth().height(6.dp), shape = RoundedCornerShape(3.dp))
                }
            }

            // 2x2 Grid Metrics Skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(0.75.dp, MonoOutlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SkeletonBox(modifier = Modifier.width(70.dp).height(12.dp))
                        SkeletonBox(modifier = Modifier.width(90.dp).height(24.dp))
                        SkeletonBox(modifier = Modifier.fillMaxWidth().height(4.dp), shape = RoundedCornerShape(2.dp))
                    }
                }

                // Card 2
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(0.75.dp, MonoOutlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SkeletonBox(modifier = Modifier.width(80.dp).height(12.dp))
                        SkeletonBox(modifier = Modifier.width(75.dp).height(24.dp))
                        SkeletonBox(modifier = Modifier.fillMaxWidth().height(4.dp), shape = RoundedCornerShape(2.dp))
                    }
                }
            }

            // Sleep Card Skeleton
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(0.75.dp, MonoOutlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SkeletonBox(modifier = Modifier.width(140.dp).height(14.dp))
                        SkeletonBox(modifier = Modifier.width(50.dp).height(18.dp), shape = RoundedCornerShape(6.dp))
                    }
                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SkeletonBox(modifier = Modifier.width(80.dp).height(30.dp))
                        SkeletonBox(modifier = Modifier.width(80.dp).height(30.dp))
                        SkeletonBox(modifier = Modifier.width(80.dp).height(30.dp))
                    }
                }
            }

            // Habits Card Skeleton
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoSurfaceLowest),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(0.75.dp, MonoOutlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SkeletonBox(modifier = Modifier.width(160.dp).height(16.dp))
                        SkeletonBox(modifier = Modifier.width(60.dp).height(14.dp))
                    }
                    HorizontalDivider(color = MonoOutlineVariant.copy(alpha = 0.4f))

                    repeat(4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SkeletonBox(modifier = Modifier.size(20.dp), shape = RoundedCornerShape(4.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    SkeletonBox(modifier = Modifier.width(140.dp).height(14.dp))
                                    SkeletonBox(modifier = Modifier.width(90.dp).height(10.dp))
                                }
                            }
                            SkeletonBox(modifier = Modifier.size(24.dp), shape = CircleShape)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
