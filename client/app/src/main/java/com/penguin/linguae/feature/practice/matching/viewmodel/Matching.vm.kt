package com.penguin.linguae.feature.practice.matching.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.data.model.MatchingPairUi
import com.penguin.linguae.data.model.MatchingQuestionUi
import com.penguin.linguae.data.model.ResultData
import com.penguin.linguae.data.repository.MatchingRepository
import com.penguin.linguae.data.repository.ResultRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MatchingItem(
    val id: String,
    val text: String,
    val isMatched: Boolean = false,
    val isSelected: Boolean = false,
    val isCorrect: Boolean? = null // null: not verified, true: correct, false: incorrect
)

class MatchingViewModel(
    val topicId: String
) : ViewModel() {
    private val repository = MatchingRepository()
    private val resultRepository = ResultRepository

    private val _questions = MutableStateFlow<List<MatchingQuestionUi>>(emptyList())
    val questions = _questions.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    private val _leftItems = MutableStateFlow<List<MatchingItem>>(emptyList())
    val leftItems = _leftItems.asStateFlow()

    private val _rightItems = MutableStateFlow<List<MatchingItem>>(emptyList())
    val rightItems = _rightItems.asStateFlow()

    private val _selectedLeftId = MutableStateFlow<String?>(null)
    val selectedLeftId = _selectedLeftId.asStateFlow()

    private val _selectedRightId = MutableStateFlow<String?>(null)
    val selectedRightId = _selectedRightId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var totalCorrectCount = 0
    private var totalQuestionCount = 0

    init {
        fetchMatchingQuestions(topicId)
    }

    fun fetchMatchingQuestions(topicId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMatchingQuestions(topicId)
                .onSuccess {
                    _questions.value = it
                    totalQuestionCount = it.size
                    if (it.isNotEmpty()) {
                        setupCurrentQuestion(it[0])
                    }
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to load questions"
                }
            _isLoading.value = false
        }
    }

    private fun setupCurrentQuestion(question: MatchingQuestionUi) {
        val pairs = question.pairs
        
        // Shuffling left and right independently
        val left = pairs.map { MatchingItem(id = it.id, text = it.leftText) }.shuffled()
        val right = pairs.map { MatchingItem(id = it.id, text = it.rightText) }.shuffled()
        
        _leftItems.value = left
        _rightItems.value = right
        _selectedLeftId.value = null
        _selectedRightId.value = null
    }

    fun onLeftItemSelected(id: String) {
        if (_leftItems.value.find { it.id == id }?.isMatched == true) return
        
        _selectedLeftId.value = if (_selectedLeftId.value == id) null else id
        checkMatch()
    }

    fun onRightItemSelected(id: String) {
        if (_rightItems.value.find { it.id == id }?.isMatched == true) return
        
        _selectedRightId.value = if (_selectedRightId.value == id) null else id
        checkMatch()
    }

    private fun checkMatch() {
        val leftId = _selectedLeftId.value
        val rightId = _selectedRightId.value

        if (leftId != null && rightId != null) {
            if (leftId == rightId) {
                // Correct match
                _leftItems.value = _leftItems.value.map {
                    if (it.id == leftId) it.copy(isMatched = true, isCorrect = true) else it
                }
                _rightItems.value = _rightItems.value.map {
                    if (it.id == rightId) it.copy(isMatched = true, isCorrect = true) else it
                }
            } else {
                // Incorrect match - briefly show error then reset?
                // For now just reset selection
            }
            _selectedLeftId.value = null
            _selectedRightId.value = null
            
            checkCompletion()
        }
    }

    private fun checkCompletion() {
        if (_leftItems.value.all { it.isMatched }) {
            totalCorrectCount++ // Current question completed correctly
            
            if (_currentIndex.value < _questions.value.size - 1) {
                // Move to next question after a delay or user click?
                // For now, let's just move immediately or wait for user
            } else {
                // All questions done
            }
        }
    }

    fun onNextQuestion() {
        if (_currentIndex.value < _questions.value.size - 1) {
            _currentIndex.value++
            setupCurrentQuestion(_questions.value[_currentIndex.value])
        }
    }

    fun onResultClicked() {
        val result = ResultData.MatchingResult(
            total = totalQuestionCount,
            score = totalCorrectCount,
            topicId = topicId
        )
        resultRepository.saveResult(result)
        viewModelScope.launch {
            _navigationEvent.emit(Screen.ResultScreen.route)
        }
    }
}
