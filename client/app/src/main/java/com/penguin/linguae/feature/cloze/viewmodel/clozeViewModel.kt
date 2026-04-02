package com.penguin.linguae.feature.cloze.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClozeViewModel : ViewModel() {
    private val _selectedOptionId = MutableStateFlow<Int?>(null)
    val selectedOptionId = _selectedOptionId.asStateFlow()

    private val _isAnswered = MutableStateFlow(false)
    val isAnswered = _isAnswered.asStateFlow()

    fun onOptionSelected(optionId: Int) {
        if (_isAnswered.value) return  // lock after answering
        _selectedOptionId.value = optionId
        _isAnswered.value = true
    }
}