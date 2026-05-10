package com.penguin.linguae.feature.dailyMission

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.penguin.linguae.core.ui.component.ErrorView
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.PurpleLight
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.feature.dailyMission.viewmodel.DailyMatchingItem
import com.penguin.linguae.feature.dailyMission.viewmodel.DailyMatchingViewModel
import com.penguin.linguae.feature.practice.matching.component.MatchingHeader

private val pairColors = listOf(
    Color(0xFF4CAF50) to Color(0xFFE8F5E9),
    Color(0xFF2196F3) to Color(0xFFE3F2FD),
    Color(0xFFFF9800) to Color(0xFFFFF3E0),
    Color(0xFF9C27B0) to Color(0xFFF3E5F5),
    Color(0xFFE91E63) to Color(0xFFFCE4EC),
)

@Composable
fun DailyMatchingScreen(
    taskId: String,
    onReturn: () -> Unit
) {
    val viewModel = remember(taskId) { DailyMatchingViewModel(taskId) }

    val leftItems by viewModel.leftItems.collectAsStateWithLifecycle()
    val rightItems by viewModel.rightItems.collectAsStateWithLifecycle()
    val selectedLeftId by viewModel.selectedLeftId.collectAsStateWithLifecycle()
    val selectedRightId by viewModel.selectedRightId.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isEvaluated by viewModel.isEvaluated.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { onReturn() }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = AppBackground) {
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PurplePrimary)
                }
            }
            error != null -> {
                ErrorView(message = error ?: "Unknown error", onRetry = { viewModel.fetchQuestions() })
            }
            leftItems.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Không có câu hỏi nào")
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    MatchingHeader(currentIndex = 0, totalQuestions = 1, onClick = onReturn)

                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nối từ phù hợp",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Row(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(leftItems) { item ->
                                    DailyMatchingItemCard(
                                        item = item,
                                        isSelected = selectedLeftId == item.id,
                                        isEvaluated = isEvaluated,
                                        onClick = { viewModel.onLeftItemSelected(item.id) }
                                    )
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(rightItems) { item ->
                                    DailyMatchingItemCard(
                                        item = item,
                                        isSelected = selectedRightId == item.id,
                                        isEvaluated = isEvaluated,
                                        onClick = { viewModel.onRightItemSelected(item.id) }
                                    )
                                }
                            }
                        }

                        if (viewModel.allPaired && !isEvaluated) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.onFinish() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                            ) {
                                Text("Hoàn thành", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyMatchingItemCard(
    item: DailyMatchingItem,
    isSelected: Boolean,
    isEvaluated: Boolean,
    onClick: () -> Unit
) {
    val (borderColor, backgroundColor, textColor) = when {
        isEvaluated && item.isCorrect == true ->
            Triple(Color(0xFF27AE60), Color(0xFFE8F5E9), Color(0xFF27AE60))
        isEvaluated && item.isCorrect == false ->
            Triple(Color(0xFFE53935), Color(0xFFFFEBEE), Color(0xFFE53935))
        item.pairIndex != null -> {
            val (border, bg) = pairColors[item.pairIndex % pairColors.size]
            Triple(border, bg, border)
        }
        isSelected -> Triple(PurplePrimary, PurpleLight, TextDark)
        else -> Triple(Color(0xFFE0E0E0), Color.White, TextDark)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !isEvaluated) { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.text,
            color = textColor,
            fontWeight = if (isSelected || item.pairIndex != null) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
