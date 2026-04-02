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
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.feature.cloze.component.*
import com.penguin.linguae.feature.cloze.viewmodel.ClozeViewModel

@Preview
@Composable
fun ClozeScreen(
    viewModel: ClozeViewModel = ClozeViewModel()
){
    val options = remember {
        listOf(
            Option(questionID = 1, answer = "Paris", isCorrect = true) { viewModel.onOptionSelected(0) },
            Option(questionID = 1, answer = "London", isCorrect = false) { viewModel.onOptionSelected(1) },
            Option(questionID = 1, answer = "Berlin", isCorrect = false) { viewModel.onOptionSelected(2) },
            Option(questionID = 1, answer = "Madrid", isCorrect = false) { viewModel.onOptionSelected(3) },
        )
    }
    val question = remember {
        Question(
            questionID = 1,
            question = "ABC is in ___ of France!"
        )
    }
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
                Header(question.questionID)
            }
            item {
                QuestionHolder(question)
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
                    options,
                    viewModel
                )
            }
        }
    }
}




data class Question(
    val questionID: Int = -1,
    val question: String = "",
)

data class Option(
    val questionID: Int = -1,
    val answer: String = "",
    val isCorrect: Boolean? = null,
    val onClick: () -> Unit = {}
)
