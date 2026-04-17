package com.penguin.linguae.data.model

data class DailyMission(
    val id: String,
    val date: String,
    val status: String,
    val tasks: List<DailyTask>
)

data class DailyTask(
    val id: String,
    val taskType: String,
    val targetCount: Int,
    val completedCount: Int,
    val status: String,
    val completedVocabularyIds: List<String>
)

data class StreakSummary(
    val currentStreak: Int,
    val bestStreak: Int,
    val lastLearnedDate: String?
)

data class DailyMissionSummary(
    val hasMission: Boolean,
    val missionStatus: String?,
    val totalTasks: Int,
    val completedTasks: Int,
    val overallProgress: Int,
    val tasks: List<DailyTaskSummary>?,
    val streak: StreakSummary?
)

data class DailyTaskSummary(
    val id: String,
    val taskType: String,
    val completedCount: Int,
    val targetCount: Int,
    val status: String
)

data class TaskWordsResponse(
    val taskId: String,
    val taskType: String,
    val targetCount: Int,
    val completedCount: Int,
    val status: String,
    val words: List<DailyMissionWord>
)

data class DailyMissionWord(
    val id: String,
    val word: String,
    val meaning: String,
    val pronunciationText: String,
    val partOfSpeech: String?,
    val VocabularyExample: List<Example>,
    val isCompleted: Boolean
)

data class CompleteWordRequest(
    val vocabularyId: String
)

data class CompleteWordResponse(
    val alreadyCounted: Boolean,
    val task: DailyTaskUpdated
)

data class DailyTaskUpdated(
    val id: String,
    val completedCount: Int,
    val status: String
)
