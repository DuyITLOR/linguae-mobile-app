package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.NotificationSettingsResponse
import com.penguin.linguae.data.model.UpdateNotificationSettingsRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface NotificationApi {
    @GET("notifications/settings")
    suspend fun getSettings(): NotificationSettingsResponse

    @PATCH("notifications/settings")
    suspend fun updateSettings(
        @Body request: UpdateNotificationSettingsRequest,
    ): NotificationSettingsResponse
}
