package com.penguin.linguae.feature.dailyMission.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.ClozeQuestionWithOptions
import com.penguin.linguae.data.repository.DailyMissionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class DailyClozeViewModel(val taskId: String) : ViewModel() {

    private val repository = DailyMissionRepository()

    private var totalQuestion by Delegates.notNull<Int>()
    private var correctCount = 0

    private val _correctCount = MutableStateFlow(0)
    val correctCountState = _correctCount.asStateFlow()

    private val _totalQuestion = MutableStateFlow(0)
    val totalQuestionState = _totalQuestion.asStateFlow()

    private val _questionsWithOptions = MutableStateFlow<List<ClozeQuestionWithOptions>>(emptyList())
    val questionsWithOptions = _questionsWithOptions.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    private val _selectedOptionId = MutableStateFlow<Int?>(null)
    val selectedOptionId = _selectedOptionId.asStateFlow()

    private val _isAnswered = MutableStateFlow(false)
    val isAnswered = _isAnswered.asStateFlow()

    private val _isLastQuestion = MutableStateFlow(false)
    val isLastQuestion = _isLastQuestion.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _showRetry = MutableStateFlow(false)
    val showRetry = _showRetry.asStateFlow()

    init {
        fetchQuestions()
    }

    fun fetchQuestions() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getDailyClozeQuestions(taskId)
                .onSuccess { questions ->
                    _questionsWithOptions.value = questions.map { q ->
                        ClozeQuestionWithOptions(question = q, options = q.clozeOptions)
                    }
                    totalQuestion = questions.size
                    _totalQuestion.value = questions.size
                    if (questions.size == 1) _isLastQuestion.value = true
                }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun onOptionSelected(optionId: Int) {
        if (_isAnswered.value) return
        val current = _questionsWithOptions.value[_currentIndex.value]
        if (current.options.find { it.id == optionId }?.isCorrect == true) {
            correctCount++
            _correctCount.value = correctCount
        }
        _selectedOptionId.value = optionId
        _isAnswered.value = true
    }

    fun onNextQuestionClicked() {
        if (!_isAnswered.value) return
        if (_currentIndex.value >= _questionsWithOptions.value.lastIndex - 1) _isLastQuestion.value = true
        _currentIndex.value++
        _selectedOptionId.value = null
        _isAnswered.value = false
    }

    fun onFinish() {
        if (correctCount == totalQuestion) {
            viewModelScope.launch {
                repository.completeDailyExercise(taskId)
                _navigationEvent.emit(Unit)
            }
        } else {
            _showRetry.value = true
        }
    }

    fun onRetry() {
        correctCount = 0
        _correctCount.value = 0
        _currentIndex.value = 0
        _selectedOptionId.value = null
        _isAnswered.value = false
        _isLastQuestion.value = _questionsWithOptions.value.size == 1
        _showRetry.value = false
    }

    fun onSkip() {
        viewModelScope.launch {
            _navigationEvent.emit(Unit)
        }
    }
}
