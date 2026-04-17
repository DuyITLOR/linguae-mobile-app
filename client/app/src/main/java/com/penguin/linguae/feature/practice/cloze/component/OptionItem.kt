package com.penguin.linguae.feature.practice.cloze.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penguin.linguae.core.ui.theme.Black
import com.penguin.linguae.core.ui.theme.Green
import com.penguin.linguae.core.ui.theme.Red
import com.penguin.linguae.core.ui.theme.SurfaceColor

@Composable
fun OptionItem(
    text: String = "",
    isSelected: Boolean? = false,
    isCorrect: Boolean? = null, // null = not yet answered
    onClick: () -> Unit = {},
) {
    val backgroundColor = when {
        isCorrect == null -> SurfaceColor              // chưa trả lời
        isCorrect -> Green
        !isCorrect && isSelected == true -> Red
        else -> SurfaceColor
    }

    val fontWeight = when {
        isSelected == true || isCorrect == true -> FontWeight.Bold
        else -> FontWeight.Black
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(12.dp)) // 1. shadow trước
            .clip(RoundedCornerShape(12.dp))         // 2. clip theo shape
            .background(SurfaceColor)                // 3. background
            .border(2.dp, backgroundColor, RoundedCornerShape(12.dp)) // 4. border
            .clickable(enabled = isCorrect == null) { onClick() }     // 5. interaction
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(backgroundColor.copy(alpha = 0.2f))
            )
            Text(
                text = text,
                color = Black,
                fontWeight = fontWeight,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Preview
@Composable
fun test(){
    Column() {
        OptionItem(
            text = "111",
            isSelected = true,
            isCorrect = true,
        )
        OptionItem(
            text = "111",
            isSelected = false,
            isCorrect = true,
        )
        OptionItem(
            text = "111",
            isSelected = true,
            isCorrect = false,
        )
        OptionItem(
            text = "111",
            isSelected = false,
            isCorrect = false,
        )
    }
}