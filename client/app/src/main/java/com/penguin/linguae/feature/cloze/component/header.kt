package com.penguin.linguae.feature.cloze.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.R
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.core.ui.theme.TextGray

@Composable
fun Header(questionNo: Int){
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(SurfaceColor)
        .shadow(1.dp)
        .padding(vertical = 12.dp),
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.return_arrow),
                modifier = Modifier.size(36.dp),
                contentDescription = null,
            )
            Column () {
                Text(
                    text = "Điền vào chỗ trống",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
                Text(
                    text = "Câu $questionNo / 10",
                    color = TextGray,
                    fontSize = 16.sp,
                )
            }
        }
    }
}
