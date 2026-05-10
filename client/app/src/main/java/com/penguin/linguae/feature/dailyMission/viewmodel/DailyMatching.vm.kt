package com.penguin.linguae.feature.dailyMission.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.repository.DailyMissionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DailyMatchingItem(
    val id: String,
    val text: String,
    val pairIndex: Int? = null,       // null = chưa ghép, Int = màu của cặp
    val isCorrect: Boolean? = null    // null = chưa đánh giá, true/false = sau khi bấm Hoàn thành
)

class DailyMatchingViewModel(val taskId: String) : ViewModel() {

    private val repository = DailyMissionRepository()

    private val _leftItems = MutableStateFlow<List<DailyMatchingItem>>(emptyList())
    val leftItems = _leftItems.asStateFlow()

    private val _rightItems = MutableStateFlow<List<DailyMatchingItem>>(emptyList())
    val rightItems = _rightItems.asStateFlow()

    // leftId -> rightId (map các cặp user đã ghép)
    private val _pairings = MutableStateFlow<Map<String, String>>(emptyMap())

    private val _selectedLeftId = MutableStateFlow<String?>(null)
    val selectedLeftId = _selectedLeftId.asStateFlow()

    private val _selectedRightId = MutableStateFlow<String?>(null)
    val selectedRightId = _selectedRightId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // true = đã bấm Hoàn thành, đang hiện kết quả
    private val _isEvaluated = MutableStateFlow(false)
    val isEvaluated = _isEvaluated.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    val allPaired get() = _pairings.value.size == _leftItems.value.size && _leftItems.value.isNotEmpty()

    init {
        fetchQuestions()
    }

    fun fetchQuestions() {
        viewModelScope.launch {
            _isLoading.value = true
            _pairings.value = emptyMap()
            _isEvaluated.value = false
            repository.getDailyMatchingQuestions(taskId)
                .onSuccess { pairs ->
                    _leftItems.value = pairs.map { DailyMatchingItem(id = it.id, text = it.leftText) }.shuffled()
                    _rightItems.value = pairs.map { DailyMatchingItem(id = it.id, text = it.rightText) }.shuffled()
                }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun onLeftItemSelected(id: String) {
        if (_isEvaluated.value) return
        // Toggle deselect
        if (_selectedLeftId.value == id) {
            _selectedLeftId.value = null
            return
        }
        _selectedLeftId.value = id
        tryPair()
    }

    fun onRightItemSelected(id: String) {
        if (_isEvaluated.value) return
        // Toggle deselect
        if (_selectedRightId.value == id) {
            _selectedRightId.value = null
            return
        }
        _selectedRightId.value = id
        tryPair()
    }

    private fun tryPair() {
        val leftId = _selectedLeftId.value ?: return
        val rightId = _selectedRightId.value ?: return

        val currentPairings = _pairings.value.toMutableMap()

        // Xóa cặp cũ của leftId nếu có
        currentPairings.remove(leftId)

        // Xóa cặp cũ của rightId nếu nó đã được ai đó ghép
        val oldLeftForRight = currentPairings.entries.find { it.value == rightId }?.key
        if (oldLeftForRight != null) {
            currentPairings.remove(oldLeftForRight)
        }

        // Ghép cặp mới
        currentPairings[leftId] = rightId
        _pairings.value = currentPairings
        updateItemColors(currentPairings)

        _selectedLeftId.value = null
        _selectedRightId.value = null
    }

    private fun updateItemColors(pairings: Map<String, String>) {
        val orderedLeftIds = pairings.keys.toList()
        _leftItems.value = _leftItems.value.map { item ->
            val idx = orderedLeftIds.indexOf(item.id)
            item.copy(pairIndex = if (idx >= 0) idx else null, isCorrect = null)
        }
        _rightItems.value = _rightItems.value.map { item ->
            val pairedLeft = pairings.entries.find { it.value == item.id }?.key
            val idx = if (pairedLeft != null) orderedLeftIds.indexOf(pairedLeft) else -1
            item.copy(pairIndex = if (idx >= 0) idx else null, isCorrect = null)
        }
    }

    fun onFinish() {
        if (!allPaired) return
        // Đánh giá: đúng khi leftId == rightId (vì id của pair trùng nhau)
        val pairings = _pairings.value
        _leftItems.value = _leftItems.value.map { item ->
            val pairedRightId = pairings[item.id]
            item.copy(isCorrect = pairedRightId == item.id)
        }
        _rightItems.value = _rightItems.value.map { item ->
            val pairedLeftId = pairings.entries.find { it.value == item.id }?.key
            item.copy(isCorrect = pairedLeftId == item.id)
        }
        _isEvaluated.value = true

        viewModelScope.launch {
            delay(1500) // Cho user nhìn kết quả 1.5s
            repository.completeDailyExercise(taskId)
            _navigationEvent.emit(Unit)
        }
    }
}
