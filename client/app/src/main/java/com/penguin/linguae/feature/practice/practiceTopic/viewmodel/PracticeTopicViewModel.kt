package com.penguin.linguae.feature.practice.practiceTopic.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.data.repository.TopicRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart

class PracticeTopicViewModel (
    val _topicRepository: TopicRepository = TopicRepository()
): ViewModel() {

    private val _topic = MutableStateFlow<List<Topic>?>(null)
    val topic = _topic.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _query = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _query
                .onStart { emit("") }
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { name ->
                    getTopicByName(name)
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            getTopicByName(_query.value)
        }
    }

    suspend fun getTopicByName(name: String = "") {
        _isLoading.value = true
        _error.value = null
        val result = _topicRepository.getAllTopic(name)

        result
            .onSuccess { data ->
                _topic.value = data.toList()
                Log.d("PRACTICEVM", "FETCHED TOPICS: ${_topic.value}")
            }
            .onFailure { e ->
                _error.value = e.message ?: "Failed to fetch topics"
                Log.d("PRACTICEVM", "ERROR FETCHING TOPIC: $e")
            }
        _isLoading.value = false
    }

    var onTopicClicked: (Topic?) -> Unit = {}

    fun onQueryChange(name: String) {
        _query.value = name
    }
}