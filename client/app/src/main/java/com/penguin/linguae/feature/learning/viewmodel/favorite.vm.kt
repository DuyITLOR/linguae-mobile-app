package com.penguin.linguae.feature.learning.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.Favorite
import com.penguin.linguae.data.repository.FavoriteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel: ViewModel() {

    private val repository = FavoriteRepository()
    private val _favorite = MutableStateFlow<Favorite?>(null)
    val topics: StateFlow<Favorite?> = _favorite.asStateFlow()

    var searchJob: Job? = null

    init {
        fetchTopic()
    }
    fun fetchTopic(query: String = "") {
        viewModelScope.launch {
            val result = repository.getFavoriteByUserId()
            Log.i("API_TEST", result.toString())
            result.onSuccess {
                _favorite.value = it
            }.onFailure {
                Log.e("TopicVM", "Error: ${it.message}")
            }
        }
    }

}