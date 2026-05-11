package com.penguin.linguae.feature.statistic.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.Green
import com.penguin.linguae.core.ui.theme.LightPurple
import com.penguin.linguae.core.ui.theme.PrimaryPurple
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.core.ui.theme.Yellow
import com.penguin.linguae.data.model.TodayProgressStats

@Composable
fun MissionProgressCard(todayProgress: TodayProgressStats, modifier: Modifier = Modifier) {
    val animatedOverall by animateFloatAsState(
        targetValue = todayProgress.overallProgress / 100f,
        label = "overallProgress"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, shape = RoundedCornerShape(24.dp)),
        color = SurfaceColor,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nhiệm vụ hôm nay",
                    color = TextDark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(LightPurple)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${todayProgress.overallProgress}%",
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Overall progress bar
            LinearProgressIndicator(
                progress = { animatedOverall },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = PrimaryPurple,
                trackColor = LightPurple,
                gapSize = 0.dp,
                drawStopIndicator = {},
                strokeCap = StrokeCap.Butt
            )

            // Per-task breakdown
            val tasks = todayProgress.tasks ?: emptyList()
            if (tasks.isEmpty()) {
                Text(
                    text = "Chưa có nhiệm vụ hôm nay.",
                    color = TextGray,
                    fontSize = 13.sp
                )
            } else {
                tasks.forEach { task ->
                    val taskLabel = when (task.taskType) {
                        "VOCABULARY_LEARN" -> "Học từ vựng"
                        "FLASHCARD_LEARN"  -> "Luyện Flashcard"
                        "CLOZE_LEARN"      -> "Luyện Cloze"
                        "MATCHING_LEARN"   -> "Luyện Matching"
                        else               -> task.taskType
                    }
                    val taskRatio by animateFloatAsState(
                        targetValue = if (task.targetCount > 0) task.completedCount.toFloat() / task.targetCount else 0f,
                        label = "task_${task.id}"
                    )
                    val isCompleted = task.status == "COMPLETED"

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = taskLabel,
                                color = TextDark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${task.completedCount}/${task.targetCount}",
                                    color = TextGray,
                                    fontSize = 13.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(if (isCompleted) Green.copy(alpha = 0.15f) else Yellow.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isCompleted) "Hoàn thành" else "Đang học",
                                        color = if (isCompleted) Green else Yellow,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        LinearProgressIndicator(
                            progress = { taskRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(999.dp)),
                            color = if (isCompleted) Green else PrimaryPurple,
                            trackColor = LightPurple,
                            gapSize = 0.dp,
                            drawStopIndicator = {},
                            strokeCap = StrokeCap.Butt
                        )
                    }
                }
            }
        }
    }
}
