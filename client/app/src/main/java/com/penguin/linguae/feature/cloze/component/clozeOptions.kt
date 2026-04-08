package com.penguin.linguae.feature.cloze.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.penguin.linguae.R
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.data.model.ClozeOption
import com.penguin.linguae.feature.cloze.viewmodel.ClozeViewModel
import kotlin.collections.forEach


@Composable
fun ClozeOptions(
    screenHeight: Dp,
    options: List<ClozeOption>,   // your options from DB
    viewModel: ClozeViewModel,
    onReturn: () -> Unit = {}
) {
    val selectedId by viewModel.selectedOptionId.collectAsState()
    val isAnswered by viewModel.isAnswered.collectAsState()
    val isFinished by viewModel.isFinished.collectAsStateWithLifecycle()

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
        if (!isFinished && isAnswered) {
            Button(
                onClick = { viewModel.onNextQuestionClicked() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    contentColor = SurfaceColor
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Next Question",
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

        if (isAnswered && isFinished) {
            Button(
                onClick = onReturn,
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HomeHeroEnd,
                    contentColor = SurfaceColor
                ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.return_arrow),
                    contentDescription = "return",
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}