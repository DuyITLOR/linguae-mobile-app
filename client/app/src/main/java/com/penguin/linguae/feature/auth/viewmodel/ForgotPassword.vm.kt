package com.penguin.linguae.feature.auth.viewmodel

import retrofit2.HttpException
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject


class ForgotPasswordViewModel (
    private val repo : AuthRepository = AuthRepository()
) : ViewModel() {
    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)

    private val _navigateLogin = MutableStateFlow(false)
    val navigateLogin : StateFlow<Boolean> = _navigateLogin

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            try {
                repo.forgotPassword(email)
                _navigateLogin.value = true
            } catch(e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                error.value = try {
                    JSONObject(errorBody).getString("message")
                } catch (ex: Exception) {
                    "Lỗi quên mật khẩu, vui lòng thử lại"
                }
            } catch (e: Exception) {
                error.value = e.message ?: "Đã có lỗi xảy ra"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun resetNavigation() {
        _navigateLogin.value = false
    }
}