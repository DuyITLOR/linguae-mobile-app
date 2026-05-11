// viewmodel/VocabularyViewModel.kt
package com.penguin.linguae.feature.learning.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.core.DailyMissionCache
import com.penguin.linguae.data.model.FlashcardReviewStatus
import com.penguin.linguae.data.model.Vocabulary
import com.penguin.linguae.data.repository.DailyMissionRepository
import com.penguin.linguae.data.repository.FlashcardRepository
import com.penguin.linguae.data.repository.VocabularyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VocabularyViewModel: ViewModel() {

    private val repository = VocabularyRepository()
    private val dailyMissionRepository = DailyMissionRepository()
    private val flashcardRepository = FlashcardRepository()
    private val _vocabulary = MutableStateFlow<List<Vocabulary>>(emptyList())
    val vocabulary: StateFlow<List<Vocabulary>> = _vocabulary.asStateFlow()

    private val _querySearch = MutableStateFlow("")
    val querySearch = _querySearch.asStateFlow()

    private var searchJob: Job? = null

    private val _selectedVocabulary = MutableStateFlow<Vocabulary?>(null)
    val selectedVocabulary = _selectedVocabulary.asStateFlow()

    fun fetchVocabularyByTopic(topicId: String, query: String = "") {
        viewModelScope.launch {
            val result = repository.getVocabularyByTopic(topicId, query)
            Log.i("API_TEST_VOCA_TOPIC", result.toString())
            result.onSuccess {
                _vocabulary.value = it
            }.onFailure {
                Log.e("ColumnVM", "Error: ${it.message}")
            }
        }
    }

    fun onSearchQueryChange(query: String, topicId: String) {
        _querySearch.value = query

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(500)

            fetchVocabularyByTopic(topicId, query)
        }
    }

    fun fetchVocabularyById(id: String) {
        viewModelScope.launch {
            val result = repository.getVocabularyById(id)
            Log.i("API_TEST_VOCA_ID", result.toString())
            result.onSuccess {
                _selectedVocabulary.value = it
                launch {
                    flashcardRepository.reviewFlashcard(id, FlashcardReviewStatus.MASTERED)
                }
                DailyMissionCache.getTaskIdForVocab(id, "VOCABULARY_LEARN")?.let { taskId ->
                    launch {
                        dailyMissionRepository.completeWord(taskId, id)
                        DailyMissionCache.markWordCompleted(taskId, id)
                    }
                }
            }.onFailure {
                Log.e("Vocabulary by id", "Error: ${it.message}")
            }
        }
    }
}