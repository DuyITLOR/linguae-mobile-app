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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Search
import com.penguin.linguae.core.ui.theme.PageBg
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurpleDeep
import com.penguin.linguae.core.ui.theme.PurpleMid
import com.penguin.linguae.core.ui.theme.TextDark

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

    var searchQuery by remember { mutableStateOf("") }

    val favoriteData = favoriteVocab
        .filter {
            it.Vocabulary.word.contains(searchQuery, ignoreCase = true) ||
            it.Vocabulary.meaning.contains(searchQuery, ignoreCase = true)
        }
        .map { favorite ->
            FavoriteItem(
                id = favorite.vocabularyId,
                title = favorite.Vocabulary.word,
                subtitle = favorite.Vocabulary.meaning
            )
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PageBg
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                // 🔹 Purple Gradient Banner Header (giống TopicScreen)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(PurplePrimary, PurpleDark, PurpleDeep)
                            )
                        )
                        .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 60.dp)
            ) {
                // Decorative circle lớn (top-right)
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 30.dp, y = (-30).dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.07f))
                )
                // Decorative circle nhỏ
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-10).dp, y = 30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.07f))
                )

                Column {
                    // Nút back
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subtitle nhỏ phía trên
                    Text(
                        text = "Từ vựng của bạn",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(Modifier.height(6.dp))

                    // Title chính
                    Text(
                        text = "Từ yêu thích",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 32.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    // Tagline
                    Text(
                        text = "Ôn tập những từ bạn đã lưu!",
                        color = Color.White.copy(alpha = 0.70f),
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    // Pill đếm số từ
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${favoriteData.size} từ yêu thích",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            FavoriteSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 🔹 List items
            items(favoriteData) { item ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    FavoriteCard(
                        item = item,
                        onClick = { onVocabularyClick(item.id) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun FavoriteSearchBar(query: String, onQueryChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .offset(y = (-20).dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Tìm kiếm từ vựng...",
                    color = PurpleMid.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = PurpleMid,
                    modifier = Modifier.size(18.dp)
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PurpleMid,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark
            )
        )
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
