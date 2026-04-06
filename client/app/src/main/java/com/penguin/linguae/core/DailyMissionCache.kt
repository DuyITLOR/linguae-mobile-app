package com.penguin.linguae.core

import com.penguin.linguae.data.model.TaskWordsResponse

object DailyMissionCache {
    private val vocabToTaskMap = mutableMapOf<String, MutableMap<String, String>>()
    private val taskWordsMap = mutableMapOf<String, TaskWordsResponse>()

    fun setTaskData(taskId: String, response: TaskWordsResponse) {
        taskWordsMap[taskId] = response
        response.words.forEach { word ->
            vocabToTaskMap.getOrPut(word.id) { mutableMapOf() }[response.taskType] = taskId
        }
    }

    fun getTaskIdForVocab(vocabId: String, taskType: String): String? =
        vocabToTaskMap[vocabId]?.get(taskType)

    fun getTaskWords(taskId: String): TaskWordsResponse? = taskWordsMap[taskId]

    fun markWordCompleted(taskId: String, vocabId: String): Boolean {
        val response = taskWordsMap[taskId] ?: return false
        val word = response.words.find { it.id == vocabId } ?: return false
        if (word.isCompleted) return false

        val updatedWords = response.words.map {
            if (it.id == vocabId) it.copy(isCompleted = true) else it
        }
        val newCompletedCount = updatedWords.count { it.isCompleted }
        val newStatus = if (newCompletedCount >= response.targetCount) "COMPLETED" else response.status
        taskWordsMap[taskId] = response.copy(
            words = updatedWords,
            completedCount = newCompletedCount,
            status = newStatus
        )
        return true
    }

    fun clear() {
        vocabToTaskMap.clear()
        taskWordsMap.clear()
    }
}
