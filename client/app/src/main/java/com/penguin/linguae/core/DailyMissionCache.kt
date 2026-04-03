package com.penguin.linguae.core

import com.penguin.linguae.data.model.TaskWordsResponse

object DailyMissionCache {
    private val vocabToTaskMap = mutableMapOf<String, String>()
    private val taskWordsMap = mutableMapOf<String, TaskWordsResponse>()

    fun setTaskData(taskId: String, response: TaskWordsResponse) {
        taskWordsMap[taskId] = response
        response.words.forEach { vocabToTaskMap[it.id] = taskId }
    }

    fun getTaskIdForVocab(vocabId: String): String? = vocabToTaskMap[vocabId]

    fun getTaskWords(taskId: String): TaskWordsResponse? = taskWordsMap[taskId]

    fun clear() {
        vocabToTaskMap.clear()
        taskWordsMap.clear()
    }
}
