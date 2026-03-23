// viewmodel/VocabularyViewModel.kt
package com.penguin.linguae.feature.learning.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.repository.VocabularyRepository
import com.penguin.linguae.data.model.Column
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VocabularyViewModel: ViewModel() {

    private val repository = VocabularyRepository()

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