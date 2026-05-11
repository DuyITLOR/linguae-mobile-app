package com.penguin.linguae.feature.learning

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.DailyMissionCache
import com.penguin.linguae.data.model.FlashcardReviewStatus
import com.penguin.linguae.data.repository.DailyMissionRepository
import com.penguin.linguae.data.repository.FlashcardRepository
import com.penguin.linguae.feature.learning.viewmodel.FlashcardDetailViewModel
import kotlinx.coroutines.launch

// ✅ data class
data class Card(
    val id: String,
    val word: String,
    val meaning: String,
    val level: String = "BEGINNER" // Added level
)

@Composable
fun FlashcardScreen(
    topicId: String,
    onBack: () -> Unit,
    viewModel: FlashcardDetailViewModel = viewModel(),
    taskId: String? = null
) {
    val topic by viewModel.topic.collectAsState()

    LaunchedEffect(topicId, taskId) {
        if (taskId == null && topicId.isNotBlank()) {
            viewModel.init(topicId)
        }
    }

    // If opened from daily mission, use cached task words; otherwise use topic vocabulary from API.
    val taskWords = remember(taskId) {
        taskId?.let { DailyMissionCache.getTaskWords(it) }
    }

    val apiCards = remember(topic) {
        topic?.Vocabulary?.map { vocab -> Card(vocab.id, vocab.word, vocab.meaning, topic?.level ?: "BEGINNER") } ?: emptyList()
    }

    val cards = remember(taskWords, apiCards) {
        taskWords?.words?.map { Card(it.id, it.word, it.meaning, topic?.level ?: "BEGINNER") } ?: apiCards
    }

    val dailyMissionRepository = remember { if (taskId != null) DailyMissionRepository() else null }
    val flashcardRepository = remember { FlashcardRepository() }
    val scope = rememberCoroutineScope()

    var showMeaning by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf(1) }

    val hasCards = cards.isNotEmpty()
    val index = if (hasCards) (current - 1).coerceIn(0, cards.lastIndex) else 0
    val headerTitle = if (taskId != null) {
        "Flashcard - Nhiệm vụ hôm nay"
    } else {
        topic?.title ?: "Flashcard"
    }

    val rotation by animateFloatAsState(
        targetValue = if (showMeaning) 180f else 0f,
        animationSpec = tween(400),
        label = ""
    )

    fun advanceCard() {
        if (!hasCards) return

        // Always mark current word as complete (including last card)
        if (taskId != null && index < cards.size) {
            scope.launch {
                dailyMissionRepository?.completeWord(taskId, cards[index].id)
            }
        }
        if (current < cards.size) {
            current++
            showMeaning = false
        } else {
            // Last card — navigate back so DailyMissionScreen refreshes via ON_RESUME
            onBack()
        }
    }

    fun reviewCurrentCard(status: FlashcardReviewStatus) {
        if (!hasCards) return

        val currentVocabularyId = cards[index].id

        scope.launch {
            flashcardRepository.reviewFlashcard(currentVocabularyId, status)
        }

        advanceCard()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F2F8)) // Changed to match Toeic list background
            .padding(bottom = 16.dp) // Removed padding start, end to have full-width header
    ) {

        // 🔹 Header matching ToeicMockTestListScreen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF8F6FF),
                            Color(0xFFF2EEFF)
                        )
                    )
                )
                .padding(start = 20.dp, end = 20.dp, top = 44.dp, bottom = 12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E1E2D)
                        )
                    }
                    Text(
                        text = headerTitle,
                        color = Color(0xFF1E1E2D),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Progress section wrapped in a padded column
        Column(modifier = Modifier.padding(horizontal = 20.dp).weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (hasCards) current else 0} / ${cards.size} từ", 
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
                if (hasCards) {
                    LevelBadge(level = cards[index].level)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(80))
                .background(Color(0xFFD6D3E0))
        ) {
            LinearProgressIndicator(
                progress = if (hasCards) current.toFloat() / cards.size.toFloat() else 0f,
                color = Color(0xFFFF8A65),
                trackColor = Color.Transparent,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 🔥 FLASHCARD FLIP
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12 * density
                }
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF3F3F3))
                    .clickable { showMeaning = !showMeaning },
                contentAlignment = Alignment.Center
            ) {
                val isFront = rotation <= 90f

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.graphicsLayer {
                        rotationY = if (isFront) 0f else 180f
                    }
                ) {
                    Text(
                        text = when {
                            !hasCards -> "Chưa có từ vựng"
                            isFront -> cards[index].word
                            else -> cards[index].meaning
                        },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C63FF)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when {
                            !hasCards -> "Vui lòng thêm từ vựng cho chủ đề này"
                            isFront -> "Nhấn để xem nghĩa"
                            else -> "Nhấn để ẩn nghĩa"
                        },
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // 🔹 Button xem nghĩa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF6C63FF), Color(0xFF7B6DFF)))
                )
                .clickable(enabled = hasCards) { showMeaning = true },
            contentAlignment = Alignment.Center
        ) {
            Text("👁 Xem nghĩa", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { reviewCurrentCard(FlashcardReviewStatus.LEARNING) },
                enabled = hasCards,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF8D7DA)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("❌ Chưa nhớ", color = Color(0xFFD9534F), fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { reviewCurrentCard(FlashcardReviewStatus.MASTERED) },
                enabled = hasCards,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4EDDA)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("✔ Đã nhớ", color = Color(0xFF28A745), fontWeight = FontWeight.Bold)
            }
        }
        }
    }
}

@Composable
private fun LevelBadge(level: String) {
    val color = when (level.uppercase()) {
        "BEGINNER" -> Color(0xFF27AE60)
        "INTERMEDIATE" -> Color(0xFFE67E22)
        "ADVANCED" -> Color(0xFFE74C3C)
        else -> Color(0xFF6B7280)
    }
    val label = when (level.uppercase()) {
        "BEGINNER" -> "Beginner"
        "INTERMEDIATE" -> "Intermediate"
        "ADVANCED" -> "Advanced"
        else -> level
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

