package com.penguin.linguae.feature.admin.matching

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.data.model.adminmatching.MatchingManageMode
import com.penguin.linguae.feature.admin.matching.component.MatchingDeleteDialog
import com.penguin.linguae.feature.admin.matching.component.MatchingFormContent
import com.penguin.linguae.feature.admin.matching.component.MatchingHeader
import com.penguin.linguae.feature.admin.matching.component.MatchingListContent
import com.penguin.linguae.feature.admin.matching.viewmodel.ManageMatchingViewModel
import com.penguin.linguae.feature.admin.matching.viewmodel.ManageMatchingViewModelFactory

@Composable
fun MatchingScreen(
    topicId: String,
    topicTitle: String,
    onBack: () -> Unit = {},
    viewModel: ManageMatchingViewModel = viewModel(
        factory = ManageMatchingViewModelFactory(topicId, topicTitle)
    )
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    if (viewModel.deleteTarget != null) {
        MatchingDeleteDialog(
            title = viewModel.deleteTarget?.title.orEmpty(),
            onConfirm = { viewModel.executeDelete() },
            onDismiss = { viewModel.cancelDelete() }
        )
    }

    Scaffold(
        containerColor = AppBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MatchingHeader(
                mode = viewModel.mode,
                topicTitle = topicTitle,
                questionCount = viewModel.questions.size,
                onBack = {
                    if (viewModel.mode == MatchingManageMode.LIST) onBack()
                    else viewModel.cancelForm()
                }
            )

            when (viewModel.mode) {
                MatchingManageMode.LIST -> MatchingListContent(
                    topicTitle = topicTitle,
                    questions = viewModel.questions,
                    searchQuery = searchQuery,
                    isLoading = viewModel.isLoading,
                    onSearchQueryChange = { searchQuery = it },
                    onAdd = { viewModel.startCreate() },
                    onEdit = { viewModel.startEdit(it) },
                    onDelete = { viewModel.confirmDelete(it) }
                )

                MatchingManageMode.CREATE,
                MatchingManageMode.EDIT -> MatchingFormContent(
                    topicId = topicId,
                    topicTitle = topicTitle,
                    mode = viewModel.mode,
                    title = viewModel.title,
                    pairs = viewModel.pairs,
                    isSubmitting = viewModel.isLoading,
                    onTitleChange = { viewModel.updateTitle(it) },
                    onLeftChange = { pairId, value -> viewModel.updateLeftText(pairId, value) },
                    onRightChange = { pairId, value -> viewModel.updateRightText(pairId, value) },
                    onAddPair = { viewModel.addPair() },
                    onRemovePair = { pairId -> viewModel.removePair(pairId) },
                    onSubmit = { viewModel.submit() },
                    onCancel = { viewModel.cancelForm() }
                )
            }
        }
    }
}
