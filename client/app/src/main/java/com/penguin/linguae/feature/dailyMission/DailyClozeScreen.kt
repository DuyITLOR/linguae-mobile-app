package com.penguin.linguae.feature.dailyMission

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.penguin.linguae.core.ui.component.ErrorView
import androidx.compose.ui.graphics.Color
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.Black
import com.penguin.linguae.core.ui.theme.Green
import com.penguin.linguae.core.ui.theme.LightGreen
import com.penguin.linguae.feature.dailyMission.viewmodel.DailyClozeViewModel
import com.penguin.linguae.feature.practice.cloze.component.Header
import com.penguin.linguae.feature.practice.cloze.component.OptionItem
import com.penguin.linguae.feature.practice.cloze.component.QuestionHolder

@Composable
fun DailyClozeScreen(
    taskId: String,
    onReturn: () -> Unit
) {
    val viewModel = remember(taskId) { DailyClozeViewModel(taskId) }
    val questionsWithOptions by viewModel.questionsWithOptions.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()
    val selectedOptionId by viewModel.selectedOptionId.collectAsStateWithLifecycle()
    val isAnswered by viewModel.isAnswered.collectAsStateWithLifecycle()
    val isLastQuestion by viewModel.isLastQuestion.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val showRetry by viewModel.showRetry.collectAsStateWithLifecycle()
    val correctCount by viewModel.correctCountState.collectAsStateWithLifecycle()
    val totalQuestion by viewModel.totalQuestionState.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { onReturn() }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = AppBackground) {
        if (showRetry) {
            RetryOverlay(
                correctCount = correctCount,
                totalQuestion = totalQuestion,
                accentColor = Green,
                onRetry = { viewModel.onRetry() },
                onSkip = { viewModel.onSkip() }
            )
            return@Surface
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                ErrorView(message = error ?: "Unknown error", onRetry = { viewModel.fetchQuestions() })
            }
            questionsWithOptions.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Không có câu hỏi nào")
                }
            }
            else -> {
                val current = questionsWithOptions[currentIndex]

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.05f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Header(currentIndex, questionsWithOptions.size, onClick = onReturn) }
                    item { QuestionHolder(current.question) }
                    item {
                        Text(
                            text = "Chọn từ phù hợp để hoàn thành câu:",
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth(0.95f)
                        )
                    }
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(0.95f),
                            verticalArrangement = Arrangement.spacedBy(screenHeight * 0.015f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            current.options.forEach { option ->
                                OptionItem(
                                    text = option.optionText,
                                    isSelected = selectedOptionId == option.id,
                                    isCorrect = if (isAnswered) option.isCorrect else null,
                                    onClick = { viewModel.onOptionSelected(option.id) }
                                )
                            }
                            if (isAnswered) {
                                OutlinedButton(
                                    onClick = {
                                        if (!isLastQuestion) viewModel.onNextQuestionClicked()
                                        else viewModel.onFinish()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LightGreen,
                                        contentColor = Black
                                    ),
                                    border = BorderStroke(2.dp, Green),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = ButtonDefaults.buttonElevation(4.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (!isLastQuestion) "Next Question" else "Hoàn thành",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
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
}
