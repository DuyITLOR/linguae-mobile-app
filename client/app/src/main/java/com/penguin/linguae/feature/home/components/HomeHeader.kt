package com.penguin.linguae.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.R
import com.penguin.linguae.core.ui.theme.HomeHeroEnd
import com.penguin.linguae.core.ui.theme.HomeHeroMid
import com.penguin.linguae.core.ui.theme.HomeHeroStart
import com.penguin.linguae.core.ui.theme.HomeHeroTint
import com.penguin.linguae.core.ui.theme.SurfaceColor

@Composable
fun HomeHeader(
    name: String,
    dayStreak: Int,
    wordLearned: Int,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        HomeHeroStart,
                        HomeHeroMid,
                        HomeHeroEnd,
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(HomeHeroTint)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(text = "Xin chào", color = SurfaceColor, fontSize = 18.sp)
            Icon(
                painter = painterResource(R.drawable.waving_hand),
                tint = SurfaceColor,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = "$name!",
            color = SurfaceColor,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "Tiếp tục nhịp học hôm nay với các bài luyện ngắn, rõ và dễ quay lại.",
            color = SurfaceColor.copy(alpha = 0.88f),
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )

        StreakCard(dayStreak, wordLearned)
        Spacer(modifier = Modifier.size(1.dp))
    }
}