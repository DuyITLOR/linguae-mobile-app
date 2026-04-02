package com.penguin.linguae.feature.cloze

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.feature.cloze.component.*
import com.penguin.linguae.feature.cloze.viewmodel.*

@Preview
@Composable
fun ClozeScreen(
    viewModel: ClozeViewModel = ClozeViewModel()
){
    val questionsWithOptions by viewModel.questionsWithOptions.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()

    val current = questionsWithOptions[currentIndex]

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppBackground
    ){
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(screenHeight * 0.05f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Header(current.question.questionID)
            }
            item {
                QuestionHolder(current.question)
            }
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
                    viewModel
                )
            }
        }
    }
}




