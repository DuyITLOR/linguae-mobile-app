package com.penguin.linguae.feature.statistic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.DailyActivityStats
import com.penguin.linguae.data.model.StatisticsResponse
import com.penguin.linguae.data.repository.DailyMissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StatisticUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val data: StatisticsResponse? = null,
    val weekOffset: Int = 0,
    val weeklyActivity: List<DailyActivityStats> = emptyList(),
    val weeklyActivityLoading: Boolean = false
)

class StatisticViewModel : ViewModel() {
    private val repository = DailyMissionRepository()
    private val _state = MutableStateFlow(StatisticUiState())
    val state = _state.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false) }
            repository.getStatistics()
                .onSuccess { data ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            data = data,
                            weekOffset = 0,
                            weeklyActivity = data.weeklyActivity
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, isError = true) }
                }
        }
    }

    fun prevWeek() {
        val newOffset = _state.value.weekOffset - 1
        _state.update { it.copy(weekOffset = newOffset) }
        loadWeeklyActivity(newOffset)
    }

    fun nextWeek() {
        val current = _state.value.weekOffset
        if (current >= 0) return
        val newOffset = current + 1
        _state.update { it.copy(weekOffset = newOffset) }
        if (newOffset == 0) {
            // Restore original data from full stats
            _state.update { it.copy(weeklyActivity = it.data?.weeklyActivity ?: emptyList()) }
        } else {
            loadWeeklyActivity(newOffset)
        }
    }

    private fun loadWeeklyActivity(weekOffset: Int) {
        viewModelScope.launch {
            _state.update { it.copy(weeklyActivityLoading = true) }
            repository.getWeeklyActivity(weekOffset)
                .onSuccess { activity ->
                    _state.update { it.copy(weeklyActivityLoading = false, weeklyActivity = activity) }
                }
                .onFailure {
                    _state.update { it.copy(weeklyActivityLoading = false) }
                }
        }
    }
}
