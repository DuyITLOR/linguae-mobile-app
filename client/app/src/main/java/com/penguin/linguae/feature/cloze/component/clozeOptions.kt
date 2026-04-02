package com.penguin.linguae.feature.cloze.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.penguin.linguae.feature.cloze.Option
import com.penguin.linguae.feature.cloze.viewmodel.ClozeViewModel
import kotlin.collections.forEach


@Composable
fun ClozeOptions(
    screenHeight: Dp,
    options: List<Option>,   // your options from DB
    viewModel: ClozeViewModel
) {
    val selectedId by viewModel.selectedOptionId.collectAsState()
    val isAnswered by viewModel.isAnswered.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth(0.95f),
        verticalArrangement = Arrangement.spacedBy(screenHeight * 0.015f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        options.forEach { option ->
            OptionItem(
                text = option.answer,
                isSelected = selectedId == option.questionID,
                isCorrect = if (isAnswered) option.isCorrect else null,
                onClick = { viewModel.onOptionSelected(option.questionID) }
            )
        }
    }
}