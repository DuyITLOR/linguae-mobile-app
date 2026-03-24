package com.penguin.linguae.feature.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.R
import com.penguin.linguae.core.ui.theme.*

@Composable
fun TodayProgress(progress: Float) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .shadow(4.dp, shape = RoundedCornerShape(20.dp)),
        color = SurfaceColor
    ){
        Box(
            modifier = Modifier.fillMaxSize(0.95f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.book),
                        contentDescription = null,
                        tint = Yellow,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Tiến độ hôm nay",
                        color = TextGray,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                    )
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = Green,
                    trackColor = LightPurple,
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                    strokeCap = StrokeCap.Butt
                )
            }
        }
    }
}
