package com.penguin.linguae.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.core.network.UserManager
import com.penguin.linguae.data.model.Destination
import com.penguin.linguae.data.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class HomeViewModel(): ViewModel() {
    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent: SharedFlow<String> = _navigationEvent.asSharedFlow()

    fun onWordCardClick(){
        viewModelScope.launch {
//            Đợi có route chính thức
//            _navigationEvent.emit("")
        }
    }

    fun onFlashCardClick(){
        viewModelScope.launch {
//            Đợi có route chính thức
//            _navigationEvent.emit("")
        }
    }

    fun onPracticeCardClick(){
        viewModelScope.launch {
//            Đợi có route chính thức
//            _navigationEvent.emit("")
        }
    }

    fun onStatisticCardClick(){
        viewModelScope.launch {
//            Đợi có route chính thức
//            _navigationEvent.emit("")
        }
    }
}

data class HomeState (
    val user: User? = UserManager.getUser(),
    val streak: Int = 0,
    val wordLearned: Int = 0,
    val todayProgress: Float = 0f,
) {
    init {
        require(todayProgress in 0f..1f) { "todayProgress phải trong 0-1" }
    }
}