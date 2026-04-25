package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.CreateReadingPart5QuestionRequest
import com.penguin.linguae.data.model.CreateReadingPart6QuestionRequest
import com.penguin.linguae.data.model.CreateToeicRequest
import com.penguin.linguae.data.model.CreateToeicResponse
import com.penguin.linguae.data.model.ReadingPart5Question
import com.penguin.linguae.data.model.ReadingPart6Question
import com.penguin.linguae.data.model.SubmitToeicAnswerRequest
import com.penguin.linguae.data.model.SubmitToeicAnswerResponse
import com.penguin.linguae.data.model.Toeic
import com.penguin.linguae.data.remote.ToeicApi

class ToeicRepository {

    private val api = RetrofitClient.create(ToeicApi::class.java)

    suspend fun getAllToeic(): Result<List<Toeic>> {
        return try {
            Result.success(api.getAllToeic())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createToeic(request: CreateToeicRequest): Result<CreateToeicResponse> {
        return try {
            Result.success(api.createToeic(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createReadingPart5Questions(
        questions: List<CreateReadingPart5QuestionRequest>
    ): Result<Unit> {
        return try {
            Result.success(api.createReadingPart5Questions(questions))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createReadingPart6Questions(
        questions: List<CreateReadingPart6QuestionRequest>
    ): Result<Unit> {
        return try {
            Result.success(api.createReadingPart6Questions(questions))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReadingPart5Questions(toeicId: String): Result<List<ReadingPart5Question>> {
        return try {
            Result.success(api.getReadingPart5Questions(toeicId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReadingPart6Questions(toeicId: String): Result<List<ReadingPart6Question>> {
        return try {
            Result.success(api.getReadingPart6Questions(toeicId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitToeicAnswer(request: SubmitToeicAnswerRequest): Result<SubmitToeicAnswerResponse> {
        return try {
            Result.success(api.submitToeicAnswer(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
