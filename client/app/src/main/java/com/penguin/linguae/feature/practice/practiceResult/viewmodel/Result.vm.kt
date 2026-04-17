package com.penguin.linguae.feature.practice.practiceResult.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.data.repository.ResultRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ResultViewModel: ViewModel() {
    private val _resultRepository = ResultRepository

    val result = _resultRepository.latestResult

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onRetryClicked(){
        Log.d("RESULT VM", "RETRY CLICKED")
        viewModelScope.launch {
            _navigationEvent.emit(Screen.Cloze.route)
        }
    }
}