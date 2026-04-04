package com.penguin.linguae.feature.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.media.MediaPlayer
import com.penguin.linguae.feature.learning.viewmodel.FavoriteViewModel
import com.penguin.linguae.feature.learning.viewmodel.VocabularyViewModel

val GradientTop = Color(0xFF6E68D1)
val GradientBottom = Color(0xFF534192)
val CardBackground = Color(0xFFF6F5FB)
val PrimaryPurple = Color(0xFF7B61FF)
val TextGrayTitle = Color(0xFF8A8A99)
val TextDarkMain = Color(0xFF2D2D3A)

@Composable
fun VocabularyScreen(
    vocabId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: VocabularyViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel()
) {
    val mediaPlayer = remember { MediaPlayer() }
    val vocabulary by viewModel.selectedVocabulary.collectAsState()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsState()

    LaunchedEffect(vocabId) {
        viewModel.fetchVocabularyById(vocabId)
    }

    if (vocabulary == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryPurple)
        }
        return
    }

    val vocab = vocabulary!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        HeaderSection(
            word = vocab.word,
            pronunciation = vocab.pronunciationText,
            partOfSpeech = vocab.partOfSpeech ?: "",
            onNavigateBack = onNavigateBack,
            onPlayAudio = {

                val audioUrl = vocab.pronunciationAudio

                if (!audioUrl.isNullOrEmpty()) {
                    try {
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(audioUrl)
                        mediaPlayer.prepareAsync()
                        mediaPlayer.setOnPreparedListener { player ->
                            player.start()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            SectionTitle("NGHĨA")
            MeaningCard(
                meaning = vocab.meaning
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("VÍ DỤ")

            if (vocab.VocabularyExample.isEmpty()) {
                Text(
                    text = "Chưa có ví dụ",
                    color = TextGrayTitle
                )
            } else {
                vocab.VocabularyExample.forEach { example ->
                    ExampleCard(
                        fullSentence = example.sentence,
                        targetWord = vocab.word,
                        translation = example.translation ?: ""
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            FavoriteButton(
                isFavorite = vocab.id in favoriteIds,
                onToggle = {
                    if (vocab.id in favoriteIds) {
                        favoriteViewModel.removeFavorite(vocab.id)
                    } else {
                        favoriteViewModel.addFavorite(vocab.id)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            FlashcardButton()
        }
    }
}

@Composable
fun HeaderSection(
    word: String,
    pronunciation: String,
    partOfSpeech: String,
    onNavigateBack: () -> Unit,
    onPlayAudio: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientTop, GradientBottom)
                )
            )
            .padding(top = 48.dp, bottom = 40.dp, start = 20.dp, end = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Back",
                    color = Color.White,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
                Text(
                    text = "",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = word,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$pronunciation · $partOfSpeech",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onPlayAudio() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Phát âm thanh",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextGrayTitle,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun MeaningCard(meaning: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = meaning,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextDarkMain
        )
    }
}

@Composable
fun ExampleCard(fullSentence: String, targetWord: String, translation: String) {
    val annotatedString = buildAnnotatedString {
        val startIndex = fullSentence.indexOf(targetWord, ignoreCase = true)
        if (startIndex >= 0) {
            append(fullSentence.substring(0, startIndex))
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = TextDarkMain)) {
                append(fullSentence.substring(startIndex, startIndex + targetWord.length))
            }
            append(fullSentence.substring(startIndex + targetWord.length))
        } else {
            append(fullSentence)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(PrimaryPurple)
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = annotatedString,
                fontSize = 16.sp,
                color = TextDarkMain
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = translation,
                fontSize = 15.sp,
                color = TextGrayTitle
            )
        }
    }
}

@Composable
fun FavoriteButton(isFavorite: Boolean, onToggle: () -> Unit) {
    val borderColor = if (isFavorite) PrimaryPurple else Color(0xFFE5E5EA)
    val bgColor = if (isFavorite) Color(0xFFF0EDFF) else Color(0xFFFAFAFC)
    val textColor = if (isFavorite) PrimaryPurple else TextDarkMain
    val label = if (isFavorite) "Đã lưu vào yêu thích" else "Lưu vào yêu thích"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .background(bgColor, shape = RoundedCornerShape(16.dp))
            .clickable { onToggle() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = textColor
        )
    }
}

@Composable
fun FlashcardButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF8871FF), Color(0xFF6A50FF))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Học bằng Flashcard",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}