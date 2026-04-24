package com.penguin.linguae.feature.admin.toeic.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.CreateToeicEditorRequest
import com.penguin.linguae.data.model.ToeicLevel
import com.penguin.linguae.data.model.ToeicPart5Draft
import com.penguin.linguae.data.model.ToeicPart6PassageDraft
import com.penguin.linguae.data.model.ToeicPart6QuestionDraft
import com.penguin.linguae.data.model.UpdateToeicEditorRequest
import com.penguin.linguae.data.repository.ToeicRepository
import kotlinx.coroutines.launch

class ToeicEditorViewModel : ViewModel() {
    private val repository = ToeicRepository()

    var uiState by mutableStateOf(ToeicEditorState())
        private set

    fun init(toeicId: String?) {
        if (toeicId.isNullOrBlank()) {
            if (uiState.part5Questions.isEmpty()) {
                uiState = uiState.copy(part5Questions = listOf(ToeicPart5Draft()))
            }
            return
        }

        if (uiState.toeicId == toeicId) return
        loadToeicDetail(toeicId)
    }

    fun updateTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun updateLevel(level: ToeicLevel) {
        uiState = uiState.copy(level = level)
    }

    fun addPart5Question() {
        uiState = uiState.copy(part5Questions = uiState.part5Questions + ToeicPart5Draft())
    }

    fun addPart6Passage() {
        val newPassage = ToeicPart6PassageDraft(
            questions = listOf(ToeicPart6QuestionDraft()),
        )
        uiState = uiState.copy(part6Passages = uiState.part6Passages + newPassage)
    }

    fun save(onSuccess: () -> Unit = {}) {
        if (uiState.title.isBlank()) {
            uiState = uiState.copy(errorMessage = "Title is required")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, errorMessage = null, successMessage = null)
            val result = if (uiState.isEditMode) {
                repository.updateToeicEditor(
                    id = uiState.toeicId.orEmpty(),
                    request = UpdateToeicEditorRequest(
                        title = uiState.title.trim(),
                        level = uiState.level,
                        part5Questions = uiState.part5Questions,
                        part6Passages = uiState.part6Passages,
                    ),
                )
            } else {
                repository.createToeicEditor(
                    CreateToeicEditorRequest(
                        title = uiState.title.trim(),
                        level = uiState.level,
                        part5Questions = uiState.part5Questions,
                        part6Passages = uiState.part6Passages,
                    ),
                )
            }

            result
                .onSuccess {
                    uiState = uiState.copy(
                        isSaving = false,
                        successMessage = if (uiState.isEditMode) "TOEIC updated" else "TOEIC created",
                    )
                    onSuccess()
                }
                .onFailure {
                    uiState = uiState.copy(
                        isSaving = false,
                        errorMessage = it.message ?: "Failed to save TOEIC",
                    )
                }
        }
    }

    private fun loadToeicDetail(toeicId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, toeicId = toeicId)
            repository.getToeicEditorDetail(toeicId)
                .onSuccess { detail ->
                    uiState = uiState.copy(
                        toeicId = detail.id,
                        title = detail.title,
                        level = runCatching { ToeicLevel.valueOf(detail.level) }
                            .getOrElse { ToeicLevel.BEGINNER },
                        part5Questions = detail.readingPart5Questions.mapIndexed { index, question ->
                            ToeicPart5Draft(
                                id = question.id,
                                question = question.question,
                                options = question.options,
                                answerIndex = question.answer,
                                displayOrder = index,
                            )
                        },
                        part6Passages = detail.readingPart6Questions.map { passage ->
                            ToeicPart6PassageDraft(
                                id = passage.id,
                                passage = passage.question,
                                questions = passage.readingPart6Options
                                    .sortedBy { it.title }
                                    .mapIndexed { index, question ->
                                        ToeicPart6QuestionDraft(
                                            id = question.id,
                                            title = question.title,
                                            question = "Question ${question.title}",
                                            options = question.option,
                                            answerIndex = question.answer,
                                            displayOrder = index,
                                        )
                                    },
                            )
                        },
                        isLoading = false,
                    )
                }
                .onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = it.message ?: "Failed to load TOEIC detail",
                    )
                }
        }
    }
}
