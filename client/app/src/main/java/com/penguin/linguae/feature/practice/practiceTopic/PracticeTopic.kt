package com.penguin.linguae.feature.practice.practiceTopic

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.feature.practice.practiceTopic.viewmodel.PracticeTopicViewModel
import com.penguin.linguae.core.ui.component.ErrorView

// ─── Screen ──────────────────────────────────────────────────────────────────
@Composable
fun PracticeTopicScreen(
    navController: NavController,
    viewModel: PracticeTopicViewModel = viewModel(),
    onReturn: () -> Unit = {},
) {
    val topics by viewModel.topic.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    val filtered = remember(query) {
        topics?.filter { it.title.contains(query, ignoreCase = true) }
    }

    LaunchedEffect(Unit) {
        viewModel.onTopicClicked = { topic ->
            if (topic != null) {
                navController.navigate(Screen.TopicPracticeConfig.createRoute(topic.id))
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(Primary, PrimaryLight))
                )
                .padding(horizontal = 16.dp, vertical = 28.dp)
        ) {
            Column {
                // Back + title row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onReturn) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = SurfaceColor.copy(alpha = 0.9f)
                        )
                    }
                    Text(
                        "Chọn chủ đề luyện tập",
                        color = SurfaceColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Search bar
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceColor.copy(alpha = 0.18f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = SurfaceColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = SurfaceColor, fontSize = 14.sp
                        ),
                        decorationBox = { inner ->
                            if (query.isEmpty()) Text(
                                "Tìm chủ đề...",
                                color = SurfaceColor.copy(0.6f),
                                fontSize = 14.sp
                            )
                            inner()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // ── List ────────────────────────────────────────────────────────
        if (isLoading && (topics == null || topics!!.isEmpty())) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (error != null && (topics == null || topics!!.isEmpty())) {
            ErrorView(
                message = error ?: "Unknown error",
                onRetry = { viewModel.refresh() }
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // "All topics" special card
                item {
                    AllTopicsCard(onClick = { viewModel.onTopicClicked(null) })
                }

                // Section label
                item {
                    Text(
                        "CHỌN CHỦ ĐỀ",
                        color = TextSecond,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }

                if (filtered === null) {
                    items(topics ?: emptyList(), key = { it.id }) { topic ->
                        TopicCard(topic = topic, onClick = { viewModel.onTopicClicked(topic) })
                    }
                } else if (filtered.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Không tìm thấy chủ đề 🥲", color = TextSecond, fontSize = 14.sp)
                        }
                    }
                } else {
                    items(filtered, key = { it.id }) { topic ->
                        TopicCard(topic = topic, onClick = { viewModel.onTopicClicked(topic) })
                    }
                }
            }
        }
    }
}

// ─── All topics card ─────────────────────────────────────────────────────────
@Composable
fun AllTopicsCard(onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(AccentOrange, Color(0xFFFF8C42))))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceColor.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text("🌟", fontSize = 24.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Text("Tất cả chủ đề", color = SurfaceColor, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Text("Luyện tập tổng hợp", color = SurfaceColor.copy(0.8f), fontSize = 12.sp)
        }

        Text("›", color = SurfaceColor.copy(0.8f), fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

// ─── Topic card ──────────────────────────────────────────────────────────────
@Composable
fun TopicCard(topic: Topic, onClick: () -> Unit) {
    val levelColor = when (topic.level.lowercase()) {
        "beginner" -> Green
        "intermediate" -> Yellow
        "advanced" -> Red
        else -> SurfaceColor
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceColor)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Emoji icon
        Box(
            Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(BgColor),
            contentAlignment = Alignment.Center
        ) {
//            Text(topic.emoji, fontSize = 24.sp)
            Icon(
                Icons.Default.Topic,
                contentDescription = null
            )
        }

        Spacer(Modifier.width(14.dp))

        // Name + word count + progress bar
        Column(Modifier.weight(1f)) {
            Text(
                topic.title,
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text("${topic._count.Vocabulary} từ", color = TextSecond, fontSize = 12.sp)
            Spacer(Modifier.height(7.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(levelColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = topic.level,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}