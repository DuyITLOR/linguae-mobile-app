package com.penguin.linguae.feature.practice.topicPracticeConfig.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.TopicPracticeConfig
import com.penguin.linguae.data.repository.TopicPracticeConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TopicPracticeConfigViewModel(
    private val repository: TopicPracticeConfigRepository = TopicPracticeConfigRepository()
) : ViewModel() {

    private val _practiceConfigs = MutableStateFlow<List<TopicPracticeConfig>>(emptyList())
    val practiceConfigs: StateFlow<List<TopicPracticeConfig>> = _practiceConfigs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchPracticeConfigs(topicId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getPracticeConfigByTopicId(topicId)
                .onSuccess {
                    _practiceConfigs.value = it
                }
                .onFailure {
                    _error.value = it.message ?: "Unknown error"
                }
            _isLoading.value = false
        }
    }
}
