package com.penguin.linguae.feature.learning

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.PageBg
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.SearchBg
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray

private data class FlashcardTopicUi(
    val title: String,
    val level: String,
    val learnedWords: Int,
    val totalWords: Int,
    val progress: Float,
    val accent: Color
)

@Composable
fun FlashcardListScreen(
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val topics = remember {
        listOf(
            FlashcardTopicUi(
                title = "Basic Words",
                level = "Beginner",
                learnedWords = 58,
                totalWords = 82,
                progress = 0.71f,
                accent = Color(0xFF5A67D8)
            ),
            FlashcardTopicUi(
                title = "Travel",
                level = "Intermediate",
                learnedWords = 41,
                totalWords = 70,
                progress = 0.58f,
                accent = Color(0xFF2F80ED)
            ),
            FlashcardTopicUi(
                title = "Food and Drinks",
                level = "Beginner",
                learnedWords = 66,
                totalWords = 90,
                progress = 0.73f,
                accent = Color(0xFF56CC9D)
            )
        )
    }

    val filteredTopics = remember(query, topics) {
        val keyword = query.trim()
        if (keyword.isEmpty()) {
            topics
        } else {
            topics.filter {
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
                    .padding(paddingValues),
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
                        text = "CHU DE PHO BIEN",
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
                        contentMaxWidth = contentMaxWidth
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
            placeholder = { Text("Tim kiem chu de ...") },
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
    contentMaxWidth: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = contentMaxWidth)
            .padding(horizontal = if (compact) 16.dp else 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceColor)
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
                        .background(topic.accent.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
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
                        Text(
                            text = topic.level,
                            color = TextGray,
                            fontSize = 13.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "next",
                        tint = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = topic.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = topic.accent,
                trackColor = topic.accent.copy(alpha = 0.18f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tien do: ${topic.learnedWords}/${topic.totalWords} tu",
                color = TextGray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun previewFlashcardListScreen() {
    FlashcardListScreen(onBack = {})
}
