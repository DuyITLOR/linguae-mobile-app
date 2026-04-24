package com.penguin.linguae.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.R
import com.penguin.linguae.core.ui.theme.Blue
import com.penguin.linguae.core.ui.theme.Emerald
import com.penguin.linguae.core.ui.theme.Orange
import com.penguin.linguae.core.ui.theme.Sapphire
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.core.ui.theme.Yellow
import com.penguin.linguae.feature.home.viewmodel.HomeViewModel

@Composable
fun MenuCard(
    icon: Int,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
                .padding(14.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = color
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(text = title, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text(text = subtitle, fontSize = 12.sp, color = TextGray)
            }
        }
    }
}

data class MenuItem(
    val icon: Int,
    val title: String,
    val subtitle: String,
    val color: Color,
    val onClick: () -> Unit = {}
)

@Composable
fun QuickLearn(
    onVocabularyClick: () -> Unit,
    onFlashcardClick: () -> Unit,
    onPracticeClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onToeicMockTestClick: () -> Unit,
    viewModel: HomeViewModel
) {
    val menuItems = remember {
        listOf(
            MenuItem(
                R.drawable.dictionary,
                "Từ vựng",
                "Học theo chủ đề",
                Orange
            ) { onVocabularyClick() },
            MenuItem(
                R.drawable.flash_card,
                "Flashcard",
                "Ôn tập nhanh",
                Blue
            ) { onFlashcardClick() },
            MenuItem(
                R.drawable.pencil,
                "Luyện tập",
                "Quiz & Game",
                Sapphire
            ) { onPracticeClick() },
            MenuItem(
                R.drawable.favorite_star,
                "Từ yêu thích",
                "Danh sách đã lưu",
                Emerald,
            ) { onFavoriteClick() },
            MenuItem(
                R.drawable.book,
                "TOEIC Test",
                "Mock test ôn luyện",
                Sapphire,
            ) { onToeicMockTestClick() },
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.lightning),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Yellow
            )
            Column {
                Text(
                    text = "Học nhanh",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "Chọn mục muốn học ngay",
                    fontSize = 13.sp,
                    color = TextGray
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            menuItems.chunked(2).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { item ->
                        MenuCard(
                            icon = item.icon,
                            title = item.title,
                            subtitle = item.subtitle,
                            color = item.color,
                            modifier = Modifier.weight(1f),
                            onClick = item.onClick
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}