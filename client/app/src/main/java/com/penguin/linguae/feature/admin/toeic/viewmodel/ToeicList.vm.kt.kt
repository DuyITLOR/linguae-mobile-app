package com.penguin.linguae.feature.admin.toeic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.Toeic
import com.penguin.linguae.data.repository.ToeicRepository
import kotlinx.coroutines.launch

class ToeicListViewModel : ViewModel() {
    private val repository = ToeicRepository()

    var toeicTests by mutableStateOf<List<Toeic>>(emptyList())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        loadToeicTests()
    }

    fun loadToeicTests() {
        viewModelScope.launch {
            isLoading = true
            error = null
            repository.getAllToeic()
                .onSuccess { toeicTests = it }
                .onFailure { error = it.message ?: "Failed to load tests" }
            isLoading = false
        }
    }

    fun retry() {
        loadToeicTests()
    }
}
