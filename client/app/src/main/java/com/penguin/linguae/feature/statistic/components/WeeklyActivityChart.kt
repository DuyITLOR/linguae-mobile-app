package com.penguin.linguae.feature.statistic.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.LightPurple
import com.penguin.linguae.core.ui.theme.PrimaryPurple
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.data.model.DailyActivityStats
import java.util.Calendar

@Composable
fun WeeklyActivityChart(
    activity: List<DailyActivityStats>,
    weekOffset: Int,
    isLoading: Boolean,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, shape = RoundedCornerShape(24.dp)),
        color = SurfaceColor,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Hoạt động trong tuần",
                    color = TextDark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            // Week navigation row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onPrevWeek, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Tuần trước",
                        tint = PrimaryPurple
                    )
                }

                Text(
                    text = monthYearLabel(activity, weekOffset),
                    color = TextGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = onNextWeek,
                    enabled = weekOffset < 0,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Tuần sau",
                        tint = if (weekOffset < 0) PrimaryPurple else TextGray.copy(alpha = 0.3f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = PrimaryPurple,
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Canvas(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                        val count = activity.size.coerceAtLeast(1)
                        val slotWidth = size.width / count
                        val barWidth = slotWidth * 0.5f
                        val maxBarHeight = size.height
                        val cornerR = CornerRadius(8.dp.toPx())

                        activity.forEachIndexed { i, day ->
                            val x = i * slotWidth + (slotWidth - barWidth) / 2f

                            drawRoundRect(
                                color = LightPurple,
                                topLeft = Offset(x, 0f),
                                size = Size(barWidth, maxBarHeight),
                                cornerRadius = cornerR
                            )

                            val ratio = if (day.totalTasks > 0) {
                                day.completedTasks.toFloat() / day.totalTasks.toFloat()
                            } else 0f

                            if (ratio > 0f) {
                                val barHeight = ratio * maxBarHeight
                                drawRoundRect(
                                    color = PrimaryPurple,
                                    topLeft = Offset(x, maxBarHeight - barHeight),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = cornerR
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                activity.forEach { day ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dateToWeekDay(day.date),
                            color = TextGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Text(
                            text = dateToDayNumber(day.date),
                            color = TextDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private fun monthYearLabel(activity: List<DailyActivityStats>, weekOffset: Int): String {
    val midDate = activity.getOrNull(3)?.date ?: activity.lastOrNull()?.date
    if (midDate != null) {
        return try {
            val parts = midDate.split("-")
            val month = parts[1].toInt()
            val year = parts[0].toInt()
            "Tháng $month, $year"
        } catch (e: Exception) {
            weekLabelFallback(weekOffset)
        }
    }
    return weekLabelFallback(weekOffset)
}

private fun weekLabelFallback(weekOffset: Int): String = when {
    weekOffset == 0 -> "Tuần này"
    weekOffset == -1 -> "Tuần trước"
    else -> "${-weekOffset} tuần trước"
}

private fun calendarFromDateStr(dateStr: String): Calendar? = try {
    val parts = dateStr.split("-")
    Calendar.getInstance().apply {
        set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
    }
} catch (e: Exception) { null }

private fun dateToWeekDay(dateStr: String): String {
    val cal = calendarFromDateStr(dateStr) ?: return ""
    return when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "T2"
        Calendar.TUESDAY -> "T3"
        Calendar.WEDNESDAY -> "T4"
        Calendar.THURSDAY -> "T5"
        Calendar.FRIDAY -> "T6"
        Calendar.SATURDAY -> "T7"
        else -> "CN"
    }
}

private fun dateToDayNumber(dateStr: String): String {
    val cal = calendarFromDateStr(dateStr) ?: return ""
    return cal.get(Calendar.DAY_OF_MONTH).toString()
}
