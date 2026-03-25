package com.penguin.linguae.feature.auth.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.penguin.linguae.core.network.TokenManager
import com.penguin.linguae.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val message = try {
                    val json = Gson().fromJson(errorBody, Map::class.java)
                    json["message"]?.toString()
                } catch (ex: Exception) {
                    "Lỗi đăng nhập, vui lòng thử lại"
                }
                error = message
            } catch (e: Exception) {
                error = "Không thể kết nối đến máy chủ"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetNavigation() {
        _navigateHome.value = false
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                val token = repo.loginWithGoogle(idToken)
                TokenManager.saveToken(token)
                _navigateHome.value = true
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()

                val message = try {
                    val json = Gson().fromJson(errorBody, Map::class.java)
                    json["message"]?.toString()
                } catch (ex: Exception) {
                    "Lỗi đăng nhập, vui lòng thử lại"
                }
                error = message
            } catch (e: Exception) {
                error = "Không thể kết nối đến máy chủ"
            } finally {
                isLoading = false
            }
        }
    }
}