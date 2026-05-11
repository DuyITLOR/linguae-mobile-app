package com.penguin.linguae.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.R
import com.penguin.linguae.core.ui.theme.HomeCardBorder
import com.penguin.linguae.core.ui.theme.SurfaceColor

@Composable
fun StreakCard(dayStreak: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .border(1.dp, HomeCardBorder, RoundedCornerShape(24.dp))
            .padding(14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(R.drawable.fire_streak),
                    contentDescription = "Fire Streaks",
                    tint = Color.Unspecified
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "$dayStreak",
                    color = SurfaceColor,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = "Ngày liên tiếp", fontSize = 14.sp, color = SurfaceColor.copy(alpha = 0.9f))
            }
        }
    }
}