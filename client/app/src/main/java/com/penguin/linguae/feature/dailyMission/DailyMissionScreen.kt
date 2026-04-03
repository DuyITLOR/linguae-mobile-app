package com.penguin.linguae.feature.dailyMission

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.data.model.DailyMissionWord
import com.penguin.linguae.data.model.DailyTaskSummary
import com.penguin.linguae.data.model.TaskWordsResponse
import com.penguin.linguae.feature.dailyMission.viewmodel.DailyMissionUiState
import com.penguin.linguae.feature.dailyMission.viewmodel.DailyMissionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DailyMissionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToVocabulary: (String) -> Unit,
    onNavigateToFlashcard: (String) -> Unit,
    viewModel: DailyMissionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Refresh when coming back from VocabularyScreen or FlashcardScreen
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadSummary()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (uiState.showCongrats) {
        CongratsScreen(onNavigateBack = onNavigateBack)
        return
    }

    Scaffold(containerColor = AppBackground) { paddingValues ->
        when {
            uiState.isLoading -> LoadingScreen(paddingValues)
            uiState.error != null -> ErrorScreen(
                error = uiState.error!!,
                onRetry = { viewModel.loadSummary() },
                paddingValues = paddingValues
            )
            else -> MissionContent(
                uiState = uiState,
                onNavigateBack = onNavigateBack,
                onVocabularyTaskToggle = { taskId -> viewModel.toggleVocabularyTask(taskId) },
                onFlashcardTaskClick = { taskId -> onNavigateToFlashcard(taskId) },
                onWordClick = { vocabId -> onNavigateToVocabulary(vocabId) },
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
private fun LoadingScreen(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PurpleBlueTheme)
    }
}

@Composable
private fun ErrorScreen(error: String, onRetry: () -> Unit, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Red, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Đã xảy ra lỗi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(8.dp))
        Text(error, fontSize = 14.sp, color = TextGray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = PurpleBlueTheme),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Thử lại", color = Color.White)
        }
    }
}

@Composable
private fun MissionContent(
    uiState: DailyMissionUiState,
    onNavigateBack: () -> Unit,
    onVocabularyTaskToggle: (String) -> Unit,
    onFlashcardTaskClick: (String) -> Unit,
    onWordClick: (String) -> Unit,
    paddingValues: PaddingValues
) {
    val summary = uiState.summary
    val tasks = summary?.tasks ?: emptyList()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            MissionHeader(
                overallProgress = summary?.overallProgress ?: 0,
                onNavigateBack = onNavigateBack
            )
        }
        items(tasks, key = { it.id }) { task ->
            val isFlashcard = task.taskType == "FLASHCARD_LEARN"
            TaskCard(
                task = task,
                isExpanded = !isFlashcard && uiState.expandedTaskId == task.id,
                taskWords = uiState.taskWordsMap[task.id],
                onCardClick = {
                    if (isFlashcard) onFlashcardTaskClick(task.id)
                    else onVocabularyTaskToggle(task.id)
                },
                onWordClick = onWordClick
            )
        }
    }
}

@Composable
private fun MissionHeader(overallProgress: Int, onNavigateBack: () -> Unit) {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("vi"))
    val dateString = dateFormat.format(calendar.time).replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PurpleBlueTheme)
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
            }
            Column {
                Text("Nhiệm vụ hôm nay", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text(dateString, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tiến độ tổng thể", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
            Text("$overallProgress%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { overallProgress / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun TaskCard(
    task: DailyTaskSummary,
    isExpanded: Boolean,
    taskWords: TaskWordsResponse?,
    onCardClick: () -> Unit,
    onWordClick: (String) -> Unit
) {
    val isFlashcard = task.taskType == "FLASHCARD_LEARN"
    val (icon, label) = when (task.taskType) {
        "VOCABULARY_LEARN" -> Pair(Icons.Default.MenuBook, "Học từ vựng")
        "FLASHCARD_LEARN" -> Pair(Icons.Default.Layers, "Luyện Flashcard")
        else -> Pair(Icons.Default.Star, task.taskType)
    }
    val isCompleted = task.status == "COMPLETED"
    val progress = if (task.targetCount > 0) task.completedCount.toFloat() / task.targetCount else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isCompleted) Color(0xFFE8F5E9) else LightPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isCompleted) Color(0xFF2E7D32) else PurpleBlueTheme,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text("${task.completedCount}/${task.targetCount} từ", fontSize = 13.sp, color = TextGray)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(isCompleted = isCompleted)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isFlashcard) Icons.Default.ChevronRight
                                      else if (isExpanded) Icons.Default.ExpandLess
                                      else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (isCompleted) Color(0xFF4CAF50) else PurpleBlueTheme,
                trackColor = AppBackground
            )

            // Word list only for VOCABULARY_LEARN
            if (!isFlashcard) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = AppBackground, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        if (taskWords == null) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = PurpleBlueTheme, modifier = Modifier.size(32.dp))
                            }
                        } else {
                            taskWords.words.forEach { word ->
                                WordItem(word = word, onClick = { onWordClick(word.id) })
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(isCompleted: Boolean) {
    val bgColor = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
    val textColor = if (isCompleted) Color(0xFF2E7D32) else Color(0xFFF57F17)
    val label = if (isCompleted) "Hoàn thành" else "Đang học"
    Box(
        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(bgColor).padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

@Composable
private fun WordItem(word: DailyMissionWord, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (word.isCompleted) Color(0xFFF0FFF0) else AppBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(word.word, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    if (word.partOfSpeech != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(LightPurple)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(word.partOfSpeech, fontSize = 10.sp, color = PurpleBlueTheme, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Text(word.pronunciationText, fontSize = 12.sp, color = PurpleBlueTheme, fontStyle = FontStyle.Italic)
                Spacer(modifier = Modifier.height(4.dp))
                Text(word.meaning, fontSize = 14.sp, color = TextDark.copy(alpha = 0.85f))
                if (word.VocabularyExample.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "\"${word.VocabularyExample[0].sentence}\"",
                        fontSize = 12.sp,
                        color = TextGray,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            if (word.isCompleted) {
                Box(
                    modifier = Modifier.size(30.dp).clip(CircleShape).background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            } else {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun CongratsScreen(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(AppBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape).background(Color(0xFFFFF8E1)),
                contentAlignment = Alignment.Center
            ) {
                Text("🏆", fontSize = 56.sp)
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text("Xuất sắc!", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Bạn đã hoàn thành tất cả\nnhiệm vụ hôm nay rồi!",
                fontSize = 16.sp, color = TextGray, textAlign = TextAlign.Center, lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Hãy quay lại vào ngày mai để tiếp tục học nhé 💪",
                fontSize = 14.sp, color = TextGray, textAlign = TextAlign.Center, lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleBlueTheme)
            ) {
                Text("Về trang chủ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
