package com.penguin.linguae.feature.admin.toeic.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.data.model.ToeicLevel

@Composable
fun ToeicEditorScreen(
    toeicId: String?,
    onBack: () -> Unit,
    viewModel: ToeicEditorViewModel = viewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState = viewModel.uiState

    LaunchedEffect(toeicId) {
        viewModel.init(toeicId)
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
        uiState.successMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.isEditMode) "Edit TOEIC" else "Create TOEIC")
                },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(horizontal = 24.dp))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Text("Level", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToeicLevel.entries.forEach { level ->
                    val selected = level == uiState.level
                    if (selected) {
                        Button(onClick = { viewModel.updateLevel(level) }) {
                            Text(level.name)
                        }
                    } else {
                        OutlinedButton(onClick = { viewModel.updateLevel(level) }) {
                            Text(level.name)
                        }
                    }
                }
            }

            Text(
                text = "Part 5 questions: ${uiState.part5Questions.size}",
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedButton(onClick = viewModel::addPart5Question) {
                Text("Add Part 5 Question")
            }

            Text(
                text = "Part 6 passages: ${uiState.part6Passages.size}",
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedButton(onClick = viewModel::addPart6Passage) {
                Text("Add Part 6 Passage")
            }

            Button(
                onClick = { viewModel.save(onSuccess = onBack) },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (uiState.isSaving) "Saving..." else "Save")
            }
        }
    }
}
