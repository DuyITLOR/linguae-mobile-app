package com.penguin.linguae.feature.admin.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.CreateVocabularyExampleRequest
import com.penguin.linguae.data.model.CreateVocabularyRequest
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.data.repository.TopicRepository
import com.penguin.linguae.data.repository.VocabularyRepository
import kotlinx.coroutines.launch

data class ExampleInput(val sentence: String = "", val translation: String = "")

class AddVocabularyViewModel : ViewModel() {

    private val vocabRepository = VocabularyRepository()
    private val topicRepository = TopicRepository()

    var topics by mutableStateOf<List<Topic>>(emptyList())
    var selectedTopic by mutableStateOf<Topic?>(null)
    var word by mutableStateOf("")
    var meaning by mutableStateOf("")
    var pronunciationText by mutableStateOf("")
    var partOfSpeech by mutableStateOf<String?>(null)
    var difficulty by mutableStateOf(1)
    val examples = mutableStateListOf<ExampleInput>()
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var success by mutableStateOf(false)

    init {
        loadTopics()
    }

    private fun loadTopics() {
        viewModelScope.launch {
            topicRepository.getAllTopic("").onSuccess { topics = it }
        }
    }

    fun addExample() {
        examples.add(ExampleInput())
    }

    fun removeExample(index: Int) {
        if (index in examples.indices) examples.removeAt(index)
    }

    fun updateExample(index: Int, sentence: String, translation: String) {
        if (index in examples.indices) examples[index] = ExampleInput(sentence, translation)
    }

    fun submit() {
        if (selectedTopic == null) { error = "Please select a topic"; return }
        if (word.isBlank()) { error = "Word is required"; return }
        if (meaning.isBlank()) { error = "Meaning is required"; return }

        viewModelScope.launch {
            isLoading = true
            error = null

            val request = CreateVocabularyRequest(
                topicId = selectedTopic!!.id,
                word = word.trim(),
                meaning = meaning.trim(),
                pronunciationText = pronunciationText.trim().ifBlank { null },
                partOfSpeech = partOfSpeech,
                difficulty = difficulty,
                examples = examples
                    .filter { it.sentence.isNotBlank() }
                    .map { CreateVocabularyExampleRequest(it.sentence.trim(), it.translation.trim().ifBlank { null }) }
            )

            vocabRepository.createVocabulary(request)
                .onSuccess { success = true }
                .onFailure { error = it.message ?: "Failed to create vocabulary" }

            isLoading = false
        }
    }

    fun clearError() {
        error = null
    }
}
