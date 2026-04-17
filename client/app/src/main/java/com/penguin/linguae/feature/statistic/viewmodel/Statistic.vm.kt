package com.penguin.linguae.feature.statistic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.StatisticsResponse
import com.penguin.linguae.data.repository.DailyMissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StatisticUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val data: StatisticsResponse? = null
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
                    _state.update { it.copy(isLoading = false, data = data) }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, isError = true) }
                }
        }
    }
}
