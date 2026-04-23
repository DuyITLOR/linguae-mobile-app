package com.penguin.linguae.feature.admin.vocabulary

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedButton
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
import com.penguin.linguae.data.model.Vocabulary
import com.penguin.linguae.feature.admin.vocabulary.viewmodel.ManageVocabularyViewModel
import com.penguin.linguae.feature.admin.vocabulary.viewmodel.ManageVocabularyViewModelFactory
import com.penguin.linguae.feature.admin.vocabulary.viewmodel.VocabManageMode

private val vocabPartsOfSpeech = listOf(
    "NOUN", "VERB", "ADJECTIVE", "ADVERB",
    "PRONOUN", "PREPOSITION", "CONJUNCTION", "INTERJECTION"
)

private val difficultyLabels = mapOf(1 to "Beginner", 2 to "Intermediate", 3 to "Advanced")
private val difficultyColors = mapOf(
    1 to Color(0xFF27AE60),
    2 to Color(0xFFE67E22),
    3 to Color(0xFFE74C3C)
)

@Composable
fun ManageVocabularyScreen(
    topicId: String,
    topicTitle: String,
    viewModel: ManageVocabularyViewModel = viewModel(factory = ManageVocabularyViewModelFactory(
        topicId,
        topicTitle
    )
    ),
    onBack: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    if (viewModel.deleteTargetId != null) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelDelete() },
            title = { Text("Delete Vocabulary") },
            text = { Text("Are you sure you want to delete this word? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.executeDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE74C3C))
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelDelete() }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = AppBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.verticalGradient(listOf(PurplePrimary, PurpleDark, PurpleDeep)))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp, vertical = 16.dp)
                ) {
                    IconButton(onClick = {
                        if (viewModel.mode != VocabManageMode.LIST) viewModel.cancelForm() else onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SurfaceColor)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = when (viewModel.mode) {
                                VocabManageMode.LIST -> viewModel.scopedTopicTitle
                                VocabManageMode.CREATE -> "New Word"
                                VocabManageMode.EDIT -> "Edit Word"
                            },
                            color = SurfaceColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = when (viewModel.mode) {
                                VocabManageMode.LIST -> "${viewModel.vocabularies.size} words"
                                VocabManageMode.CREATE -> "Add a word to ${viewModel.scopedTopicTitle}"
                                VocabManageMode.EDIT -> "Update word details"
                            },
                            color = SurfaceColor.copy(alpha = 0.75f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            when (viewModel.mode) {
                VocabManageMode.LIST -> VocabListContent(
                    vocabularies = viewModel.vocabularies,
                    isLoading = viewModel.isLoading,
                    onAdd = { viewModel.startCreate() },
                    onEdit = { viewModel.startEdit(it) },
                    onDelete = { viewModel.confirmDelete(it) }
                )
                else -> VocabFormContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun VocabListContent(
    vocabularies: List<Vocabulary>,
    isLoading: Boolean,
    onAdd: () -> Unit,
    onEdit: (Vocabulary) -> Unit,
    onDelete: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("New Word", fontWeight = FontWeight.SemiBold)
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PurplePrimary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                items(vocabularies) { vocab ->
                    VocabManageCard(
                        vocab = vocab,
                        onEdit = { onEdit(vocab) },
                        onDelete = { onDelete(vocab.id) }
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun VocabManageCard(vocab: Vocabulary, onEdit: () -> Unit, onDelete: () -> Unit) {
    val diffColor = difficultyColors[vocab.difficulty] ?: Color(0xFF7F8C8D)
    val diffLabel = difficultyLabels[vocab.difficulty] ?: vocab.difficulty.toString()

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFE8E3FA)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(vocab.word, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(vocab.meaning, color = TextGray, fontSize = 13.sp, maxLines = 1)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(diffColor.copy(alpha = 0.13f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(diffLabel, color = diffColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    if (vocab.partOfSpeech != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF1EEFF))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(vocab.partOfSpeech, color = PurplePrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PurplePrimary, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE74C3C), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun VocabFormContent(viewModel: ManageVocabularyViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        VocabFormSection(title = "Basic Info") {
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

        VocabFormSection(title = "Details") {
            VocabFormField(label = "Part of Speech") {
                VocabPartOfSpeechDropdown(selected = viewModel.partOfSpeech, onSelect = { viewModel.partOfSpeech = it })
            }
            VocabFormField(label = "Difficulty") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1, 2, 3).forEach { lvl ->
                        FilterChip(
                            selected = viewModel.difficulty == lvl,
                            onClick = { viewModel.difficulty = lvl },
                            label = { Text(difficultyLabels[lvl] ?: lvl.toString(), fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurplePrimary,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = viewModel.difficulty == lvl,
                                borderColor = BorderGray,
                                selectedBorderColor = PurplePrimary
                            )
                        )
                    }
                }
            }
        }

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
                if (index < viewModel.examples.lastIndex) Spacer(Modifier.height(4.dp))
            }
            TextButton(
                onClick = { viewModel.addExample() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = PurplePrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add Example", fontWeight = FontWeight.SemiBold)
            }
        }

        Button(
            onClick = { viewModel.submit() },
            enabled = !viewModel.isLoading,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(54.dp)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
                Text(if (viewModel.mode == VocabManageMode.CREATE) "Creating..." else "Saving...", fontWeight = FontWeight.SemiBold)
            } else {
                Text(
                    text = if (viewModel.mode == VocabManageMode.CREATE) "Create Vocabulary" else "Update Vocabulary",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }

        OutlinedButton(
            onClick = { viewModel.cancelForm() },
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, BorderGray),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Cancel", color = TextGray, fontWeight = FontWeight.SemiBold)
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
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("None", color = TextGray) }, onClick = { onSelect(null); expanded = false })
            vocabPartsOfSpeech.forEach { pos ->
                DropdownMenuItem(text = { Text(pos) }, onClick = { onSelect(pos); expanded = false })
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
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Example ${index + 1}", color = PurplePrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextGray, modifier = Modifier.size(16.dp))
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
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(text = title, color = TextDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
