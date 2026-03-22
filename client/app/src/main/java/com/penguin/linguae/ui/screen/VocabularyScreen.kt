package com.penguin.linguae.ui.screen

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.model.Column
import com.penguin.linguae.ui.theme.AppBackground
import com.penguin.linguae.ui.theme.PurpleBlueTheme
import com.penguin.linguae.ui.theme.SearchBg
import com.penguin.linguae.ui.theme.StarActive
import com.penguin.linguae.ui.theme.SurfaceColor
import com.penguin.linguae.ui.theme.TextDark
import com.penguin.linguae.ui.theme.TextGray
import com.penguin.linguae.viewmodel.VocabularyViewModel

@Composable
fun VocabularyScreen(topicId: Int, onNavigateBack: () -> Unit, viewModel: VocabularyViewModel = viewModel()) {
    val vocabularies = viewModel.vocabulary.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchVocabulary()
    }

    Scaffold(
        bottomBar = { PracticeButton() },
        containerColor = AppBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TopHeader(onNavigateBack = onNavigateBack)
            SearchBar()
            VocabularyList(vocabularyList = vocabularies.value)
        }
    }
}

@Composable
fun TopHeader(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 12.dp, start = 8.dp, end = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextDark
                )
            }
            Text(
                text = "Đồ ăn & Thức uống",
                color = PurpleBlueTheme,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            text = "25 từ vựng • 80% hoàn thành",
            color = TextGray,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 48.dp, top = 2.dp)
        )
    }
}

@Composable
fun SearchBar() {
    TextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Tìm từ vựng...", color = PurpleBlueTheme.copy(alpha = 0.6f)) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = PurpleBlueTheme)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SearchBg,
            unfocusedContainerColor = SearchBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = PurpleBlueTheme
        )
    )
}

@Composable
fun VocabularyList(vocabularyList: List<Column>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(vocabularyList, key = { it.id }) { vocab ->
            VocabularyCard(vocabulary = vocab)
        }
    }
}

@Composable
fun VocabularyCard(vocabulary: Column) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = vocabulary.word,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = vocabulary.pronunciation,
                    fontSize = 14.sp,
                    color = PurpleBlueTheme,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = vocabulary.meaning,
                    fontSize = 15.sp,
                    color = TextDark.copy(alpha = 0.8f)
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = if (vocabulary.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Favorite",
                    tint = if (vocabulary.isFavorite) StarActive else TextGray.copy(alpha = 0.3f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun PracticeButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleBlueTheme)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Practice", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Luyện tập chủ đề này", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}