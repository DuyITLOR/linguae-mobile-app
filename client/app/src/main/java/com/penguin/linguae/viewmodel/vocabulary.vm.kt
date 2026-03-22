// viewmodel/VocabularyViewModel.kt
package com.penguin.linguae.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.network.RetrofitClient
import com.penguin.linguae.data.repository.VocabularyRepository
import com.penguin.linguae.model.Column
import com.penguin.linguae.model.Vocabulary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VocabularyViewModel: ViewModel() {

    private val repository = VocabularyRepository(
        RetrofitClient.vocabularyApi
    )

    private val _vocabulary = MutableStateFlow<List<Column>>(emptyList())
    val vocabulary: StateFlow<List<Column>> = _vocabulary.asStateFlow()

    init {
        fetchVocabulary()
    }
    fun fetchVocabulary() {
        viewModelScope.launch {
            val result = repository.getAllColumn()
            result.onSuccess {
                _vocabulary.value = it
            }.onFailure {
                Log.e("ColumnVM", "Error: ${it.message}")
            }
        }
    }
}