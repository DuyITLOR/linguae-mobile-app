package com.penguin.linguae.feature.learning.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.FlashcardTopic
import com.penguin.linguae.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlashcardViewModel : ViewModel(
) {
    // Declare repository
    private val flashcardRepository = FlashcardRepository()

    // Declare state
    private val _topic = MutableStateFlow<List<FlashcardTopic>>(emptyList())

    val topics: StateFlow<List<FlashcardTopic>> = _topic.asStateFlow()

    fun fetchAllTopics() {
        viewModelScope.launch {
            val result = flashcardRepository.getAllFlashcardTopics()
            Log.i("FLASHCARD_VM_TOPICS", result.toString())
            result.onSuccess {
                _topic.value = it
            }.onFailure {
                Log.e("FLASHCARD_VM_TOPICS", "Error: ${it.message}")
            }
        }
    }

    fun refreshTopics() {
        fetchAllTopics()
    }

    fun fetchTopic(topicId: String) {
        viewModelScope.launch {

        }
    }

    init {
        fetchAllTopics()
    }
}