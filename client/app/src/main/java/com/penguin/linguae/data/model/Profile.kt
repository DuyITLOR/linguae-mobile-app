package com.penguin.linguae.data.model

data class ProfileStats(
    val learnedWords: Int = 342,
    val streakDays: Int = 7,
    val accuracyPercent: Int = 85,
)

data class ProfileUiState(
    val fullName: String,
    val email: String,
    val avatarEmoji: String = "\uD83D\uDC27",
    val avatarUrl: String? = null,
    val dailyGoal: Int = 12,
    val reminderTime: String = "20:00",
    val reminderEnabled: Boolean = true,
    val stats: ProfileStats = ProfileStats(),
)

object ProfilePreviewData {
    val sample = ProfileUiState(
        fullName = "Quách Châu Hạo Kiệt",
        email = "23127078@student.hcmus.edu.vn",
    )
}
