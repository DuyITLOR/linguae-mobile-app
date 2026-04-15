package com.penguin.linguae.data.model

sealed class ResultData {
    data class ClozeResult(
        val score: Int,
        val total: Int,
        val topicId: String
    ) : ResultData()
}