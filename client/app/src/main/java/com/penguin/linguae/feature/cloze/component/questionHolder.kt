package com.penguin.linguae.feature.cloze.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.feature.cloze.Question

@Composable
fun QuestionHolder(sentence: Question){
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 36.dp),
            text = sentence.question,
            fontSize = 28.sp,
        )
    }
}