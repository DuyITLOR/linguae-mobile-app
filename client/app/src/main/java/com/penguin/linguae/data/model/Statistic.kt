package com.penguin.linguae.data.model

data class StatisticsResponse(
    val streak: StreakStats,
    val totalVocabularyLearned: Int,
    val todayProgress: TodayProgressStats,
    val weeklyActivity: List<DailyActivityStats>,
    val missionHistory: List<MissionHistoryStat>
)

data class StreakStats(
    val currentStreak: Int,
    val bestStreak: Int
)

data class TodayProgressStats(
    val overallProgress: Int,
    val completedTasks: Int,
    val totalTasks: Int,
    val tasks: List<TaskProgressItem>?
)

data class TaskProgressItem(
    val id: String,
    val taskType: String,
    val completedCount: Int,
    val targetCount: Int,
    val status: String
)

data class DailyActivityStats(
    val date: String,
    val completedTasks: Int,
    val totalTasks: Int
)

data class MissionHistoryStat(
    val date: String,
    val status: String,
    val completedTasks: Int,
    val totalTasks: Int
)
