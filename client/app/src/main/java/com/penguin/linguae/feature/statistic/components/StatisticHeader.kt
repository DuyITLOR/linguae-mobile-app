package com.penguin.linguae.feature.statistic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurpleDeep
import com.penguin.linguae.core.ui.theme.PurplePrimary

@Composable
fun StatisticHeader(currentStreak: Int, totalVocabLearned: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(PurplePrimary, PurpleDark, PurpleDeep)
                )
            )
            .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 32.dp)
    ) {
        // Decorative blobs
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.07f))
        )
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-10).dp, y = 30.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.07f))
        )

        Column {
            Text(
                text = "Học tập",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tiến độ của bạn",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 32.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Theo dõi chuỗi ngày học và từ vựng đã tích lũy",
                color = Color.White.copy(alpha = 0.70f),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                StatPill(label = "🔥 $currentStreak ngày")
                StatPill(label = "📚 $totalVocabLearned từ")
            }
        }
    }
}

@Composable
private fun StatPill(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
