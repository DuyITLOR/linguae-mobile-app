package com.penguin.linguae.feature.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.feature.learning.viewmodel.FavoriteViewModel
import androidx.compose.runtime.getValue

// ✅ Data model
data class FavoriteItem(
    val id: String,
    val title: String,
    val subtitle: String
)

@Composable
fun FavoriteScreen(
    favoriteViewModel: FavoriteViewModel = viewModel(),
    onBack: () -> Unit = {},
    onVocabularyClick: (String) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                favoriteViewModel.fetchFavorite()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val favoriteVocab by favoriteViewModel.favorites.collectAsState()

    val favoriteData = favoriteVocab.map { favorite ->
        FavoriteItem(
            id = favorite.vocabularyId,
            title = favorite.Vocabulary.word,
            subtitle = favorite.Vocabulary.meaning
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9E7F2))
    ) {

        // 🔹 Purple Gradient Banner Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF7B5EA7), Color(0xFF5B4FCF))
                    )
                )
                .padding(start = 8.dp, end = 24.dp, top = 16.dp, bottom = 28.dp)
        ) {
            // Decorative circle (top-right)
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-20).dp)
                    .background(Color(0x22FFFFFF), shape = CircleShape)
            )

            Column {
                // Back button row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = "Từ yêu thích",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Subtitle tagline
                Text(
                    text = "Ôn tập những từ bạn đã lưu!",
                    fontSize = 13.sp,
                    color = Color(0xCCFFFFFF)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Word count badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0x33FFFFFF))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${favoriteData.size} TỪ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 List items
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            favoriteData.forEach {
                FavoriteCard(
                    item = it,
                    onClick = { onVocabularyClick(it.id) }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// 🔥 Item UI
@Composable
fun FavoriteCard(
    item: FavoriteItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF3F3F3))
            .padding(16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ⭐ Icon
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 🔹 Text
        Column {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F)
            )

            Text(
                text = item.subtitle,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}
