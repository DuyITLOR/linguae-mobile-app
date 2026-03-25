// viewmodel/VocabularyViewModel.kt
package com.penguin.linguae.feature.learning.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.Vocabulary
import com.penguin.linguae.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VocabularyViewModel: ViewModel() {

    private val repository = VocabularyRepository()

    private val _vocabulary = MutableStateFlow<List<Vocabulary>>(emptyList())
    val vocabulary: StateFlow<List<Vocabulary>> = _vocabulary.asStateFlow()

    fun fetchVocabularyByTopic(topicId: String) {
        viewModelScope.launch {
            val result = repository.getVocabularyByTopic(topicId)
            Log.i("API_TEST_VOCA", result.toString())
            result.onSuccess {
                _vocabulary.value = it
            }.onFailure {
                Log.e("ColumnVM", "Error: ${it.message}")
            }
        }
    }
}