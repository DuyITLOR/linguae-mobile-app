package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.CreateMatchingQuestionRequest
import com.penguin.linguae.data.model.MatchingQuestionUi
import com.penguin.linguae.data.model.UpdateMatchingQuestionRequest
import com.penguin.linguae.data.remote.MatchingApi

class MatchingRepository {

    private val api = RetrofitClient.create(MatchingApi::class.java)

    suspend fun getMatchingQuestions(topicId: String? = null): Result<List<MatchingQuestionUi>> {
        return try {
            Result.success(api.getMatchingQuestions(topicId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createMatchingQuestion(
        request: CreateMatchingQuestionRequest
    ): Result<MatchingQuestionUi> {
        return try {
            Result.success(api.createMatchingQuestion(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMatchingQuestion(
        id: String,
        request: UpdateMatchingQuestionRequest
    ): Result<MatchingQuestionUi> {
        return try {
            Result.success(api.updateMatchingQuestion(id, request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMatchingQuestion(id: String): Result<MatchingQuestionUi> {
        return try {
            Result.success(api.deleteMatchingQuestion(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
