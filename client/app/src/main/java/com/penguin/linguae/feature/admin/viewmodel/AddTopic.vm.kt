package com.penguin.linguae.feature.admin.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.CreateTopicRequest
import com.penguin.linguae.data.repository.TopicRepository
import kotlinx.coroutines.launch

class AddTopicViewModel : ViewModel() {

    private val repository = TopicRepository()

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var icon by mutableStateOf("")
    var level by mutableStateOf("BEGINNER")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var success by mutableStateOf(false)

    fun submit() {
        if (title.isBlank()) {
            error = "Title is required"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            val request = CreateTopicRequest(
                title = title.trim(),
                description = description.trim().ifBlank { null },
                icon = icon.trim().ifBlank { null },
                level = level,
                displayOrder = 0
            )

            repository.createTopic(request)
                .onSuccess { success = true }
                .onFailure { error = it.message ?: "Failed to create topic" }

            isLoading = false
        }
    }

    fun clearError() {
        error = null
    }
}
