package com.penguin.linguae.feature.statistic.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun WeeklyActivityChart(activity: List<DailyActivityStats>, modifier: Modifier = Modifier) {
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
            Text(
                text = "Hoạt động tuần này",
                color = TextDark,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                val count = activity.size.coerceAtLeast(1)
                val slotWidth = size.width / count
                val barWidth = slotWidth * 0.5f
                val maxBarHeight = size.height
                val cornerR = CornerRadius(8.dp.toPx())

                activity.forEachIndexed { i, day ->
                    val x = i * slotWidth + (slotWidth - barWidth) / 2f

                    // Track (background bar)
                    drawRoundRect(
                        color = LightPurple,
                        topLeft = Offset(x, 0f),
                        size = Size(barWidth, maxBarHeight),
                        cornerRadius = cornerR
                    )

                    // Filled bar
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

            // Day labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                activity.forEach { day ->
                    Text(
                        text = dateToLabel(day.date),
                        color = TextGray,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun dateToLabel(dateStr: String): String {
    return try {
        val parts = dateStr.split("-")
        val cal = Calendar.getInstance().apply {
            set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
        }
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "T2"
            Calendar.TUESDAY -> "T3"
            Calendar.WEDNESDAY -> "T4"
            Calendar.THURSDAY -> "T5"
            Calendar.FRIDAY -> "T6"
            Calendar.SATURDAY -> "T7"
            else -> "CN"
        }
    } catch (e: Exception) {
        ""
    }
}
