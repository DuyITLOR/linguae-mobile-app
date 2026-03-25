package com.penguin.linguae.feature.learning

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
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
import com.penguin.linguae.core.ui.theme.BadgeBlue
import com.penguin.linguae.core.ui.theme.BadgeBlueBg
import com.penguin.linguae.core.ui.theme.PageBg
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurpleDeep
import com.penguin.linguae.core.ui.theme.PurpleHint
import com.penguin.linguae.core.ui.theme.PurpleLight
import com.penguin.linguae.core.ui.theme.PurpleMid
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.feature.learning.viewmodel.TopicViewModel

@Composable
fun TopicScreen(
    onTopicClick: (String) -> Unit = {},
    viewModel: TopicViewModel = viewModel()
) {
    val topics by viewModel.topics.collectAsState()

    LaunchedEffect(Unit) {
        Log.i("API", "fetchTopic called")
        viewModel.fetchTopic()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PageBg
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item { TopicHeader(totalTopics = topics.size) }

            item { SearchingBar() }

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
                    onClick = { onTopicClick(topic.id) },
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
private fun SearchingBar() {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .offset(y = (-20).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Search,
                contentDescription = null,
                tint = PurpleMid,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Tìm kiếm chủ đề...",
                color = PurpleMid,
                fontSize = 14.sp
            )
        }
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

@Composable
private fun FeaturedBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.20f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CompactTopicCard(
    topic: Topic,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 700),
        label = "progress"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Emoji icon box
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PurpleLight),
            contentAlignment = Alignment.Center
        ) {
            Text(text = topicEmoji(topic.title), fontSize = 24.sp)
        }

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = topic.title,
                    color = TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                LevelBadge(level = topic.level)
            }
            if (!topic.description.isNullOrEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = topic.description!!,
                    color = PurpleHint,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(PurpleLight)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(PurplePrimary)
                    )
                }
                Text(
                    text = "${topic._count.Vocabulary} từ",
                    color = PurpleMid,
                    fontSize = 11.sp
                )
            }
        }

        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color(0xFFC5BCEA),
            modifier = Modifier.size(16.dp)
        )
    }
}

// ─── Level badge ─────────────────────────────────────────────────────────────
@Composable
private fun LevelBadge(level: String) {
    val (bg, fg) = when (level.lowercase()) {
        "nâng cao", "advanced", "c1", "c2" -> BadgeBlueBg to BadgeBlue
        "trung cấp", "intermediate", "b1", "b2" -> PurpleLight to PurplePrimary
        else -> PurpleLight to PurplePrimary
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(text = level, color = fg, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
    }
}

private fun topicEmoji(title: String): String {
    val t = title.lowercase()
    return when {
        t.contains("ăn") || t.contains("thực") || t.contains("nấu") -> "🍽️"
        t.contains("du lịch") || t.contains("travel")              -> "✈️"
        t.contains("công") || t.contains("việc") || t.contains("work") -> "💼"
        t.contains("thể thao") || t.contains("sport")              -> "🏃"
        t.contains("sức khoẻ") || t.contains("y tế")               -> "🏥"
        t.contains("gia đình") || t.contains("family")             -> "👨‍👩‍👧"
        t.contains("thiên nhiên") || t.contains("nature")          -> "🌿"
        t.contains("công nghệ") || t.contains("tech")              -> "💻"
        t.contains("thời trang") || t.contains("fashion")          -> "👗"
        t.contains("âm nhạc") || t.contains("music")               -> "🎵"
        else                                                        -> "📚"
    }
}

@Preview(showBackground = true)
@Composable
fun TopicScreenPreview() {
    MaterialTheme { TopicScreen() }
}