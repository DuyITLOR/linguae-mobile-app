package com.penguin.linguae.feature.learning

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurpleDeep
import com.penguin.linguae.core.ui.theme.PurpleLight
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.feature.learning.viewmodel.FavoriteViewModel
import com.penguin.linguae.feature.learning.viewmodel.VocabularyViewModel
import java.util.Locale

private fun difficultyLabel(level: Int): String = when (level) {
    1 -> "Beginner"
    2 -> "Intermediate"
    else -> "Advanced"
}

private fun difficultyColor(level: Int): Color = when (level) {
    1 -> Color(0xFF27AE60)
    2 -> Color(0xFFE67E22)
    else -> Color(0xFFE74C3C)
}

@Composable
fun VocabularyScreen(
    vocabId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: VocabularyViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel()
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    val vocabulary by viewModel.selectedVocabulary.collectAsState()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsState()

    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        tts = engine
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    LaunchedEffect(vocabId) {
        viewModel.fetchVocabularyById(vocabId)
    }

    if (vocabulary == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PurplePrimary)
        }
        return
    }

    val vocab = vocabulary!!
    val isFavorite = vocab.id in favoriteIds
    val diffColor = difficultyColor(vocab.difficulty)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(Brush.verticalGradient(listOf(PurplePrimary, PurpleDark, PurpleDeep)))
                .padding(top = 48.dp, bottom = 36.dp, start = 8.dp, end = 20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Back button row
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = SurfaceColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Word + difficulty + audio
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = vocab.word,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SurfaceColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = vocab.pronunciationText,
                                fontSize = 15.sp,
                                color = SurfaceColor.copy(alpha = 0.8f)
                            )
                            if (!vocab.partOfSpeech.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = vocab.partOfSpeech,
                                        fontSize = 12.sp,
                                        color = SurfaceColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(diffColor.copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = difficultyLabel(vocab.difficulty),
                                fontSize = 12.sp,
                                color = SurfaceColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Audio button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SurfaceColor.copy(alpha = 0.2f))
                            .clickable { tts?.speak(vocab.word, TextToSpeech.QUEUE_FLUSH, null, null) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Play audio",
                            tint = SurfaceColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // ── Body ─────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Meaning
            VocabSection(title = "MEANING") {
                Surface(
                    color = SurfaceColor,
                    shape = RoundedCornerShape(18.dp),
                    tonalElevation = 0.dp,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💡", fontSize = 20.sp)
                        }
                        Text(
                            text = vocab.meaning,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    }
                }
            }

            // Examples
            VocabSection(title = "EXAMPLES") {
                if (vocab.VocabularyExample.isEmpty()) {
                    Surface(
                        color = SurfaceColor,
                        shape = RoundedCornerShape(18.dp),
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No examples yet",
                                color = TextGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        vocab.VocabularyExample.forEach { example ->
                            VocabExampleCard(
                                fullSentence = example.sentence,
                                targetWord = vocab.word,
                                translation = example.translation ?: ""
                            )
                        }
                    }
                }
            }

            // Favorite button
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    if (isFavorite) favoriteViewModel.removeFavorite(vocab.id)
                    else favoriteViewModel.addFavorite(vocab.id)
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFavorite) Color(0xFFE74C3C) else PurplePrimary,
                    contentColor = SurfaceColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFavorite) "Saved to favourites" else "Save to favourites",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun VocabSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray,
            letterSpacing = 1.2.sp
        )
        content()
    }
}

@Composable
private fun VocabExampleCard(fullSentence: String, targetWord: String, translation: String) {
    val annotatedString = buildAnnotatedString {
        val startIndex = fullSentence.indexOf(targetWord, ignoreCase = true)
        if (startIndex >= 0) {
            append(fullSentence.substring(0, startIndex))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = PurplePrimary)) {
                append(fullSentence.substring(startIndex, startIndex + targetWord.length))
            }
            append(fullSentence.substring(startIndex + targetWord.length))
        } else {
            append(fullSentence)
        }
    }

    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(PurplePrimary)
            )
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = annotatedString, fontSize = 15.sp, color = TextDark, lineHeight = 22.sp)
                if (translation.isNotBlank()) {
                    Text(text = translation, fontSize = 13.sp, color = TextGray, lineHeight = 19.sp)
                }
            }
        }
    }
}
