package com.penguin.linguae.feature.auth.viewmodel

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.core.network.TokenManager
import com.penguin.linguae.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel (
    private val repo : AuthRepository = AuthRepository()
) : ViewModel() {
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    private val _navigateHome = MutableStateFlow(false)
    val navigateHome: StateFlow<Boolean> = _navigateHome

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                val token = repo.login(email, password)
                TokenManager.saveToken(token)
                _navigateHome.value = true
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun resetNavigation() {
        _navigateHome.value = false
    }
}