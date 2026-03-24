package com.penguin.linguae.feature.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.R
import com.penguin.linguae.core.ui.theme.*


@Composable
fun Greetings(name: String) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "Xin chào", color = SurfaceColor, fontSize = 24.sp)
            Icon(
                painter = painterResource(R.drawable.waving_hand),
                tint = SurfaceColor,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "$name!",
            color = SurfaceColor,
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}