package com.penguin.linguae.feature.toeic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.PageBg
import com.penguin.linguae.core.ui.theme.PurpleLight
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.feature.toeic.viewmodel.ToeicPart
import com.penguin.linguae.feature.toeic.viewmodel.ToeicTestDetailViewModel
import com.penguin.linguae.feature.toeic.viewmodel.ToeicTestUiState

@Composable
fun ToeicTest(
    onBack: () -> Unit,
    toeicId: String,
    viewModel: ToeicTestDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(toeicId) {
        viewModel.initialize(toeicId)
    }

    Scaffold(
        containerColor = PageBg,
        bottomBar = {
            FooterPartSwitcher(
                selectedPart = uiState.selectedPart,
                onSelectPart = { part -> viewModel.onSelectPart(toeicId, part) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            QuestionProgressBar(
                answeredCount = uiState.answeredQuestions,
                totalQuestions = uiState.totalQuestions
            )
            Spacer(modifier = Modifier.height(10.dp))
            Header(
                currentQuestion = uiState.currentQuestionIndex + 1,
                totalQuestions = uiState.totalQuestions,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(16.dp))

            PartBadge(uiState.selectedPart)
            Spacer(modifier = Modifier.height(10.dp))

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PurplePrimary)
                    }
                }

                uiState.errorMessage != null -> {
                    ErrorState(
                        message = uiState.errorMessage.orEmpty(),
                        onRetry = { viewModel.onSelectPart(toeicId, uiState.selectedPart) }
                    )
                }

                else -> {
                    QuestionSection(
                        uiState = uiState,
                        onSelectAnswer = viewModel::onSelectAnswer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = viewModel::onPreviousQuestion,
                            enabled = uiState.currentQuestionIndex > 0,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2DFFF)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = null,
                                tint = PurplePrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Previous", color = PurplePrimary, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = viewModel::onNextQuestion,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Next",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    QuestionJumpBar(
                        totalQuestions = uiState.totalQuestions,
                        currentQuestionIndex = uiState.currentQuestionIndex,
                        answeredCount = uiState.answeredQuestions,
                        answeredIndexes = when (uiState.selectedPart) {
                            ToeicPart.PART_5 -> uiState.part5Answers.keys
                            ToeicPart.PART_6 -> uiState.part6Answers.keys
                        },
                        onQuestionClick = viewModel::jumpToQuestion
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionProgressBar(
    answeredCount: Int,
    totalQuestions: Int
) {
    val progress = if (totalQuestions == 0) 0f else answeredCount.toFloat() / totalQuestions.toFloat()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = PurplePrimary,
            trackColor = Color(0xFFE4E2EF)
        )
        Text(
            text = "$answeredCount/$totalQuestions answered",
            color = TextGray,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun Header(
    currentQuestion: Int,
    totalQuestions: Int,
    onBack: () -> Unit
) {
    val safeTotal = totalQuestions.coerceAtLeast(1)
    val safeCurrent = currentQuestion.coerceIn(1, safeTotal)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = TextDark
            )
        }
        Text(
            text = "Mock Test 1 Practice",
            color = TextDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = String.format("%02d:%02d", safeCurrent, safeTotal),
            color = PurplePrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
    Text(
        text = "Question $safeCurrent/$safeTotal",
        color = TextGray,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    )
}

@Composable
private fun PartBadge(part: ToeicPart) {
    Box(
        modifier = Modifier
            .background(PurpleLight, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = if (part == ToeicPart.PART_5) "PART 5" else "PART 6",
            color = PurplePrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuestionSection(
    uiState: ToeicTestUiState,
    onSelectAnswer: (Int) -> Unit
) {
    val question = when (uiState.selectedPart) {
        ToeicPart.PART_5 -> uiState.part5Questions.getOrNull(uiState.currentQuestionIndex)
        ToeicPart.PART_6 -> null
    }

    val options = when (uiState.selectedPart) {
        ToeicPart.PART_5 -> question?.options.orEmpty()
        ToeicPart.PART_6 -> uiState.part6Questions
            .getOrNull(uiState.currentQuestionIndex)
            ?.readingPart6Options
            ?.firstOrNull()
            ?.option
            .orEmpty()
    }

    val questionText = when (uiState.selectedPart) {
        ToeicPart.PART_5 -> question?.question
        ToeicPart.PART_6 -> uiState.part6Questions.getOrNull(uiState.currentQuestionIndex)?.question
    }.orEmpty()

    if (questionText.isBlank()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No question available", color = TextGray)
        }
        return
    }

    Text(
        text = questionText,
        color = TextDark,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    )

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        itemsIndexed(options) { index, option ->
            AnswerItem(
                label = ('A' + index).toString(),
                content = option,
                isSelected = uiState.selectedAnswerIndex == index,
                onClick = { onSelectAnswer(index) }
            )
        }
    }
}

@Composable
private fun AnswerItem(
    label: String,
    content: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) Color(0xFFDCD6FF) else Color(0xFFF3F2FA)
    val borderColor = if (isSelected) PurplePrimary else Color.Transparent
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor, RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = PurplePrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = content,
            color = TextDark,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun QuestionJumpBar(
    totalQuestions: Int,
    currentQuestionIndex: Int,
    answeredCount: Int,
    answeredIndexes: Set<Int>,
    onQuestionClick: (Int) -> Unit
) {
    if (totalQuestions <= 0) return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Question Navigator ($answeredCount/$totalQuestions)",
            color = TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(totalQuestions) { index ->
                val selected = index == currentQuestionIndex
                val isAnswered = answeredIndexes.contains(index)
                val bg = if (selected) PurplePrimary else Color(0xFFEAE8F4)
                val textColor = if (selected) Color.White else TextDark
                Box(
                    modifier = Modifier
                        .background(bg, RoundedCornerShape(10.dp))
                        .clickable { onQuestionClick(index) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        color = textColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isAnswered) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                                .background(Color(0xFF22C55E), RoundedCornerShape(999.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Answered",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FooterPartSwitcher(
    selectedPart: ToeicPart,
    onSelectPart: (ToeicPart) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FooterPartItem(
            label = "PART 5",
            selected = selectedPart == ToeicPart.PART_5,
            onClick = { onSelectPart(ToeicPart.PART_5) },
            modifier = Modifier.weight(1f)
        )
        FooterPartItem(
            label = "PART 6",
            selected = selectedPart == ToeicPart.PART_6,
            onClick = { onSelectPart(ToeicPart.PART_6) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FooterPartItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) PurplePrimary else Color(0xFFF2F1FA)
    val textColor = if (selected) Color.White else TextGray
    Box(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = message,
            color = Color(0xFFD64545),
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Try again", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToeicTestPreview() {
    ToeicTest(
        onBack = {},
        toeicId = "preview"
    )
}
