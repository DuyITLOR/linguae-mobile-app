package com.penguin.linguae.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.model.Topic
import com.penguin.linguae.ui.theme.AppBackground
import com.penguin.linguae.ui.theme.IconBackground
import com.penguin.linguae.ui.theme.LightPurple
import com.penguin.linguae.ui.theme.PrimaryPurple
import com.penguin.linguae.ui.theme.TextDark
import com.penguin.linguae.ui.theme.TextGray

@Composable
fun TopicScreen(
    onTopicClick: (Int) -> Unit = {}
) {
    val fakeTopics = listOf(
        Topic(1,  "Đồ ăn & Thức uống", 25, 80),
        Topic(2,  "Nhà cửa & Đồ vật", 30, 45),
        Topic(3, "Công việc & Nghề nghiệp", 28, 20),
        Topic(4, "Phương tiện & Du lịch", 22, null)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "Chủ đề từ vựng",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Chọn chủ đề để bắt đầu học",
                    fontSize = 15.sp,
                    color = TextGray
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(fakeTopics) { topic ->
                    TopicCard(topic = topic, onClick = { onTopicClick(topic.id) })
                }
            }
        }
    }
}

@Composable
fun TopicCard(topic: Topic, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Để 0.dp cho phẳng giống thiết kế
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = topic.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${topic.wordCount} từ vựng",
                            fontSize = 13.sp,
                            color = TextGray
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LightPurple)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (topic.progressPercent != null) "${topic.progressPercent}%" else "Mới",
                            color = PrimaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (topic.progressPercent != null) {
                    LinearProgressIndicator(
                        progress = topic.progressPercent / 100f,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                        color = PrimaryPurple,
                        trackColor = LightPurple
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(IconBackground)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopicScreenPreview() {
    MaterialTheme {
        TopicScreen()
    }
}