package com.penguin.linguae.feature.practice.cloze.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.R
import com.penguin.linguae.core.ui.theme.PurpleBlueTheme
import com.penguin.linguae.core.ui.theme.SurfaceColor

@Composable
fun Header(
    questionNo: Int,
    questionCount: Int,
    onClick: () -> Unit = {}
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(SurfaceColor)
        .shadow(1.dp)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PurpleBlueTheme)
                .alpha(0.8f)
                .padding(vertical = 12.dp),
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                IconButton(onClick = onClick) {
                    Icon(
                        painter = painterResource(R.drawable.return_arrow),
                        modifier = Modifier.size(36.dp),
                        contentDescription = null,
                        tint = SurfaceColor,
                    )
                }

                Column() {
                    Text(
                        text = "Điền vào chỗ trống",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = SurfaceColor,
                    )
                    Text(
                        text = "Câu ${questionNo + 1} / $questionCount",
                        color = SurfaceColor,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}
