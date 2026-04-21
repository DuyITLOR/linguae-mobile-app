package com.penguin.linguae.feature.toeic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.ReadingPart5Question
import com.penguin.linguae.data.model.ReadingPart6Question
import com.penguin.linguae.data.repository.ToeicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ToeicPart {
    PART_5,
    PART_6
}

data class ToeicTestUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedPart: ToeicPart = ToeicPart.PART_5,
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val part5Answers: Map<Int, Int> = emptyMap(),
    val part6Answers: Map<Int, Int> = emptyMap(),
    val part5Questions: List<ReadingPart5Question> = emptyList(),
    val part6Questions: List<ReadingPart6Question> = emptyList()
) {
    val totalQuestions: Int
        get() = when (selectedPart) {
            ToeicPart.PART_5 -> part5Questions.size
            ToeicPart.PART_6 -> part6Questions.size
        }

    val answeredQuestions: Int
        get() = when (selectedPart) {
            ToeicPart.PART_5 -> part5Answers.size
            ToeicPart.PART_6 -> part6Answers.size
        }
}

class ToeicTestDetailViewModel : ViewModel() {
    private val repository = ToeicRepository()

    private val _uiState = MutableStateFlow(ToeicTestUiState(isLoading = true))
    val uiState: StateFlow<ToeicTestUiState> = _uiState.asStateFlow()

    fun initialize(toeicId: String) {
        if (_uiState.value.part5Questions.isNotEmpty() || _uiState.value.isLoading.not()) return
        loadPart(toeicId = toeicId, part = ToeicPart.PART_5)
    }

    fun onSelectPart(toeicId: String, part: ToeicPart) {
        val currentState = _uiState.value
        if (currentState.selectedPart == part && currentState.totalQuestions > 0) return
        loadPart(toeicId = toeicId, part = part)
    }

    fun onSelectAnswer(index: Int) {
        val state = _uiState.value
        _uiState.value = when (state.selectedPart) {
            ToeicPart.PART_5 -> state.copy(
                selectedAnswerIndex = index,
                part5Answers = state.part5Answers + (state.currentQuestionIndex to index)
            )
            ToeicPart.PART_6 -> state.copy(
                selectedAnswerIndex = index,
                part6Answers = state.part6Answers + (state.currentQuestionIndex to index)
            )
        }
    }

    fun onNextQuestion() {
        val state = _uiState.value
        if (state.totalQuestions == 0) return
        val nextIndex = (state.currentQuestionIndex + 1).coerceAtMost(state.totalQuestions - 1)
        _uiState.value = state.copy(
            currentQuestionIndex = nextIndex,
            selectedAnswerIndex = selectedAnswerFor(nextIndex, state.selectedPart)
        )
    }

    fun onPreviousQuestion() {
        val state = _uiState.value
        if (state.totalQuestions == 0) return
        val previousIndex = (state.currentQuestionIndex - 1).coerceAtLeast(0)
        _uiState.value = state.copy(
            currentQuestionIndex = previousIndex,
            selectedAnswerIndex = selectedAnswerFor(previousIndex, state.selectedPart)
        )
    }

    fun jumpToQuestion(index: Int) {
        val state = _uiState.value
        if (index !in 0 until state.totalQuestions) return
        _uiState.value = state.copy(
            currentQuestionIndex = index,
            selectedAnswerIndex = selectedAnswerFor(index, state.selectedPart)
        )
    }

    private fun loadPart(toeicId: String, part: ToeicPart) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            selectedPart = part,
            currentQuestionIndex = 0,
            selectedAnswerIndex = null
        )
        viewModelScope.launch {
            when (part) {
                ToeicPart.PART_5 -> {
                    repository.getReadingPart5Questions(toeicId)
                        .onSuccess { questions ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                part5Questions = questions,
                                selectedAnswerIndex = _uiState.value.part5Answers[0]
                            )
                        }
                        .onFailure { throwable ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = throwable.message ?: "Failed to load Part 5 questions"
                            )
                        }
                }

                ToeicPart.PART_6 -> {
                    repository.getReadingPart6Questions(toeicId)
                        .onSuccess { questions ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                part6Questions = questions,
                                selectedAnswerIndex = _uiState.value.part6Answers[0]
                            )
                        }
                        .onFailure { throwable ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = throwable.message ?: "Failed to load Part 6 questions"
                            )
                        }
                }
            }
        }
    }

    private fun selectedAnswerFor(questionIndex: Int, part: ToeicPart): Int? {
        val state = _uiState.value
        return when (part) {
            ToeicPart.PART_5 -> state.part5Answers[questionIndex]
            ToeicPart.PART_6 -> state.part6Answers[questionIndex]
        }
    }
}
