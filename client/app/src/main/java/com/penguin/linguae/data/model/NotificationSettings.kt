package com.penguin.linguae.data.model

data class NotificationSettingsResponse(
    val success: Boolean,
    val message: String,
    val data: NotificationSettingsData,
)

data class NotificationSettingsData(
    val notificationsEnabled: Boolean = true,
    val pushNotifications: Boolean = true,
    val emailNotifications: Boolean = false,
    val reminderTime: String = "20:00",
    val timezone: String = "Asia/Ho_Chi_Minh",
    val dailyReminder: DailyReminderData = DailyReminderData(),
)

data class DailyReminderData(
    val id: String? = null,
    val enabled: Boolean = true,
    val title: String = "Đến giờ luyện tập rồi!",
    val message: String? = "Vào Linguae làm nhiệm vụ hằng ngày nhé.",
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
)

data class UpdateNotificationSettingsRequest(
    val notificationsEnabled: Boolean,
    val pushNotifications: Boolean,
    val reminderTime: String,
    val timezone: String,
    val dailyReminderEnabled: Boolean,
    val reminderTitle: String,
    val reminderMessage: String?,
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
)
