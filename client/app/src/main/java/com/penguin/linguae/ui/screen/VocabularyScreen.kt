package com.penguin.linguae.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.model.Vocabulary
import com.penguin.linguae.viewmodel.VocabularyViewModel

val GreenHeader = Color(0xFF48C78E)
val PurpleButton = Color(0xFF7C4DFF)
val BackgroundApp = Color(0xFFF4F5FA)
val YellowStar = Color(0xFFFFC107)

@Composable
fun VocabularyScreen(viewModel: VocabularyViewModel = viewModel()) {

    val sampleList = listOf(
        Vocabulary(1, "Apple", "/'æpəl/", "Quả táo", true),
        Vocabulary(2, "Banana", "/bə'nɑ:nə/", "Quả chuối", false),
        Vocabulary(3, "Coffee", "/'kɒfi/", "Cà phê", true),
        Vocabulary(4, "Rice", "/raɪs/", "Cơm / Gạo", false),
        Vocabulary(5, "Noodle", "/'nu:dl/", "Mì", false)
    )

    Scaffold(
        bottomBar = { PracticeButton() },
        containerColor = BackgroundApp
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TopHeader()
            SearchBar()
            VocabularyList(vocabularyList = sampleList)
        }
    }
}

@Composable
fun TopHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenHeader)
            .padding(top = 40.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "🍎 Đồ ăn & Thức uống",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "25 từ vựng • 80% hoàn thành",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 40.dp, top = 4.dp)
        )
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Tìm từ vựng...", color = Color.Gray) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = PurpleButton)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = PurpleButton
        )
    )
}

@Composable
fun VocabularyList(vocabularyList: List<Vocabulary>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(vocabularyList, key = { it.id }) { vocab ->
            VocabularyCard(vocabulary = vocab)
        }
    }
}

@Composable
fun VocabularyCard(vocabulary: Vocabulary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = vocabulary.word,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = vocabulary.pronunciation,
                    fontSize = 14.sp,
                    color = PurpleButton,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = vocabulary.meaning,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Icon(
                imageVector = if (vocabulary.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Favorite",
                tint = if (vocabulary.isFavorite) YellowStar else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun PracticeButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = { /* TODO: Chuyển sang màn hình Practice */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleButton)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Practice")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Luyện tập chủ đề này", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, name = "Giao Diện Mới Đẹp Mắt")
@Composable
fun VocabularyScreenPreview() {
    MaterialTheme {
        VocabularyScreen()
    }
}