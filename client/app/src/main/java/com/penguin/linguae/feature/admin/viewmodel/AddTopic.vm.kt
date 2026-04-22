package com.penguin.linguae.feature.admin.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.CreateTopicRequest
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.data.model.UpdateTopicRequest
import com.penguin.linguae.data.repository.TopicRepository
import kotlinx.coroutines.launch

enum class TopicManageMode { LIST, CREATE, EDIT }

class ManageTopicViewModel : ViewModel() {

    private val repository = TopicRepository()

    var topics by mutableStateOf<List<Topic>>(emptyList())
    var mode by mutableStateOf(TopicManageMode.LIST)
    var editingTopic by mutableStateOf<Topic?>(null)

    // Form fields
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var icon by mutableStateOf("")
    var level by mutableStateOf("BEGINNER")

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var deleteTarget by mutableStateOf<Topic?>(null)

    init {
        loadTopics()
    }

    fun loadTopics() {
        viewModelScope.launch {
            repository.getAllTopic("").onSuccess { topics = it }
        }
    }

    fun startCreate() {
        editingTopic = null
        title = ""
        description = ""
        icon = ""
        level = "BEGINNER"
        mode = TopicManageMode.CREATE
    }

    fun startEdit(topic: Topic) {
        editingTopic = topic
        title = topic.title
        description = topic.description ?: ""
        icon = topic.icon ?: ""
        level = topic.level
        mode = TopicManageMode.EDIT
    }

    fun cancelForm() {
        mode = TopicManageMode.LIST
        editingTopic = null
    }

    fun submit() {
        if (title.isBlank()) {
            error = "Title is required"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            if (mode == TopicManageMode.CREATE) {
                val request = CreateTopicRequest(
                    title = title.trim(),
                    description = description.trim().ifBlank { null },
                    icon = icon.trim().ifBlank { null },
                    level = level,
                    displayOrder = 0
                )
                repository.createTopic(request)
                    .onSuccess { mode = TopicManageMode.LIST; loadTopics() }
                    .onFailure { error = it.message ?: "Failed to create topic" }
            } else {
                val topicId = editingTopic?.id ?: return@launch
                val request = UpdateTopicRequest(
                    title = title.trim(),
                    description = description.trim().ifBlank { null },
                    icon = icon.trim().ifBlank { null },
                    level = level
                )
                repository.updateTopic(topicId, request)
                    .onSuccess { mode = TopicManageMode.LIST; loadTopics() }
                    .onFailure { error = it.message ?: "Failed to update topic" }
            }

            isLoading = false
        }
    }

    fun confirmDelete(topic: Topic) {
        deleteTarget = topic
    }

    fun cancelDelete() {
        deleteTarget = null
    }

    fun executeDelete() {
        val id = deleteTarget?.id ?: return
        viewModelScope.launch {
            isLoading = true
            repository.deleteTopic(id)
                .onSuccess { loadTopics() }
                .onFailure { error = it.message ?: "Failed to delete topic" }
            isLoading = false
            deleteTarget = null
        }
    }

    fun clearError() {
        error = null
    }
}
