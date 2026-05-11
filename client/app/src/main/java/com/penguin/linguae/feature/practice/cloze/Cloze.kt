package com.penguin.linguae.feature.practice.cloze

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.feature.practice.cloze.component.ClozeOptions
import com.penguin.linguae.feature.practice.cloze.component.Header
import com.penguin.linguae.feature.practice.cloze.component.QuestionHolder
import com.penguin.linguae.feature.practice.cloze.viewmodel.ClozeViewModel
import com.penguin.linguae.core.ui.component.ErrorView

@Composable
fun ClozeScreen(
    navController: NavController,
    topicId: String = "",
    viewModel: ClozeViewModel = ClozeViewModel(topicId),
    onReturn: () -> Unit = {}
){
    val questionsWithOptions by viewModel.shuffleQuestionsWithOptions.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { route ->
            navController.navigate(route) {
                popUpTo(Screen.Cloze.route) {
                    inclusive = true
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppBackground
    ){
        when {
            // Đang load
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Có lỗi
            error != null -> {
                ErrorView(
                    message = error ?: "Unknown error",
                    onRetry = { viewModel.fetchQuestionsWithOptionsByTopic(topicId) }
                )
            }

            // List rỗng sau khi load xong
            questionsWithOptions.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Không có câu hỏi nào")
                }
            }

            // Có data
            else -> {
                val current = questionsWithOptions[currentIndex]

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.05f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Header(currentIndex, questionsWithOptions.size, onClick = onReturn) }
                    item { QuestionHolder(current.question) }
                    item {
                        Text(
                            text = "Chọn từ phù hợp để hoàn thành câu:",
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth(0.95f)
                        )
                    }
                    item {
                        ClozeOptions(
                            screenHeight,
                            current.options,
                            viewModel,
                        )
                    }
                }
            }
        }
    }
}



