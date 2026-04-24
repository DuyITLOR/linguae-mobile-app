package com.penguin.linguae.feature.practice.practiceResult.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.data.model.ResultData
import com.penguin.linguae.data.repository.ResultRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ResultViewModel: ViewModel() {
    private val _resultRepository = ResultRepository

    val result = _resultRepository.latestResult

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onRetryClicked() {
        Log.d("RESULT VM", "RETRY CLICKED")
        val currentResult = result.value
        val route = when (currentResult) {
            is ResultData.ClozeResult -> Screen.Cloze.route
            is ResultData.MatchingResult -> Screen.Matching.route
            else -> Screen.Cloze.route
        }
        viewModelScope.launch {
            _navigationEvent.emit(route)
        }
    }
}