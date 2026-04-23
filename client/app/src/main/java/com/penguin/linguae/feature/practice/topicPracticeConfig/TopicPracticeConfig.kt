package com.penguin.linguae.feature.practice.topicPracticeConfig

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.data.model.TopicPracticeConfig
import com.penguin.linguae.feature.practice.topicPracticeConfig.viewmodel.TopicPracticeConfigViewModel

@Composable
fun TopicPracticeConfigScreen(
    topicId: String,
    navController: NavController,
    viewModel: TopicPracticeConfigViewModel = viewModel(),
    onReturn: () -> Unit = {}
) {
    val practiceConfigs by viewModel.practiceConfigs.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(topicId) {
        viewModel.fetchPracticeConfigs(topicId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // Header
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(Primary, PrimaryLight))
                )
                .padding(horizontal = 16.dp, vertical = 28.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onReturn) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = SurfaceColor
                    )
                }
                Text(
                    "Chọn loại bài tập",
                    color = SurfaceColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error!!, color = Red)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "CÁC DẠNG BÀI TẬP",
                        color = TextSecond,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                // Flatten all question types from all TopicPracticeConfig records for this topic
                val allQuestionTypes = practiceConfigs.flatMap { it.questionType }.distinct()

                items(allQuestionTypes) { type ->
                    TopicPracticeConfigCard(
                        typeName = type,
                        onClick = {
                            when (type.uppercase()) {
                                "CLOZE" -> navController.navigate(Screen.Cloze.createRoute(topicId))
                                "FLASHCARD" -> navController.navigate(Screen.Flashcard.createRoute(topicId))
                                // Add other mappings as they are implemented
                            }
                        }
                    )
                }
                
                if (allQuestionTypes.isEmpty()) {
                    item {
                        Text(
                            "Chưa có bài tập nào cho chủ đề này.",
                            modifier = Modifier.padding(top = 20.dp),
                            color = TextSecond
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopicPracticeConfigCard(typeName: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Primary.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text("📝", fontSize = 20.sp)
        }

        Spacer(Modifier.width(16.dp))

        Text(
            text = typeName,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            "›",
            color = TextSecond,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
