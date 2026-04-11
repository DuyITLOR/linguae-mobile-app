package com.penguin.linguae.feature.learning.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.data.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlashcardViewModel : ViewModel(
) {
    // Declare repository
    private val topicRepository = TopicRepository()

    // Declare state
    private val _topic = MutableStateFlow<List<Topic>>(emptyList())

    val topics: StateFlow<List<Topic>> = _topic.asStateFlow()

    fun fetchAllTopics() {
        viewModelScope.launch {
            val result = topicRepository.getAllTopic("")
            Log.i("FLASHCARD_VM_TOPICS", result.toString())
            result.onSuccess {
                _topic.value = it.filter { topic -> topic.isActive }
            }.onFailure {
                Log.e("FLASHCARD_VM_TOPICS", "Error: ${it.message}")
            }
        }
    }

    fun fetchTopic(topicId: String) {
        viewModelScope.launch {

        }
    }

    init {
        fetchAllTopics()
    }
}