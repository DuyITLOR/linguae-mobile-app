package com.penguin.linguae.feature.learning.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.data.repository.TopicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TopicViewModel: ViewModel() {

    private val repository = TopicRepository()
    private val _topic = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topic.asStateFlow()

    private val _querySearch = MutableStateFlow("")
    val querySearch = _querySearch.asStateFlow()

    var searchJob: Job? = null

    init {
        fetchTopic()
    }
    fun fetchTopic(query: String = "") {
        viewModelScope.launch {
            val result = repository.getAllTopic(query)
            Log.i("API_TEST", result.toString())
            result.onSuccess {
                _topic.value = it
            }.onFailure {
                Log.e("TopicVM", "Error: ${it.message}")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _querySearch.value = query
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(500)
            fetchTopic(query)
        }
    }
}