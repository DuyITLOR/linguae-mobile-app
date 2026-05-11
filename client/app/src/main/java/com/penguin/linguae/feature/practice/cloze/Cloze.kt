package com.penguin.linguae.feature.practice.cloze

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.core.ui.component.ErrorView
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.BorderColor
import com.penguin.linguae.core.ui.theme.GreenCorrect
import com.penguin.linguae.core.ui.theme.PurpleLight
import com.penguin.linguae.core.ui.theme.PurpleMid
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.RedWrong
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.feature.practice.cloze.component.ClozeOptions
import com.penguin.linguae.feature.practice.cloze.viewmodel.ClozeViewModel
import com.penguin.linguae.feature.practice.cloze.viewmodel.ClozeViewModelFactory

@Composable
fun ClozeScreen(
    navController: NavController,
    topicId: String = "",
    viewModel: ClozeViewModel = viewModel(factory = ClozeViewModelFactory(topicId)),
    onReturn: () -> Unit = {}
) {
    val questionsWithOptions by viewModel.shuffleQuestionsWithOptions.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { route ->
            navController.navigate(route) {
                popUpTo(Screen.Cloze.route) { inclusive = true }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppBackground
    ) {
        when {
            isLoading -> LoadingState()
            error != null -> ErrorView(
                message = error ?: "Unknown error",
                onRetry = { viewModel.fetchQuestionsWithOptionsByTopic(topicId) }
            )
            questionsWithOptions.isEmpty() -> EmptyState()
            else -> {
                val current = questionsWithOptions[currentIndex]
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.03f)
                ) {
                    item { Spacer(Modifier.height(12.dp)) }
                    item {
                        ClozeHeader(
                            currentIndex = currentIndex,
                            total = questionsWithOptions.size,
                            onBack = onReturn
                        )
                    }
                    item { QuestionCard(question = current.question.question) }
                    item {
                        Text(
                            text = "Chọn từ phù hợp để hoàn thành câu",
                            fontSize = 13.sp,
                            color = TextGray,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.3.sp
                        )
                    }
                    item {
                        ClozeOptions(
                            screenHeight = screenHeight,
                            options = current.options,
                            viewModel = viewModel
                        )
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = PurplePrimary,
            strokeWidth = 2.dp,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "📭", fontSize = 40.sp)
            Text(
                text = "Không có câu hỏi nào",
                color = TextGray,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun ClozeHeader(currentIndex: Int, total: Int, onBack: () -> Unit) {
    val progress = if (total > 0) (currentIndex + 1).toFloat() / total.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceColor)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = TextDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "${currentIndex + 1} / $total",
                color = TextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PurpleLight)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = PurplePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(BorderColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(listOf(PurplePrimary, PurpleMid))
                    )
            )
        }
    }
}

@Composable
private fun QuestionCard(question: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceColor)
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary)
                )
                Text(
                    text = "Đọc câu sau",
                    fontSize = 11.sp,
                    color = PurplePrimary,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
            }

            Text(
                text = question,
                color = TextDark,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 26.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun ClozeOptionItem(
    text: String,
    state: OptionState = OptionState.Idle,
    onClick: () -> Unit
) {
    val correctBg   = Color(0xFF22C55E)  // Duolingo-style solid green
    val wrongBg     = Color(0xFFEF4444)  // Solid red
    val selectedBg  = PurplePrimary

    val isFilled = state == OptionState.Correct || state == OptionState.Wrong || state == OptionState.Selected

    val bgColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.Idle     -> SurfaceColor
            OptionState.Selected -> selectedBg
            OptionState.Correct  -> correctBg
            OptionState.Wrong    -> wrongBg
        },
        animationSpec = tween(250),
        label = "bg"
    )

    val borderColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.Idle     -> BorderColor
            OptionState.Selected -> selectedBg
            OptionState.Correct  -> correctBg
            OptionState.Wrong    -> wrongBg
        },
        animationSpec = tween(250),
        label = "border"
    )

    val textColor by animateColorAsState(
        targetValue = if (isFilled) Color.White else TextDark,
        animationSpec = tween(250),
        label = "text"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = if (isFilled) FontWeight.SemiBold else FontWeight.Medium
        )

        if (state == OptionState.Correct || state == OptionState.Wrong) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (state == OptionState.Correct) "✓" else "✗",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

enum class OptionState { Idle, Selected, Correct, Wrong }