package com.penguin.linguae.feature.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.PageBg
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurpleDeep
import com.penguin.linguae.core.ui.theme.PurpleMid
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.feature.learning.viewmodel.TopicViewModel

@Composable
fun TopicScreen(
    onTopicClick: (String, String) -> Unit = { _, _ -> },
    viewModel: TopicViewModel = viewModel()
) {
    val topics by viewModel.topics.collectAsState()
    val querySearch by viewModel.querySearch.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTopic()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PageBg
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item { TopicHeader(totalTopics = topics.size) }

            item {
                SearchingBar(
                    query = querySearch,
                    onQueryChange = { newQuery -> viewModel.onSearchQueryChange(newQuery) }
                )
            }

            item {
                SectionLabel(
                    text = "Tất cả chủ đề",
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 4.dp)
                )
            }

            items(topics) { topic ->
                CompactTopicCard(
                    topic = topic,
                    progress = 0f,
                    onClick = { onTopicClick(topic.id, topic.title) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun TopicHeader(totalTopics: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(PurplePrimary, PurpleDark, PurpleDeep)
                )
            )
            .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 60.dp)
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.07f))
        )
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-10).dp, y = 30.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.07f))
        )

        Column {
            Text(
                text = "Học từ vựng",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Chủ đề từ vựng",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 32.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Chọn chủ đề để bắt đầu học",
                color = Color.White.copy(alpha = 0.70f),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatPill(label = "$totalTopics chủ đề")
            }
        }
    }
}

@Composable
private fun StatPill(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SearchingBar(query: String, onQueryChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .offset(y = (-20).dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Tìm kiếm chủ đề...",
                    color = PurpleMid.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = PurpleMid,
                    modifier = Modifier.size(18.dp)
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PurpleMid,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark
            )
        )
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = PurplePrimary,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        modifier = modifier
    )
}

internal fun levelColor(level: String): Color = when (level.uppercase()) {
    "BEGINNER" -> Color(0xFF27AE60)
    "INTERMEDIATE" -> Color(0xFFE67E22)
    "ADVANCED" -> Color(0xFFE74C3C)
    else -> Color(0xFF7F8C8D)
}

private fun levelLabel(level: String): String = when (level.uppercase()) {
    "BEGINNER" -> "Beginner"
    "INTERMEDIATE" -> "Intermediate"
    "ADVANCED" -> "Advanced"
    else -> level
}

@Composable
fun CompactTopicCard(
    topic: Topic,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val seed = "${topic.id}_${topic.title}_${topic.level}"
    val accent = topicColorFor(seed)
    val fallbackIcon = topicIconFor(seed)
    val lvlColor = levelColor(topic.level)

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            accent.copy(alpha = 0.22f),
            accent.copy(alpha = 0.12f),
            Color.White.copy(alpha = 0.94f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardGradient)
            .border(
                width = 1.dp,
                color = accent.copy(alpha = 0.24f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accent.copy(alpha = 0.28f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!topic.icon.isNullOrBlank()) {
                        Text(text = topic.icon, fontSize = 22.sp)
                    } else {
                        Icon(
                            imageVector = fallbackIcon,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
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
                            fontSize = 17.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(lvlColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = levelLabel(topic.level),
                                color = lvlColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = accent.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = accent,
                trackColor = accent.copy(alpha = 0.24f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${topic._count.Vocabulary} từ",
                color = TextGray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopicScreenPreview() {
    MaterialTheme { TopicScreen() }
}
