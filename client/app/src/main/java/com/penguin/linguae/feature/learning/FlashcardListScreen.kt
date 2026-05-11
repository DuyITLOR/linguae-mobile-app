package com.penguin.linguae.feature.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.PageBg
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.SearchBg
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.feature.learning.viewmodel.FlashcardViewModel
import kotlin.math.abs

private data class FlashcardTopicUi(
    val id: String,
    val title: String,
    val level: String,
    val learnedWords: Int,
    val totalWords: Int,
    val progress: Float,
    val accent: Color,
    val icon: ImageVector
)

internal val TopicAccentPalette = listOf(
    Color(0xFF5A67D8),
    Color(0xFF5468FF),
    Color(0xFF6A5AE0),
    Color(0xFF4F7EF7),
    Color(0xFF3E8ED0),
    Color(0xFF5D79E6),
    Color(0xFF7A63EC),
    Color(0xFF4D8BFF)
)

internal val TopicIconPalette = listOf(
    Icons.Default.MenuBook,
    Icons.Default.AutoStories,
    Icons.Default.LocalLibrary,
    Icons.Default.School
)

internal fun topicColorFor(seed: String): Color {
    val index = abs(seed.hashCode()) % TopicAccentPalette.size
    return TopicAccentPalette[index]
}

internal fun topicIconFor(seed: String): ImageVector {
    val index = abs(seed.hashCode() / 31) % TopicIconPalette.size
    return TopicIconPalette[index]
}

@Composable
fun FlashcardListScreen(
    flashcardViewModel: FlashcardViewModel = viewModel(),
    onTopicClick: (String) -> Unit = {},
    onBack: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                flashcardViewModel.refreshTopics()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var query by remember { mutableStateOf("") }

    val topics by flashcardViewModel.topics.collectAsState()

    val uiTopics = topics.map {
        val seed = "${it.id}_${it.title}_${it.level}"
        val accentColor = topicColorFor(seed)
        val progress = if (it.totalWords > 0) it.learnedWords.toFloat() / it.totalWords.toFloat() else 0f
        FlashcardTopicUi(
            id = it.id,
            title = it.title,
            level = it.level,
            learnedWords = it.learnedWords,
            totalWords = it.totalWords,
            progress = progress,
            accent = accentColor,
            icon = topicIconFor(seed)
        )
    }

    val filteredTopics = remember(query, uiTopics) {
        val keyword = query.trim()
        if (keyword.isEmpty()) {
            uiTopics
        } else {
            uiTopics.filter {
                it.title.contains(keyword, ignoreCase = true) ||
                        it.level.contains(keyword, ignoreCase = true)
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val compact = maxWidth < 360.dp
        val contentMaxWidth = if (maxWidth >= 680.dp) 560.dp else Dp.Unspecified

        Scaffold(containerColor = PageBg) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding()),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 14.dp)
            ) {
                item {
                    Header(
                        onBack = onBack,
                        compact = compact,
                        totalTopics = topics.size
                    )
                }

                item {
                    SearchInput(
                        query = query,
                        onQueryChange = { query = it },
                        compact = compact,
                        contentMaxWidth = contentMaxWidth
                    )
                }

                item {
                    Text(
                        text = "CHỦ ĐỀ PHỔ BIẾN",
                        color = PurplePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = contentMaxWidth)
                            .padding(horizontal = if (compact) 16.dp else 20.dp, vertical = 4.dp)
                    )
                }

                items(filteredTopics) { topic ->
                    FlashcardCard(
                        topic = topic,
                        compact = compact,
                        contentMaxWidth = contentMaxWidth,
                        onClick = { onTopicClick(topic.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    onBack: () -> Unit,
    compact: Boolean,
    totalTopics: Int
) {
    val horizontalPadding = if (compact) 16.dp else 20.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4E60EF),
                        PurpleDark,
                        Color(0xFF7A63EC)
                    )
                )
            )
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = 44.dp,
                bottom = 52.dp
            )
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 82.dp else 104.dp)
                .align(Alignment.TopEnd)
                .offset(x = 16.dp, y = (-12).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
        )

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back button",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Chủ đề Flash Card",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (compact) 24.sp else 27.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Luyện tập từ vựng mỗi ngày!",
                color = Color.White.copy(alpha = 0.74f),
                fontSize = if (compact) 13.sp else 14.sp,
                modifier = Modifier.padding(start = 48.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .padding(start = 48.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$totalTopics CHỦ ĐỀ",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SearchInput(
    query: String,
    onQueryChange: (String) -> Unit,
    compact: Boolean,
    contentMaxWidth: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = contentMaxWidth)
            .padding(horizontal = if (compact) 16.dp else 20.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Tìm kiếm chủ đề ...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = PurplePrimary
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-22).dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SearchBg,
                unfocusedContainerColor = SearchBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PurplePrimary
            )
        )
    }
}

@Composable
private fun FlashcardCard(
    topic: FlashcardTopicUi,
    compact: Boolean,
    contentMaxWidth: Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = contentMaxWidth)
            .padding(horizontal = if (compact) 16.dp else 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFEEEEF2),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(if (compact) 14.dp else 16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 44.dp else 48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(topic.accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = topic.icon,
                        contentDescription = "Book",
                        tint = topic.accent,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Column {
                        Text(
                            text = topic.title,
                            color = TextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (compact) 16.sp else 17.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        LevelBadge(level = topic.level)
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "next",
                        tint = Color(0xFFCCCCD6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = topic.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = topic.accent,
                trackColor = Color(0xFFF0F0F5)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tiến độ: ${topic.learnedWords}/${topic.totalWords} từ",
                color = TextGray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
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