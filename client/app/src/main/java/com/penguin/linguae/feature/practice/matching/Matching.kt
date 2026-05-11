package com.penguin.linguae.feature.practice.matching

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.feature.practice.matching.component.MatchingHeader
import com.penguin.linguae.feature.practice.matching.viewmodel.MatchingItem
import com.penguin.linguae.feature.practice.matching.viewmodel.MatchingViewModel
import com.penguin.linguae.core.ui.component.ErrorView

@Composable
fun MatchingScreen(
    navController: NavController,
    topicId: String,
    viewModel: MatchingViewModel = MatchingViewModel(topicId),
    onReturn: () -> Unit = {}
) {
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()
    val leftItems by viewModel.leftItems.collectAsStateWithLifecycle()
    val rightItems by viewModel.rightItems.collectAsStateWithLifecycle()
    val selectedLeftId by viewModel.selectedLeftId.collectAsStateWithLifecycle()
    val selectedRightId by viewModel.selectedRightId.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { route ->
            navController.navigate(route)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppBackground
    ) {
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PurplePrimary)
                }
            }
            error != null -> {
                ErrorView(
                    message = error ?: "Unknown error",
                    onRetry = { viewModel.fetchMatchingQuestions(topicId) }
                )
            }
            questions.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Không có câu hỏi nào")
                }
            }
            else -> {
                val currentQuestion = questions[currentIndex]
                val allMatched = leftItems.all { it.isMatched }

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MatchingHeader(
                        currentIndex = currentIndex,
                        totalQuestions = questions.size,
                        onClick = onReturn
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentQuestion.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Left Column
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(leftItems) { item ->
                                    MatchingItemCard(
                                        item = item,
                                        isSelected = selectedLeftId == item.id,
                                        onClick = { viewModel.onLeftItemSelected(item.id) }
                                    )
                                }
                            }

                            // Right Column
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(rightItems) { item ->
                                    MatchingItemCard(
                                        item = item,
                                        isSelected = selectedRightId == item.id,
                                        onClick = { viewModel.onRightItemSelected(item.id) }
                                    )
                                }
                            }
                        }

                        if (allMatched) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    if (currentIndex < questions.size - 1) {
                                        viewModel.onNextQuestion()
                                    } else {
                                        viewModel.onResultClicked()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                            ) {
                                Text(
                                    text = if (currentIndex < questions.size - 1) "Câu tiếp theo" else "Xem kết quả",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchingItemCard(
    item: MatchingItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        item.isMatched -> Color(0xFFE7F9EF)
        item.isCorrect == false -> Red
        isSelected -> PurpleLight
        else -> Color.White
    }
    
    val borderColor = when {
        item.isMatched -> Color(0xFF27AE60)
        isSelected -> PurplePrimary
        else -> Color(0xFFE0E0E0)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !item.isMatched) { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.text,
            color = if (item.isMatched) Color(0xFF27AE60) else TextDark,
            fontWeight = if (isSelected || item.isMatched) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
