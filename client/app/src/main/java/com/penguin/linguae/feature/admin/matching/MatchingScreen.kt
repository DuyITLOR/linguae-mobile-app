package com.penguin.linguae.feature.admin.matching

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.data.model.adminmatching.MatchingDraftPair
import com.penguin.linguae.data.model.adminmatching.MatchingManageMode
import com.penguin.linguae.data.model.adminmatching.MatchingQuestionUi
import com.penguin.linguae.feature.admin.matching.component.MatchingDeleteDialog
import com.penguin.linguae.feature.admin.matching.component.MatchingFormContent
import com.penguin.linguae.feature.admin.matching.component.MatchingHeader
import com.penguin.linguae.feature.admin.matching.component.MatchingListContent
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun MatchingScreen(
    topicId: String,
    topicTitle: String,
    onBack: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var mode by remember { mutableStateOf(MatchingManageMode.LIST) }
    var searchQuery by remember { mutableStateOf("") }
    var questions by remember { mutableStateOf(sampleMatchingQuestions(topicId)) }
    var editingQuestionId by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<MatchingQuestionUi?>(null) }

    var draftTitle by remember { mutableStateOf("") }
    var draftPairs by remember { mutableStateOf(defaultMatchingDraftPairs()) }

    fun resetDraft() {
        editingQuestionId = null
        draftTitle = ""
        draftPairs = defaultMatchingDraftPairs()
    }

    fun startCreate() {
        resetDraft()
        mode = MatchingManageMode.CREATE
    }

    fun startEdit(question: MatchingQuestionUi) {
        editingQuestionId = question.id
        draftTitle = question.title
        draftPairs = question.pairs.ifEmpty { defaultMatchingDraftPairs() }
        mode = MatchingManageMode.EDIT
    }

    fun cancelForm() {
        resetDraft()
        mode = MatchingManageMode.LIST
    }

    fun showMessage(message: String) {
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    fun submitForm() {
        val normalizedTitle = draftTitle.trim()
        val normalizedPairs = draftPairs.map {
            it.copy(
                leftText = it.leftText.trim(),
                rightText = it.rightText.trim()
            )
        }

        if (normalizedTitle.isBlank()) {
            showMessage("Title is required")
            return
        }

        val hasIncompletePair = normalizedPairs.any {
            it.leftText.isBlank() xor it.rightText.isBlank()
        }
        if (hasIncompletePair) {
            showMessage("Each matching pair needs both left and right text")
            return
        }

        val completedPairs = normalizedPairs.filter {
            it.leftText.isNotBlank() && it.rightText.isNotBlank()
        }
        if (completedPairs.size < 2) {
            showMessage("Please add at least 2 complete pairs")
            return
        }

        val isCreating = editingQuestionId == null
        val payload = MatchingQuestionUi(
            id = editingQuestionId ?: UUID.randomUUID().toString(),
            topicId = topicId,
            title = normalizedTitle,
            pairs = completedPairs.mapIndexed { index, pair ->
                pair.copy(id = index + 1)
            }
        )

        questions = if (isCreating) {
            listOf(payload) + questions
        } else {
            questions.map { if (it.id == payload.id) payload else it }
        }

        mode = MatchingManageMode.LIST
        resetDraft()
        showMessage(
            if (isCreating) "Matching set created locally"
            else "Matching set updated locally"
        )
    }

    if (deleteTarget != null) {
        MatchingDeleteDialog(
            title = deleteTarget?.title.orEmpty(),
            onConfirm = {
                val questionId = deleteTarget?.id
                if (questionId != null) {
                    questions = questions.filterNot { it.id == questionId }
                    deleteTarget = null
                    showMessage("Matching set removed locally")
                }
            },
            onDismiss = { deleteTarget = null }
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
                mode = mode,
                topicTitle = topicTitle,
                questionCount = questions.size,
                onBack = {
                    if (mode == MatchingManageMode.LIST) onBack() else cancelForm()
                }
            )

            when (mode) {
                MatchingManageMode.LIST -> MatchingListContent(
                    topicTitle = topicTitle,
                    questions = questions,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onAdd = { startCreate() },
                    onEdit = { startEdit(it) },
                    onDelete = { deleteTarget = it }
                )

                MatchingManageMode.CREATE,
                MatchingManageMode.EDIT -> MatchingFormContent(
                    topicId = topicId,
                    topicTitle = topicTitle,
                    mode = mode,
                    title = draftTitle,
                    pairs = draftPairs,
                    onTitleChange = { draftTitle = it },
                    onLeftChange = { pairId, value ->
                        draftPairs = draftPairs.map {
                            if (it.id == pairId) it.copy(leftText = value) else it
                        }
                    },
                    onRightChange = { pairId, value ->
                        draftPairs = draftPairs.map {
                            if (it.id == pairId) it.copy(rightText = value) else it
                        }
                    },
                    onAddPair = {
                        val nextId = (draftPairs.maxOfOrNull { it.id } ?: 0) + 1
                        draftPairs = draftPairs + MatchingDraftPair(id = nextId)
                    },
                    onRemovePair = { pairId ->
                        if (draftPairs.size <= 2) {
                            showMessage("A matching set needs at least 2 pairs")
                        } else {
                            draftPairs = draftPairs.filterNot { it.id == pairId }
                        }
                    },
                    onSubmit = { submitForm() },
                    onCancel = { cancelForm() }
                )
            }
        }
    }
}

private fun defaultMatchingDraftPairs() = listOf(
    MatchingDraftPair(id = 1),
    MatchingDraftPair(id = 2)
)

private fun sampleMatchingQuestions(topicId: String) = listOf(
    MatchingQuestionUi(
        id = "matching-1",
        topicId = topicId,
        title = "Match the phrasal verbs to their meanings",
        pairs = listOf(
            MatchingDraftPair(1, "turn down", "reject an offer"),
            MatchingDraftPair(2, "run into", "meet unexpectedly"),
            MatchingDraftPair(3, "carry on", "continue doing something")
        )
    ),
    MatchingQuestionUi(
        id = "matching-2",
        topicId = topicId,
        title = "Connect workplace words with simple definitions",
        pairs = listOf(
            MatchingDraftPair(1, "deadline", "the final time to finish work"),
            MatchingDraftPair(2, "agenda", "a list of items for a meeting"),
            MatchingDraftPair(3, "feedback", "comments for improvement"),
            MatchingDraftPair(4, "proposal", "a suggested plan or idea")
        )
    )
)

@Preview(showBackground = true, backgroundColor = 0xFFF4F5FA)
@Composable
private fun MatchingScreenPreview() {
    MatchingScreen(
        topicId = "topic-matching-demo",
        topicTitle = "Business English"
    )
}
