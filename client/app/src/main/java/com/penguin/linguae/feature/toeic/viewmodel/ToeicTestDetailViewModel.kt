package com.penguin.linguae.feature.toeic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.ReadingPart5Question
import com.penguin.linguae.data.model.ReadingPart6Question
import com.penguin.linguae.data.model.SubmitToeicAnswerRequest
import com.penguin.linguae.data.model.ToeicAnswerRequest
import com.penguin.linguae.data.repository.ToeicRepository
import kotlinx.coroutines.async
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
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val submitMessage: String? = null,
    val selectedPart: ToeicPart = ToeicPart.PART_5,
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val part5Answers: Map<Int, Int> = emptyMap(),
    val part6Answers: Map<String, Int> = emptyMap(),
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
            ToeicPart.PART_6 -> part6Questions.count { question ->
                question.readingPart6Options.all { option -> part6Answers.containsKey(option.id) }
            }
        }

    val totalPart6SubQuestions: Int
        get() = part6Questions.sumOf { it.readingPart6Options.size }
}

class ToeicTestDetailViewModel : ViewModel() {
    private val repository = ToeicRepository()

    private val _uiState = MutableStateFlow(ToeicTestUiState(isLoading = true))
    val uiState: StateFlow<ToeicTestUiState> = _uiState.asStateFlow()

    private var currentToeicId: String? = null

    fun initialize(toeicId: String) {
        val shouldReload = currentToeicId != toeicId ||
            (_uiState.value.part5Questions.isEmpty() && _uiState.value.part6Questions.isEmpty())
        currentToeicId = toeicId
        if (shouldReload) {
            loadAllParts(toeicId)
        }
    }

    fun retry() {
        currentToeicId?.let(::loadAllParts)
    }

    fun onSelectPart(part: ToeicPart) {
        val state = _uiState.value
        if (state.selectedPart == part) return
        _uiState.value = state.copy(
            selectedPart = part,
            currentQuestionIndex = 0,
            selectedAnswerIndex = selectedAnswerFor(0, part)
        )
    }

    fun onSelectAnswer(index: Int) {
        val state = _uiState.value
        if (state.selectedPart != ToeicPart.PART_5) return
        _uiState.value = state.copy(
            selectedAnswerIndex = index,
            part5Answers = state.part5Answers + (state.currentQuestionIndex to index)
        )
    }

    fun onSelectPart6Answer(optionId: String, answerIndex: Int) {
        val state = _uiState.value
        _uiState.value = state.copy(
            part6Answers = state.part6Answers + (optionId to answerIndex)
        )
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

    fun submitToeicTest() {
        val toeicId = currentToeicId ?: return
        val state = _uiState.value
        val answers = buildSubmissionAnswers(state)
        val totalRequiredAnswers = state.part5Questions.size + state.totalPart6SubQuestions

        if (answers.size != totalRequiredAnswers) {
            _uiState.value = state.copy(
                submitMessage = "Vui lòng trả lời hết câu hỏi Part 5 và Part 6 trước khi nộp bài."
            )
            return
        }

        val correctAnswers = calculateCorrectAnswers(state)
        _uiState.value = state.copy(
            isSubmitting = true,
            submitMessage = null,
            errorMessage = null
        )

        viewModelScope.launch {
            repository.submitToeicAnswer(
                SubmitToeicAnswerRequest(
                    toeicId = toeicId,
                    answers = answers,
                    correctAnswers = correctAnswers
                )
            )
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        submitMessage = response.message
                    )
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        submitMessage = throwable.message ?: "Nộp bài thất bại"
                    )
                }
        }
    }

    fun consumeSubmitMessage() {
        _uiState.value = _uiState.value.copy(submitMessage = null)
    }

    private fun loadAllParts(toeicId: String) {
        val selectedPart = _uiState.value.selectedPart
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            submitMessage = null,
            currentQuestionIndex = 0,
            selectedAnswerIndex = null
        )

        viewModelScope.launch {
            val part5Deferred = async { repository.getReadingPart5Questions(toeicId) }
            val part6Deferred = async { repository.getReadingPart6Questions(toeicId) }

            val part5Result = part5Deferred.await()
            val part6Result = part6Deferred.await()

            val error = part5Result.exceptionOrNull() ?: part6Result.exceptionOrNull()
            if (error != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Failed to load TOEIC test"
                )
                return@launch
            }

            val part5Questions = part5Result.getOrDefault(emptyList())
            val part6Questions = part6Result.getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                selectedPart = selectedPart,
                part5Questions = part5Questions,
                part6Questions = part6Questions,
                currentQuestionIndex = 0,
                selectedAnswerIndex = selectedAnswerFor(0, selectedPart)
            )
        }
    }

    private fun selectedAnswerFor(questionIndex: Int, part: ToeicPart): Int? {
        val state = _uiState.value
        return when (part) {
            ToeicPart.PART_5 -> state.part5Answers[questionIndex]
            ToeicPart.PART_6 -> null
        }
    }

    private fun buildSubmissionAnswers(state: ToeicTestUiState): List<ToeicAnswerRequest> {
        val part5Submission = state.part5Questions.mapIndexedNotNull { index, question ->
            state.part5Answers[index]?.let { selected ->
                ToeicAnswerRequest(
                    questionId = question.id,
                    selected = selected,
                    part = 5
                )
            }
        }

        val part6Submission = state.part6Questions.flatMap { question ->
            question.readingPart6Options.mapNotNull { option ->
                state.part6Answers[option.id]?.let { selected ->
                    ToeicAnswerRequest(
                        questionId = option.id,
                        selected = selected,
                        part = 6
                    )
                }
            }
        }

        return part5Submission + part6Submission
    }

    private fun calculateCorrectAnswers(state: ToeicTestUiState): Int {
        val part5Correct = state.part5Questions.mapIndexed { index, question ->
            if (state.part5Answers[index] == question.answer) 1 else 0
        }.sum()

        val part6Correct = state.part6Questions.sumOf { question ->
            question.readingPart6Options.count { option ->
                state.part6Answers[option.id] == option.answer
            }
        }

        return part5Correct + part6Correct
    }
}
