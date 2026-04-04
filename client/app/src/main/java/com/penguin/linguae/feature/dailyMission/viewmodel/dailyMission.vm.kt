package com.penguin.linguae.feature.dailyMission.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.core.DailyMissionCache
import com.penguin.linguae.data.model.DailyMissionSummary
import com.penguin.linguae.data.model.TaskWordsResponse
import com.penguin.linguae.data.repository.DailyMissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DailyMissionUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val summary: DailyMissionSummary? = null,
    val expandedTaskId: String? = null,
    val taskWordsMap: Map<String, TaskWordsResponse> = emptyMap(),
    val showCongrats: Boolean = false
)

class DailyMissionViewModel : ViewModel() {

    private val repository = DailyMissionRepository()

    private val _uiState = MutableStateFlow(DailyMissionUiState())
    val uiState: StateFlow<DailyMissionUiState> = _uiState.asStateFlow()

    init {
        loadSummary()
    }

    fun loadSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, taskWordsMap = emptyMap()) }
            // getTodayMission creates the mission for the day if it doesn't exist yet
            repository.getTodayMission()
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Lỗi tải dữ liệu") }
                    return@launch
                }
            repository.getTodaySummary()
                .onSuccess { summary ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            summary = summary,
                            showCongrats = summary.missionStatus == "COMPLETED"
                        )
                    }
                    // Eagerly load words for all tasks to populate DailyMissionCache
                    summary.tasks?.forEach { task ->
                        launch { loadTaskWords(task.id) }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Lỗi tải dữ liệu") }
                }
        }
    }

    fun toggleVocabularyTask(taskId: String) {
        val current = _uiState.value
        _uiState.update {
            it.copy(expandedTaskId = if (current.expandedTaskId == taskId) null else taskId)
        }
    }

    private suspend fun loadTaskWords(taskId: String) {
        repository.getTaskWords(taskId)
            .onSuccess { response ->
                DailyMissionCache.setTaskData(taskId, response)
                _uiState.update { state ->
                    state.copy(taskWordsMap = state.taskWordsMap + (taskId to response))
                }
            }
    }
}
