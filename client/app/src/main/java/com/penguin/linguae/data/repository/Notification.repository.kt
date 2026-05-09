package com.penguin.linguae.data.repository

import android.content.Context
import com.penguin.linguae.core.notification.LearningReminderScheduler
import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.NotificationSettingsData
import com.penguin.linguae.data.model.UpdateNotificationSettingsRequest
import com.penguin.linguae.data.remote.NotificationApi

class NotificationRepository {
    private val api = RetrofitClient.create(NotificationApi::class.java)

    suspend fun getRemoteSettings(): NotificationSettingsData {
        val response = api.getSettings()
        if (!response.success) {
            throw IllegalStateException(response.message)
        }
        return response.data
    }

    suspend fun updateRemoteSettings(request: UpdateNotificationSettingsRequest): NotificationSettingsData {
        val response = api.updateSettings(request)
        if (!response.success) {
            throw IllegalStateException(response.message)
        }
        return response.data
    }

    fun getLocalSettings(context: Context): NotificationSettingsData {
        return LearningReminderScheduler.getSavedSettings(context)
    }

    fun saveLocalSettings(context: Context, settings: NotificationSettingsData) {
        LearningReminderScheduler.saveSettings(context, settings)
        LearningReminderScheduler.applySchedule(context, settings)
    }
}
