package com.penguin.linguae.feature.cloze.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.penguin.linguae.core.ui.theme.Black
import com.penguin.linguae.core.ui.theme.Green
import com.penguin.linguae.core.ui.theme.Red
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.core.ui.theme.TextGray

@Composable
fun OptionItem(
    text: String = "",
    isSelected: Boolean? = false,
    isCorrect: Boolean? = null,       // null = not yet answered
    onClick: () -> Unit = {},
) {
    val backgroundColor = when (isCorrect) {
        true if isSelected == true -> Green
        false if isSelected == false -> Red
        else -> SurfaceColor
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, TextGray, RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = isCorrect == null) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, color = if (isSelected == true) SurfaceColor else Black)
    }
}