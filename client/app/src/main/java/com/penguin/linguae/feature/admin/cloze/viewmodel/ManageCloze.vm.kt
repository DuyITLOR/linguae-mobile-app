package com.penguin.linguae.feature.admin.cloze.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.ClozeQuestion
import com.penguin.linguae.data.model.CreateClozeOptionRequest
import com.penguin.linguae.data.model.CreateClozeQuestionRequest
import com.penguin.linguae.data.repository.ClozeRepository
import kotlinx.coroutines.launch

data class ClozeOptionInput(
    val optionText: String = "",
    val isCorrect: Boolean = false,
    val blankIndex: Int = 1
)

enum class ClozeManageMode { LIST, CREATE, EDIT }

class ManageClozeViewModel(
    val scopedTopicId: String,
    val scopedTopicTitle: String
) : ViewModel() {

    private val clozeRepository = ClozeRepository()

    var questions by mutableStateOf<List<ClozeQuestion>>(emptyList())
    var mode by mutableStateOf(ClozeManageMode.LIST)

    // Form fields
    var sentence by mutableStateOf("")
    val options = mutableStateListOf<ClozeOptionInput>()
    var editingQuestionId by mutableStateOf<Int?>(null)

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var deleteTargetId by mutableStateOf<Int?>(null)

    init {
        loadQuestions()
    }

    fun loadQuestions() {
        viewModelScope.launch {
            isLoading = true
            try {
                questions = clozeRepository.getClozeQuestionWithTopicId(scopedTopicId)
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun startCreate() {
        sentence = ""
        options.clear()
        // Add 4 default options
        repeat(4) { options.add(ClozeOptionInput()) }
        mode = ClozeManageMode.CREATE
    }

    fun startEdit(question: ClozeQuestion) {
        editingQuestionId = question.questionID
        sentence = question.question
        options.clear()
        question.clozeOptions.forEach { opt ->
            options.add(ClozeOptionInput(opt.optionText, opt.isCorrect!!, opt.blankIndex))
        }
        // Ensure at least 4 options for UI consistency if needed, or just use what exists
        while (options.size < 4) {
            options.add(ClozeOptionInput())
        }
        mode = ClozeManageMode.EDIT
    }

    fun cancelForm() {
        mode = ClozeManageMode.LIST
        editingQuestionId = null
    }

    fun updateOption(index: Int, text: String, isCorrect: Boolean) {
        if (index in options.indices) {
            if (isCorrect) {
                // Single-correct option logic
                for (i in options.indices) {
                    val current = options[i]
                    options[i] = if (i == index) {
                        ClozeOptionInput(text, true, 1)
                    } else {
                        current.copy(isCorrect = false)
                    }
                }
            } else {
                options[index] = ClozeOptionInput(text, isCorrect, 1)
            }
        }
    }

    fun submit() {
        if (sentence.isBlank()) { error = "Sentence is required"; return }
        if (options.none { it.isCorrect }) { error = "One option must be correct"; return }
        if (options.any { it.optionText.isBlank() }) { error = "All options must have text"; return }

        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val request = CreateClozeQuestionRequest(
                    topicId = scopedTopicId,
                    sentence = sentence.trim(),
                    options = options.map { opt ->
                        CreateClozeOptionRequest(
                            optionText = opt.optionText.trim(),
                            isCorrect = opt.isCorrect,
                            blankIndex = opt.blankIndex
                        )
                    }
                )

                if (mode == ClozeManageMode.EDIT) {
                    val id = editingQuestionId ?: return@launch
                    clozeRepository.updateClozeQuestion(id, request)
                } else {
                    clozeRepository.createClozeQuestion(request)
                }

                mode = ClozeManageMode.LIST
                editingQuestionId = null
                loadQuestions()
            } catch (e: Exception) {
                error = e.message ?: "Failed to save question"
            } finally {
                isLoading = false
            }
        }
    }

    fun confirmDelete(id: Int) { deleteTargetId = id }
    fun cancelDelete() { deleteTargetId = null }

    fun executeDelete() {
        val id = deleteTargetId ?: return
        deleteTargetId = null
        viewModelScope.launch {
            isLoading = true
            try {
                clozeRepository.deleteClozeQuestion(id)
                loadQuestions()
            } catch (e: Exception) {
                error = e.message ?: "Failed to delete question"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearError() { error = null }
}

class ManageClozeViewModelFactory(
    private val topicId: String,
    private val topicTitle: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ManageClozeViewModel(topicId, topicTitle) as T
    }
}
