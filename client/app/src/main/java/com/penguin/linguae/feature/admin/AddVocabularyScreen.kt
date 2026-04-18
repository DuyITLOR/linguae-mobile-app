package com.penguin.linguae.feature.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.BorderGray
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurpleDeep
import com.penguin.linguae.core.ui.theme.PurpleLight
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.feature.admin.viewmodel.AddVocabularyViewModel

private val vocabPartsOfSpeech = listOf(
    "NOUN", "VERB", "ADJECTIVE", "ADVERB",
    "PRONOUN", "PREPOSITION", "CONJUNCTION", "INTERJECTION"
)

private val difficultyLabels = mapOf(1 to "Beginner", 2 to "Intermediate", 3 to "Advanced")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVocabularyScreen(
    viewModel: AddVocabularyViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.success) {
        if (viewModel.success) onBack()
    }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = AppBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Gradient header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(PurplePrimary, PurpleDark, PurpleDeep)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = SurfaceColor
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Add Vocabulary",
                                color = SurfaceColor,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Add a new word to the database",
                                color = SurfaceColor.copy(alpha = 0.75f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Basic Info section
                VocabFormSection(title = "Basic Info") {
                    VocabFormField(label = "Topic *") {
                        VocabTopicDropdown(
                            topics = viewModel.topics.map { it.title to it.id },
                            selectedTitle = viewModel.selectedTopic?.title ?: "",
                            onSelect = { id ->
                                viewModel.selectedTopic = viewModel.topics.find { it.id == id }
                            }
                        )
                    }
                    VocabFormField(label = "Word *") {
                        OutlinedTextField(
                            value = viewModel.word,
                            onValueChange = { viewModel.word = it },
                            placeholder = { Text("e.g. Ambiguous", color = TextGray) },
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            colors = vocabFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    VocabFormField(label = "Meaning *") {
                        OutlinedTextField(
                            value = viewModel.meaning,
                            onValueChange = { viewModel.meaning = it },
                            placeholder = { Text("e.g. Having multiple meanings", color = TextGray) },
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            colors = vocabFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    VocabFormField(label = "Pronunciation (IPA)") {
                        OutlinedTextField(
                            value = viewModel.pronunciationText,
                            onValueChange = { viewModel.pronunciationText = it },
                            placeholder = { Text("e.g. /æmˈbɪɡ.ju.əs/", color = TextGray) },
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            colors = vocabFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Details section
                VocabFormSection(title = "Details") {
                    VocabFormField(label = "Part of Speech") {
                        VocabPartOfSpeechDropdown(
                            selected = viewModel.partOfSpeech,
                            onSelect = { viewModel.partOfSpeech = it }
                        )
                    }
                    VocabFormField(label = "Difficulty") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(1, 2, 3).forEach { level ->
                                FilterChip(
                                    selected = viewModel.difficulty == level,
                                    onClick = { viewModel.difficulty = level },
                                    label = {
                                        Text(
                                            text = difficultyLabels[level] ?: level.toString(),
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PurplePrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = viewModel.difficulty == level,
                                        borderColor = BorderGray,
                                        selectedBorderColor = PurplePrimary
                                    )
                                )
                            }
                        }
                    }
                }

                // Examples section
                VocabFormSection(title = "Examples") {
                    viewModel.examples.forEachIndexed { index, example ->
                        VocabExampleRow(
                            index = index,
                            sentence = example.sentence,
                            translation = example.translation,
                            onSentenceChange = { viewModel.updateExample(index, it, example.translation) },
                            onTranslationChange = { viewModel.updateExample(index, example.sentence, it) },
                            onRemove = { viewModel.removeExample(index) }
                        )
                        if (index < viewModel.examples.lastIndex) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    TextButton(
                        onClick = { viewModel.addExample() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = PurplePrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Example", fontWeight = FontWeight.SemiBold)
                    }
                }

                // Submit button
                Button(
                    onClick = { viewModel.submit() },
                    enabled = !viewModel.isLoading,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Creating...", fontWeight = FontWeight.SemiBold)
                    } else {
                        Text("Create Vocabulary", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VocabTopicDropdown(
    topics: List<Pair<String, String>>,
    selectedTitle: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedTitle,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Select a topic", color = TextGray) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(18.dp),
            colors = vocabFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            topics.forEach { (title, id) ->
                DropdownMenuItem(
                    text = { Text(title) },
                    onClick = { onSelect(id); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VocabPartOfSpeechDropdown(selected: String?, onSelect: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Select part of speech", color = TextGray) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(18.dp),
            colors = vocabFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("None", color = TextGray) },
                onClick = { onSelect(null); expanded = false }
            )
            vocabPartsOfSpeech.forEach { pos ->
                DropdownMenuItem(
                    text = { Text(pos) },
                    onClick = { onSelect(pos); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun VocabExampleRow(
    index: Int,
    sentence: String,
    translation: String,
    onSentenceChange: (String) -> Unit,
    onTranslationChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        color = PurpleLight.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Example ${index + 1}",
                    color = PurplePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = TextGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            OutlinedTextField(
                value = sentence,
                onValueChange = onSentenceChange,
                placeholder = { Text("Sentence", color = TextGray) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = vocabFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = translation,
                onValueChange = onTranslationChange,
                placeholder = { Text("Translation (optional)", color = TextGray) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = vocabFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun VocabFormSection(title: String, content: @Composable () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFE8E3FA)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                color = TextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun VocabFormField(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun vocabFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PurplePrimary,
    unfocusedBorderColor = BorderGray,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = PurplePrimary
)
