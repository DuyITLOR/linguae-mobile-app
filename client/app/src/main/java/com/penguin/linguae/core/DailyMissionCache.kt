package com.penguin.linguae.core

import com.penguin.linguae.data.model.TaskWordsResponse

object DailyMissionCache {
    // vocabId -> (taskType -> taskId), keeps each task type's mapping separate
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

    fun clear() {
        vocabToTaskMap.clear()
        taskWordsMap.clear()
    }
}
