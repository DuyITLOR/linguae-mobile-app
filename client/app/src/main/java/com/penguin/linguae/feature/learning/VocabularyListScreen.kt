package com.penguin.linguae.feature.learning

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.PurpleBlueTheme
import com.penguin.linguae.core.ui.theme.SearchBg
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.data.model.Vocabulary
import com.penguin.linguae.feature.learning.viewmodel.FavoriteViewModel
import com.penguin.linguae.feature.learning.viewmodel.FlashcardViewModel
import com.penguin.linguae.feature.learning.viewmodel.TopicViewModel
import com.penguin.linguae.feature.learning.viewmodel.VocabularyViewModel

val StarActive = Color(0xFFFFC107)

@Composable
fun VocabularyListScreen(
    topicId: String,
    title: String,
    navController: NavHostController,
    onNavigateBack: () -> Unit,
    viewModel: VocabularyViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(),
    flashcardViewModel: FlashcardViewModel = viewModel(),
) {
    val vocabularies by viewModel.vocabulary.collectAsState()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsState()
    val querySearch by viewModel.querySearch.collectAsState()
    val flashcardTopics by flashcardViewModel.topics.collectAsState()
    val currentTopic = flashcardTopics.find { it.id == topicId }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        if (navBackStackEntry?.destination?.route == Screen.VocabularyList.route) {
            flashcardViewModel.refreshTopics()
        }
    }

    LaunchedEffect(topicId) {
        viewModel.fetchVocabularyByTopic(topicId, "")
        favoriteViewModel.fetchFavorite()
    }

    Scaffold(
        containerColor = AppBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TopHeader(onNavigateBack = onNavigateBack, title = title)
            currentTopic?.let {
                TopicProgressBar(learnedWords = it.learnedWords, totalWords = it.totalWords)
            }
            SearchBar(
                query = querySearch,
                onQueryChange = { newQuery ->
                    viewModel.onSearchQueryChange(newQuery, topicId)
                }
            )
            VocabularyList(
                vocabularyList = vocabularies,
                favoriteIds = favoriteIds,
                masteredIds = currentTopic?.masteredVocabIds?.toSet() ?: emptySet(),
                onItemClick = { vocabId ->
                    navController.navigate(Screen.Vocabulary.createRoute(vocabId))
                },
                onFavoriteClick = { vocab, isCurrentlyFavorite ->
                    if (isCurrentlyFavorite) {
                        favoriteViewModel.removeFavorite(vocab.id)
                    } else {
                        favoriteViewModel.addFavorite(vocab.id)
                    }
                }
            )
        }
    }
}

@Composable
fun TopHeader(onNavigateBack: () -> Unit, title: String) {
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
                text = title,
                color = PurpleBlueTheme,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun TopicProgressBar(learnedWords: Int, totalWords: Int) {
    val progress = if (totalWords > 0) learnedWords.toFloat() / totalWords else 0f
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Đã học",
                fontSize = 13.sp,
                color = TextGray
            )
            Text(
                text = "$learnedWords / $totalWords từ",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PurpleBlueTheme
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = PurpleBlueTheme,
            trackColor = PurpleBlueTheme.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
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
fun VocabularyList(
    vocabularyList: List<Vocabulary>,
    favoriteIds: Set<String>,
    masteredIds: Set<String> = emptySet(),
    onItemClick: (String) -> Unit,
    onFavoriteClick: (Vocabulary, Boolean) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(vocabularyList, key = { it.id }) { vocab ->
            val isFavorite = favoriteIds.contains(vocab.id)
            val isMastered = masteredIds.contains(vocab.id)

            VocabularyCard(
                vocabulary = vocab,
                isFavorite = isFavorite,
                isMastered = isMastered,
                onClick = { onItemClick(vocab.id) },
                onFavoriteClick = { onFavoriteClick(vocab, isFavorite) }
            )
        }
    }
}

@Composable
fun VocabularyCard(
    vocabulary: Vocabulary,
    isFavorite: Boolean,
    isMastered: Boolean = false,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = vocabulary.word,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    if (isMastered) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF27AE60).copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Đã học",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF27AE60),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = vocabulary.pronunciationText,
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

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) StarActive else TextGray.copy(alpha = 0.3f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

