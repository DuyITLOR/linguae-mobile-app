package com.penguin.linguae.feature.profile.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.penguin.linguae.data.model.DailyReminderData
import com.penguin.linguae.data.model.NotificationSettingsData
import com.penguin.linguae.data.model.UpdateNotificationSettingsRequest
import com.penguin.linguae.data.repository.NotificationRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class NotificationSettingsViewModel(
    private val repository: NotificationRepository = NotificationRepository(),
) : ViewModel() {
    var enabled by mutableStateOf(true)
        private set
    var reminderTime by mutableStateOf("20:00")
        private set
    var title by mutableStateOf("Đến giờ luyện tập rồi!")
        private set
    var message by mutableStateOf("Vào Linguae làm nhiệm vụ hằng ngày nhé.")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isSaving by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    fun load(context: Context) {
        val local = repository.getLocalSettings(context)
        applySettings(local)

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val remote = repository.getRemoteSettings()
                applySettings(remote)
                repository.saveLocalSettings(context, remote)
            } catch (_: Exception) {
                // Local settings still keep reminders working while backend is offline.
            } finally {
                isLoading = false
            }
        }
    }

    fun updateEnabled(value: Boolean) {
        enabled = value
    }

    fun updateReminderTime(value: String) {
        reminderTime = value
    }

    fun updateTitle(value: String) {
        title = value
    }

    fun updateMessage(value: String) {
        message = value
    }

    fun save(context: Context) {
        val normalizedTime = normalizeTime(reminderTime)
        if (normalizedTime == null) {
            errorMessage = "Giờ nhắc phải có dạng HH:mm, ví dụ 20:00"
            return
        }

        val normalizedTitle = title.trim()
        if (normalizedTitle.isEmpty()) {
            errorMessage = "Tiêu đề thông báo không được để trống"
            return
        }

        val localSettings = NotificationSettingsData(
            notificationsEnabled = enabled,
            pushNotifications = enabled,
            reminderTime = normalizedTime,
            timezone = "Asia/Ho_Chi_Minh",
            dailyReminder = DailyReminderData(
                enabled = enabled,
                title = normalizedTitle,
                message = message.trim().ifEmpty { null },
            )
        )

        repository.saveLocalSettings(context, localSettings)
        applySettings(localSettings)

        viewModelScope.launch {
            isSaving = true
            errorMessage = null
            successMessage = null
            try {
                val remote = repository.updateRemoteSettings(
                    UpdateNotificationSettingsRequest(
                        notificationsEnabled = enabled,
                        pushNotifications = enabled,
                        reminderTime = normalizedTime,
                        timezone = "Asia/Ho_Chi_Minh",
                        dailyReminderEnabled = enabled,
                        reminderTitle = normalizedTitle,
                        reminderMessage = message.trim().ifEmpty { null },
                    )
                )
                applySettings(remote)
                repository.saveLocalSettings(context, remote)
                successMessage = "Đã lưu cài đặt thông báo"
            } catch (exception: HttpException) {
                errorMessage = parseHttpErrorMessage(exception)
            } catch (_: Exception) {
                successMessage = "Đã lưu trên thiết bị. Sẽ đồng bộ khi máy chủ sẵn sàng."
            } finally {
                isSaving = false
            }
        }
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }

    private fun applySettings(settings: NotificationSettingsData) {
        enabled = settings.notificationsEnabled &&
            settings.pushNotifications &&
            settings.dailyReminder.enabled
        reminderTime = settings.reminderTime
        title = settings.dailyReminder.title
        message = settings.dailyReminder.message.orEmpty()
    }

    private fun normalizeTime(value: String): String? {
        val match = Regex("""^(\d{1,2}):(\d{2})$""").matchEntire(value.trim()) ?: return null
        val hour = match.groupValues[1].toIntOrNull() ?: return null
        val minute = match.groupValues[2].toIntOrNull() ?: return null

        if (hour !in 0..23 || minute !in 0..59) return null
        return "%02d:%02d".format(hour, minute)
    }

    private fun parseHttpErrorMessage(exception: HttpException): String {
        val errorBody = exception.response()?.errorBody()?.string()

        return try {
            val json = Gson().fromJson(errorBody, Map::class.java)
            json["message"]?.toString() ?: "Không thể đồng bộ cài đặt"
        } catch (_: Exception) {
            "Không thể đồng bộ cài đặt"
        }
    }
}
