package com.penguin.linguae.feature.practice.cloze.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.data.model.ClozeOption
import com.penguin.linguae.feature.practice.cloze.viewmodel.ClozeViewModel
import kotlin.collections.forEach


@Composable
fun ClozeOptions(
    screenHeight: Dp,
    options: List<ClozeOption>,   // your options from DB
    viewModel: ClozeViewModel,
) {
    val selectedId by viewModel.selectedOptionId.collectAsState()
    val isAnswered by viewModel.isAnswered.collectAsState()
    val isLastQuestion by viewModel.isLastQuestion.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth(0.95f),
        verticalArrangement = Arrangement.spacedBy(screenHeight * 0.015f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        options.forEach { option ->
            OptionItem(
                text = option.answer,
                isSelected = selectedId == option.id,
                isCorrect = if (isAnswered) option.isCorrect else null,
                onClick = { viewModel.onOptionSelected(option.id) }
            )
        }
        if (isAnswered) {
            OutlinedButton(
                onClick = {
                    if (!isLastQuestion) viewModel.onNextQuestionClicked()
                    else viewModel.onResultClicked()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightGreen,
                    contentColor = Black
                ),
                border = BorderStroke(2.dp, Green),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (!isLastQuestion) "Next Question" else "See Result",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                }
            }
        }
//        if (isAnswered && isFinished) {
//            Button(
//                onClick = onReturn,
//                shape = RoundedCornerShape(12.dp),
//                elevation = ButtonDefaults.buttonElevation(
//                    defaultElevation = 4.dp
//                ),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = HomeHeroEnd,
//                    contentColor = SurfaceColor
//                ),
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.return_arrow),
//                    contentDescription = "return",
//                    modifier = Modifier.size(24.dp),
//                )
//            }
//        }
    }
