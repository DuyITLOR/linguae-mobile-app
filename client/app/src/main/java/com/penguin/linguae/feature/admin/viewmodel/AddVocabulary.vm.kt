package com.penguin.linguae.feature.admin.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.CreateVocabularyExampleRequest
import com.penguin.linguae.data.model.CreateVocabularyRequest
import com.penguin.linguae.data.model.UpdateVocabularyRequest
import com.penguin.linguae.data.model.Vocabulary
import com.penguin.linguae.data.repository.VocabularyRepository
import kotlinx.coroutines.launch

data class ExampleInput(val sentence: String = "", val translation: String = "")

enum class VocabManageMode { LIST, CREATE, EDIT }

class ManageVocabularyViewModel(
    val scopedTopicId: String,
    val scopedTopicTitle: String
) : ViewModel() {

    private val vocabRepository = VocabularyRepository()

    var vocabularies by mutableStateOf<List<Vocabulary>>(emptyList())
    var mode by mutableStateOf(VocabManageMode.LIST)
    var editingVocab by mutableStateOf<Vocabulary?>(null)

    // Form fields
    var word by mutableStateOf("")
    var meaning by mutableStateOf("")
    var pronunciationText by mutableStateOf("")
    var partOfSpeech by mutableStateOf<String?>(null)
    var difficulty by mutableStateOf(1)
    val examples = mutableStateListOf<ExampleInput>()

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var deleteTargetId by mutableStateOf<String?>(null)

    init {
        loadVocabularies()
    }

    fun loadVocabularies() {
        viewModelScope.launch {
            vocabRepository.getVocabularyByTopic(scopedTopicId, "").onSuccess { vocabularies = it }
        }
    }

    fun startCreate() {
        editingVocab = null
        word = ""
        meaning = ""
        pronunciationText = ""
        partOfSpeech = null
        difficulty = 1
        examples.clear()
        mode = VocabManageMode.CREATE
    }

    fun startEdit(vocab: Vocabulary) {
        editingVocab = vocab
        word = vocab.word
        meaning = vocab.meaning
        pronunciationText = vocab.pronunciationText
        partOfSpeech = vocab.partOfSpeech
        difficulty = vocab.difficulty
        examples.clear()
        examples.addAll(vocab.VocabularyExample.map { ExampleInput(it.sentence, it.translation ?: "") })
        mode = VocabManageMode.EDIT
    }

    fun cancelForm() {
        mode = VocabManageMode.LIST
        editingVocab = null
    }

    fun addExample() { examples.add(ExampleInput()) }

    fun removeExample(index: Int) {
        if (index in examples.indices) examples.removeAt(index)
    }

    fun updateExample(index: Int, sentence: String, translation: String) {
        if (index in examples.indices) examples[index] = ExampleInput(sentence, translation)
    }

    fun submit() {
        if (word.isBlank()) { error = "Word is required"; return }
        if (meaning.isBlank()) { error = "Meaning is required"; return }

        viewModelScope.launch {
            isLoading = true
            error = null

            val validExamples = examples
                .filter { it.sentence.isNotBlank() }
                .map { CreateVocabularyExampleRequest(it.sentence.trim(), it.translation.trim().ifBlank { null }) }

            if (mode == VocabManageMode.CREATE) {
                val request = CreateVocabularyRequest(
                    topicId = scopedTopicId,
                    word = word.trim(),
                    meaning = meaning.trim(),
                    pronunciationText = pronunciationText.trim().ifBlank { null },
                    partOfSpeech = partOfSpeech,
                    difficulty = difficulty,
                    examples = validExamples
                )
                vocabRepository.createVocabulary(request)
                    .onSuccess { mode = VocabManageMode.LIST; loadVocabularies() }
                    .onFailure { error = it.message ?: "Failed to create vocabulary" }
            } else {
                val vocabId = editingVocab?.id ?: return@launch
                val request = UpdateVocabularyRequest(
                    topicId = scopedTopicId,
                    word = word.trim(),
                    meaning = meaning.trim(),
                    pronunciationText = pronunciationText.trim().ifBlank { null },
                    partOfSpeech = partOfSpeech,
                    difficulty = difficulty,
                    examples = validExamples
                )
                vocabRepository.updateVocabulary(vocabId, request)
                    .onSuccess { mode = VocabManageMode.LIST; loadVocabularies() }
                    .onFailure { error = it.message ?: "Failed to update vocabulary" }
            }

            isLoading = false
        }
    }

    fun confirmDelete(vocabId: String) { deleteTargetId = vocabId }

    fun cancelDelete() { deleteTargetId = null }

    fun executeDelete() {
        val id = deleteTargetId ?: return
        viewModelScope.launch {
            isLoading = true
            vocabRepository.deleteVocabulary(id)
                .onSuccess { loadVocabularies() }
                .onFailure { error = it.message ?: "Failed to delete vocabulary" }
            isLoading = false
            deleteTargetId = null
        }
    }

    fun clearError() { error = null }
}

class ManageVocabularyViewModelFactory(
    private val topicId: String,
    private val topicTitle: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ManageVocabularyViewModel(topicId, topicTitle) as T
    }
}
