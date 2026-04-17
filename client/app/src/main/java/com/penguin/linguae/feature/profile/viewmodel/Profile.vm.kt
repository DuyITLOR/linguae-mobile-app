package com.penguin.linguae.feature.profile.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.penguin.linguae.core.network.UserManager
import com.penguin.linguae.data.repository.ProfileRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository(),
) : ViewModel() {

    var isSaving by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var saveCompleted by mutableStateOf(false)
        private set

    fun updateProfile(
        context: Context,
        fullName: String,
        avatarUri: Uri?,
    ) {
        if (fullName.trim().isEmpty()) {
            errorMessage = "Tên hiển thị không được để trống"
            return
        }

        viewModelScope.launch {
            isSaving = true
            errorMessage = null

            try {
                val updatedUser = repository.updateMyProfile(
                    context = context,
                    fullName = fullName,
                    avatarUri = avatarUri,
                )
                UserManager.saveUser(updatedUser)
                saveCompleted = true
            } catch (exception: HttpException) {
                errorMessage = parseHttpErrorMessage(exception)
            } catch (exception: Exception) {
                errorMessage = exception.message ?: "Không thể cập nhật hồ sơ"
            } finally {
                isSaving = false
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }

    fun consumeSaveCompleted() {
        saveCompleted = false
    }

    private fun parseHttpErrorMessage(exception: HttpException): String {
        val errorBody = exception.response()?.errorBody()?.string()

        return try {
            val json = Gson().fromJson(errorBody, Map::class.java)
            json["message"]?.toString() ?: "Không thể cập nhật hồ sơ"
        } catch (_: Exception) {
            "Không thể cập nhật hồ sơ"
        }
    }
}
