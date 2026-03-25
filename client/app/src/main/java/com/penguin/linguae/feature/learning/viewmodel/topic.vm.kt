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

class TopicViewModel: ViewModel() {

    private val repository = TopicRepository()

    private val _topic = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topic.asStateFlow()

    init {
        fetchTopic()
    }
    fun fetchTopic() {
        viewModelScope.launch {
            val result = repository.getAllTopic()
            Log.i("API_TEST", result.toString())
            result.onSuccess {
                _topic.value = it
            }.onFailure {
                Log.e("TopicVM", "Error: ${it.message}")
            }
        }
    }
}