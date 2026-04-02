package com.penguin.linguae.feature.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ✅ Data model
data class FavoriteItem(
    val title: String,
    val subtitle: String
)

// ✅ Fake data
val favoriteData = listOf(
    FavoriteItem("Apple", "Quả táo"),
    FavoriteItem("Coffee", "Cà phê"),
    FavoriteItem("Beautiful", "Xinh đẹp"),
    FavoriteItem("Friendship", "Tình bạn")
)

@Composable
fun FavoriteScreen(
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9E7F2))
            .padding(16.dp)
    ) {

        // 🔹 Header
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "⭐ Từ yêu thích",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 List items
        favoriteData.forEach {
            FavoriteCard(item = it)

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// 🔥 Item UI
@Composable
fun FavoriteCard(item: FavoriteItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF3F3F3))
            .padding(16.dp)
            .clickable { },
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

// 👀 Preview
@Preview(showBackground = true)
@Composable
fun FavoritePreview() {
    FavoriteScreen()
}