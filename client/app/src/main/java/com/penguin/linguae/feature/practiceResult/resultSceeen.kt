package com.penguin.linguae.feature.practiceResult

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.data.model.ResultData
import com.penguin.linguae.feature.practiceResult.viewmodel.ResultViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.log


@Composable
fun ResultScreen(
    navController: NavController,
    viewModel: ResultViewModel = viewModel(),
    onReturn: () -> Unit = {}
) {


    val result by viewModel.result.collectAsStateWithLifecycle()

    val (score, total, topicId) = when (val data = result) {
        is ResultData.ClozeResult -> Triple(
            data.score,
            data.total,
            data.topicId
        )
        else -> Triple(0, 0, "")
    }

    val scorePercent = if (total > 0) {
        (score.toFloat() / total * 100).toInt()
    } else 0

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { route ->
            Log.d("RESULT VM", "Navigating to ${Screen.Cloze.createRoute(topicId = topicId)}")
            when(route) {
                Screen.Cloze.route -> {
                    navController.navigate(Screen.Cloze.createRoute(topicId = topicId)) {
                        popUpTo(Screen.ResultScreen.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    // Trophy bounce animation
    val infiniteTransition = rememberInfiniteTransition(label = "trophy")
    val trophyScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophyScale"
    )
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val (scoreLabel, scoreLabelEmoji) = when {
        scorePercent >= 80 -> "Xuất sắc!" to "🌟"
        scorePercent >= 60 -> "Giỏi!"     to "👏"
        scorePercent >= 40 -> "Khá!"      to "💪"
        scorePercent >= 20 -> "Cố lên!"   to "😅"
        else               -> "Thử lại nhé!" to "😬"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(screenHeight * 0.05f)
        ) {

            Text(
                text = "🏆",
                fontSize = 72.sp,
                modifier = Modifier.scale(trophyScale)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score/$total",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ScoreTextColor
                )
                Text(
                    text = "Kết quả luyện tập",
                    fontSize = 15.sp,
                    color = PurpleHint,
                    fontWeight = FontWeight.Normal
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF8B7FF5), PurpleCard)
                        )
                    )
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Điểm số",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "$scorePercent%",
                        color = Color.White,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "$scoreLabel $scoreLabelEmoji",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "$score",
                    icon = "✓",
                    label = "Đúng",
                    valueColor = GreenCorrect,
                    iconColor = GreenCorrect
                )
                StatItem(
                    value = "${total - score}",
                    icon = "✗",
                    label = "Sai",
                    valueColor = RedWrong,
                    iconColor = RedWrong
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { viewModel.onRetryClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleCard
                )
            ) {
                Text(
                    text = "Luyện tập lại",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            OutlinedButton(
                onClick = onReturn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 2.dp,
                    color = PurpleCard
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PurpleDark
                )
            ) {
                Text(
                    text = "📚  Chủ đề khác",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurpleDark
                )
            }
        }
    }
}
@Composable
private fun StatItem(
    value: String,
    icon: String,
    label: String,
    valueColor: Color,
    iconColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = valueColor
            )
            Text(
                text = icon,
                fontSize = 22.sp,
                color = iconColor,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF888888)
        )
    }
}
