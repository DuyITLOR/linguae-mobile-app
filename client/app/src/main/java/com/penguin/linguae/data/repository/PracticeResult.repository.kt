package com.penguin.linguae.data.repository

import com.penguin.linguae.data.model.ResultData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ResultRepository {
    private val _latestResult = MutableStateFlow<ResultData?>(null)
    val latestResult = _latestResult.asStateFlow()

    fun saveResult(result: ResultData) {
        _latestResult.value = result
    }

    fun clearResult() {
        _latestResult.value = null
    }
}