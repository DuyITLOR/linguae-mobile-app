// viewmodel/VocabularyViewModel.kt
package com.penguin.linguae.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.VocabularyRepository
import com.penguin.linguae.model.Vocabulary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class VocabularyViewModel(
    private val repository: VocabularyRepository = VocabularyRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<Vocabulary>>(emptyList())

    val uiState: StateFlow<List<Vocabulary>> = _uiState.asStateFlow()

    init {
        fetchVocabulary()
    }

    private fun fetchVocabulary() {
        viewModelScope.launch {
            repository.getVocabularyList()
                .catch { exception ->

                    exception.printStackTrace()
                }
                .collect { vocabList ->

                    _uiState.value = vocabList
                }
        }
    }
}