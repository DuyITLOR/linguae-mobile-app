package com.penguin.linguae.feature.home.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.R
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.feature.home.viewmodel.HomeViewModel

@Composable
fun MenuCard(
    icon: Int,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit = {}) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = color
            )
            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, fontSize = 12.sp)

        }
    }
}

data class MenuItem (
    val icon: Int,
    val title: String,
    val subtitle: String,
    val color: Color,
    val onClick: () -> Unit = {}
)

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun QuickLearn(screenHeight: Dp, viewModel: HomeViewModel) {
    val menuItems = remember {
        listOf(
            MenuItem(
                R.drawable.dictionary,
                "Từ vựng",
                "12 chủ đề",
                Orange
            ) { viewModel.onWordCardClick() },
            MenuItem(
                R.drawable.flash_card,
                "Flashcard",
                "Ôn tập nhanh",
                Blue)
            { viewModel.onPracticeCardClick() },
            MenuItem(
                R.drawable.pencil,
                "Luyện tập",
                "Quiz & Game",
                Sapphire
            ){ viewModel.onWordCardClick() },
            MenuItem(
                R.drawable.statistic,
                "Thống kê",
                "Xem tiến độ",
                Emerald
            ){ viewModel.onWordCardClick() },
        )
    }


    BoxWithConstraints (
        modifier = Modifier.fillMaxSize()
    ) {
        val columns = (maxWidth / 192.dp).toInt()
        val rows = menuItems.chunked(columns)

        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(screenHeight * 0.01f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            rows.forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    rowItems.forEachIndexed { index, item ->
                        MenuCard(
                            icon = item.icon,
                            title = item.title,
                            subtitle = item.subtitle,
                            color = item.color,
                            modifier = Modifier.weight(20f),
                            onClick = item.onClick
                        )
                        if (index < rowItems.lastIndex)
                            Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}