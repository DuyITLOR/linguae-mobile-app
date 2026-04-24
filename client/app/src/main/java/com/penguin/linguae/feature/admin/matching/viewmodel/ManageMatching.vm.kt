package com.penguin.linguae.feature.admin.matching.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.CreateMatchingQuestionRequest
import com.penguin.linguae.data.model.MatchingDraftPair
import com.penguin.linguae.data.model.MatchingManageMode
import com.penguin.linguae.data.model.MatchingPairRequest
import com.penguin.linguae.data.model.MatchingQuestionUi
import com.penguin.linguae.data.model.UpdateMatchingQuestionRequest
import com.penguin.linguae.data.repository.MatchingRepository
import kotlinx.coroutines.launch

class ManageMatchingViewModel(
    val scopedTopicId: String,
    val scopedTopicTitle: String
) : ViewModel() {

    private val matchingRepository = MatchingRepository()

    var questions by mutableStateOf<List<MatchingQuestionUi>>(emptyList())
    var mode by mutableStateOf(MatchingManageMode.LIST)
    var editingQuestion by mutableStateOf<MatchingQuestionUi?>(null)

    var title by mutableStateOf("")
    var pairs by mutableStateOf(defaultMatchingDraftPairs())

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var deleteTarget by mutableStateOf<MatchingQuestionUi?>(null)

    init {
        loadMatchingQuestions()
    }

    fun loadMatchingQuestions() {
        viewModelScope.launch {
            isLoading = true
            matchingRepository.getMatchingQuestions(scopedTopicId)
                .onSuccess { questions = it }
                .onFailure { error = it.message ?: "Failed to load matching questions" }
            isLoading = false
        }
    }

    fun startCreate() {
        editingQuestion = null
        title = ""
        pairs = defaultMatchingDraftPairs()
        mode = MatchingManageMode.CREATE
    }

    fun startEdit(question: MatchingQuestionUi) {
        editingQuestion = question
        title = question.title
        pairs = question.pairs
            .sortedBy { it.displayOrder }
            .mapIndexed { index, pair ->
                MatchingDraftPair(
                    id = index + 1,
                    leftText = pair.leftText,
                    rightText = pair.rightText
                )
            }
            .ifEmpty { defaultMatchingDraftPairs() }
        mode = MatchingManageMode.EDIT
    }

    fun cancelForm() {
        editingQuestion = null
        title = ""
        pairs = defaultMatchingDraftPairs()
        mode = MatchingManageMode.LIST
    }

    fun updateTitle(value: String) {
        title = value
    }

    fun updateLeftText(pairId: Int, value: String) {
        pairs = pairs.map {
            if (it.id == pairId) it.copy(leftText = value) else it
        }
    }

    fun updateRightText(pairId: Int, value: String) {
        pairs = pairs.map {
            if (it.id == pairId) it.copy(rightText = value) else it
        }
    }

    fun addPair() {
        val nextId = (pairs.maxOfOrNull { it.id } ?: 0) + 1
        pairs = pairs + MatchingDraftPair(id = nextId)
    }

    fun removePair(pairId: Int) {
        if (pairs.size <= 2) {
            error = "A matching set needs at least 2 pairs"
            return
        }
        pairs = pairs.filterNot { it.id == pairId }
    }

    fun submit() {
        val normalizedTitle = title.trim()
        val normalizedPairs = pairs.map {
            it.copy(
                leftText = it.leftText.trim(),
                rightText = it.rightText.trim()
            )
        }

        if (normalizedTitle.isBlank()) {
            error = "Title is required"
            return
        }

        if (normalizedPairs.any { it.leftText.isBlank() xor it.rightText.isBlank() }) {
            error = "Each matching pair needs both left and right text"
            return
        }

        val completedPairs = normalizedPairs.filter {
            it.leftText.isNotBlank() && it.rightText.isNotBlank()
        }

        if (completedPairs.size < 2) {
            error = "Please add at least 2 complete pairs"
            return
        }

        val pairRequests = completedPairs.mapIndexed { index, pair ->
            MatchingPairRequest(
                leftText = pair.leftText,
                rightText = pair.rightText,
                displayOrder = index
            )
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            if (mode == MatchingManageMode.EDIT) {
                val questionId = editingQuestion?.id
                if (questionId == null) {
                    error = "Matching question not found"
                    isLoading = false
                    return@launch
                }

                matchingRepository.updateMatchingQuestion(
                    id = questionId,
                    request = UpdateMatchingQuestionRequest(
                        topicId = scopedTopicId,
                        title = normalizedTitle,
                        pairs = pairRequests
                    )
                )
                    .onSuccess {
                        cancelForm()
                        loadMatchingQuestions()
                    }
                    .onFailure {
                        error = it.message ?: "Failed to update matching question"
                        isLoading = false
                    }
            } else {
                matchingRepository.createMatchingQuestion(
                    CreateMatchingQuestionRequest(
                        topicId = scopedTopicId,
                        title = normalizedTitle,
                        pairs = pairRequests
                    )
                )
                    .onSuccess {
                        cancelForm()
                        loadMatchingQuestions()
                    }
                    .onFailure {
                        error = it.message ?: "Failed to create matching question"
                        isLoading = false
                    }
            }
        }
    }

    fun confirmDelete(question: MatchingQuestionUi) {
        deleteTarget = question
    }

    fun cancelDelete() {
        deleteTarget = null
    }

    fun executeDelete() {
        val question = deleteTarget ?: return
        deleteTarget = null
        viewModelScope.launch {
            isLoading = true
            matchingRepository.deleteMatchingQuestion(question.id)
                .onSuccess { loadMatchingQuestions() }
                .onFailure {
                    error = it.message ?: "Failed to delete matching question"
                    isLoading = false
                }
        }
    }

    fun clearError() {
        error = null
    }
}

class ManageMatchingViewModelFactory(
    private val topicId: String,
    private val topicTitle: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ManageMatchingViewModel(topicId, topicTitle) as T
    }
}

private fun defaultMatchingDraftPairs() = listOf(
    MatchingDraftPair(id = 1),
    MatchingDraftPair(id = 2)
)
