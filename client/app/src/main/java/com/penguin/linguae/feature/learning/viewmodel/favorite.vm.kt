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
    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    var searchJob: Job? = null

    init {
        fetchFavorite()
    }
    fun fetchFavorite(query: String = "") {
        viewModelScope.launch {
            val result = repository.getFavoriteByUserId()
            Log.i("API_TEST_FAVORITE", result.toString())
            result.onSuccess {_favorites.value = it
                _favoriteIds.value = it.map { fav -> fav.vocabularyId }.toSet()
                _favorites.value = it
            }.onFailure {
                Log.e("TopicVM", "Error: ${it.message}")
            }
        }
    }

    fun addFavorite(vocabId: String) {
        val cur = _favoriteIds.value

        _favoriteIds.value = cur + vocabId

        viewModelScope.launch {
            val result = repository.createFavorite(vocabId)

            result.onSuccess { newFavoriteData ->
                Log.i("FavoriteVM", "Successfully added vocab: $vocabId")
                fetchFavorite()
            }.onFailure {
                _favoriteIds.value = cur
                Log.e("FavoriteVM", "Error adding favorite: ${it.message}")
            }
        }
    }

    fun removeFavorite(vocabId: String) {
        val cur = _favoriteIds.value
        _favoriteIds.value = cur - vocabId

        viewModelScope.launch {
            val result = repository.removeFavorite(vocabId)

            result.onSuccess {
                Log.i("FavoriteVM", "Successfully removed vocab: $vocabId")
                fetchFavorite()
            }.onFailure {
                _favoriteIds.value = cur
                Log.e("FavoriteVM", "Error removing favorite: ${it.message}")
            }
        }
    }

}