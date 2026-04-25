package com.penguin.linguae.feature.admin.cloze

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.data.model.ClozeQuestion
import com.penguin.linguae.feature.admin.cloze.viewmodel.ClozeManageMode
import com.penguin.linguae.feature.admin.cloze.viewmodel.ManageClozeViewModel
import com.penguin.linguae.feature.admin.cloze.viewmodel.ManageClozeViewModelFactory
import com.penguin.linguae.core.ui.component.ErrorView

@Composable
fun ManageClozeScreen(
    topicId: String,
    topicTitle: String,
    viewModel: ManageClozeViewModel = viewModel(factory = ManageClozeViewModelFactory(
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
            title = { Text("Delete Cloze Question") },
            text = { Text("Are you sure you want to delete this question? This cannot be undone.") },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
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
                        if (viewModel.mode != ClozeManageMode.LIST) viewModel.cancelForm() else onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SurfaceColor)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = when (viewModel.mode) {
                                ClozeManageMode.LIST -> "Cloze: $topicTitle"
                                ClozeManageMode.CREATE -> "New Cloze Question"
                                ClozeManageMode.EDIT -> "Edit Question"
                            },
                            color = SurfaceColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = when (viewModel.mode) {
                                ClozeManageMode.LIST -> "${viewModel.questions.size} questions"
                                ClozeManageMode.CREATE -> "Fill the sentence and options"
                                ClozeManageMode.EDIT -> "Update sentence and options"
                            },
                            color = SurfaceColor.copy(alpha = 0.75f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            when (viewModel.mode) {
                ClozeManageMode.LIST -> ClozeListContent(
                    questions = viewModel.questions,
                    isLoading = viewModel.isLoading,
                    error = viewModel.error,
                    onRetry = { viewModel.loadQuestions() },
                    onAdd = { viewModel.startCreate() },
                    onEdit = { viewModel.startEdit(it) },
                    onDelete = { viewModel.confirmDelete(it) }
                )
                else -> ClozeFormContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun ClozeListContent(
    questions: List<ClozeQuestion>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (ClozeQuestion) -> Unit,
    onDelete: (Int) -> Unit
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
                Text("New Question", fontWeight = FontWeight.SemiBold)
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PurplePrimary)
            }
        } else if (error != null && questions.isEmpty()) {
            ErrorView(
                message = error,
                onRetry = onRetry
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                items(questions) { question ->
                    ClozeManageCard(
                        question = question,
                        onEdit = { onEdit(question) },
                        onDelete = { onDelete(question.questionID) }
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun ClozeManageCard(question: ClozeQuestion, onEdit: () -> Unit, onDelete: () -> Unit) {
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
                Text(question.question, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 2)
                Text("ID: ${question.questionID}", color = TextGray, fontSize = 12.sp)
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
private fun ClozeFormContent(viewModel: ManageClozeViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Sentence Input
        ClozeFormSection(title = "Question Sentence") {
            Text("Use '___' for the blank space.", color = TextGray, fontSize = 12.sp)
            OutlinedTextField(
                value = viewModel.sentence,
                onValueChange = { viewModel.sentence = it },
                placeholder = { Text("e.g. She ___ to the store yesterday.", color = TextGray) },
                minLines = 3,
                shape = RoundedCornerShape(18.dp),
                colors = clozeFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Options Input
        ClozeFormSection(title = "Options") {
            Text("Select the correct answer and fill in the text.", color = TextGray, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            
            viewModel.options.forEachIndexed { index, opt ->
                val borderColor = if (opt.isCorrect) PurplePrimary else BorderGray
                val backgroundColor = if (opt.isCorrect) PurpleLight.copy(alpha = 0.2f) else Color.Transparent

                Surface(
                    color = backgroundColor,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(if (opt.isCorrect) 2.dp else 1.dp, borderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateOption(index, opt.optionText, true) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = opt.isCorrect,
                            onClick = { viewModel.updateOption(index, opt.optionText, true) },
                            colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
                        )
                        OutlinedTextField(
                            value = opt.optionText,
                            onValueChange = { viewModel.updateOption(index, it, opt.isCorrect) },
                            placeholder = { Text("Option ${index + 1}", color = TextGray) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        if (opt.isCorrect) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { viewModel.submit() },
            enabled = !viewModel.isLoading,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(54.dp)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (viewModel.mode == ClozeManageMode.EDIT) "Update Question" else "Create Question",
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

@Composable
private fun ClozeFormSection(title: String, content: @Composable () -> Unit) {
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
private fun clozeFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PurplePrimary,
    unfocusedBorderColor = BorderGray,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = PurplePrimary
)
