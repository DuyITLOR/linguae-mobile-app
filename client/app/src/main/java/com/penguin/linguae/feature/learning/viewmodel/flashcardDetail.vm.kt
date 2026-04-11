package com.penguin.linguae.feature.learning.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.TopicIncludeVocab
import com.penguin.linguae.data.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlashcardDetailViewModel : ViewModel() {

    private val repository = TopicRepository()

    private val _topic = MutableStateFlow<TopicIncludeVocab?>(null)
    val topic = _topic.asStateFlow()

    fun init(topicId: String) {
        fetchTopic(topicId)
    }

    private fun fetchTopic(topicId: String) {
        viewModelScope.launch {
            val result = repository.getTopicById(topicId)
            result.onSuccess {
                _topic.value = it
                Log.i("FLASHCARDDETAIL_VM_TOPICS", "data = ${_topic.value}")
            }.onFailure {
                Log.e("FLASHCARDDETAIL_VM_TOPICS", "Error: ${it.message}")
            }
        }
    }
}