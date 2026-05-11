package com.penguin.linguae.feature.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.PageBg
import com.penguin.linguae.core.ui.theme.PurpleBlueTheme
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurpleDeep
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.SearchBg
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.feature.learning.viewmodel.FavoriteViewModel
import com.penguin.linguae.feature.learning.viewmodel.SearchVocabularyViewModel

@Composable
fun SearchScreen(
    onVocabularyClick: (String) -> Unit,
    viewModel: SearchVocabularyViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsState()

    LaunchedEffect(Unit) {
        favoriteViewModel.fetchFavorite()
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(containerColor = PageBg, contentWindowInsets = WindowInsets(0)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchHeader()

            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-20).dp)
            ) {
                TextField(
                    value = query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    placeholder = {
                        Text(
                            text = "Nhập từ cần tìm...",
                            color = PurpleBlueTheme.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .focusRequester(focusRequester),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SearchBg,
                        unfocusedContainerColor = SearchBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PurplePrimary
                    )
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PurplePrimary)
                    }
                }

                query.isBlank() -> {
                    EmptySearchHint()
                }

                results.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Không tìm thấy kết quả",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextGray
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "\"$query\"",
                                fontSize = 14.sp,
                                color = TextGray.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                else -> {
                    Text(
                        text = "${results.size} kết quả",
                        fontSize = 12.sp,
                        color = TextGray,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(results, key = { it.id }) { vocab ->
                            val isFavorite = vocab.id in favoriteIds
                            VocabularyCard(
                                vocabulary = vocab,
                                isFavorite = isFavorite,
                                onClick = { onVocabularyClick(vocab.id) },
                                onFavoriteClick = {
                                    if (isFavorite) favoriteViewModel.removeFavorite(vocab.id)
                                    else favoriteViewModel.addFavorite(vocab.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(PurplePrimary, PurpleDark, PurpleDeep))
            )
            .padding(start = 24.dp, end = 24.dp, top = 30.dp, bottom = 60.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 24.dp, y = (-24).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.07f))
        )

        Column {
            Text(
                text = "Từ điển",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tìm kiếm từ vựng",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 32.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tra cứu toàn bộ từ vựng trong hệ thống",
                color = Color.White.copy(alpha = 0.70f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EmptySearchHint() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = PurplePrimary.copy(alpha = 0.3f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Nhập từ để tìm kiếm",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextGray
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tìm theo từ tiếng Anh hoặc nghĩa tiếng Việt",
                fontSize = 13.sp,
                color = TextGray.copy(alpha = 0.7f)
            )
        }
    }
}
