package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.ReadingPart5Question
import com.penguin.linguae.data.model.ReadingPart6Question
import com.penguin.linguae.data.model.ToeicEditorDetail
import com.penguin.linguae.data.model.CreateToeicEditorRequest
import com.penguin.linguae.data.model.SubmitToeicAnswerRequest
import com.penguin.linguae.data.model.SubmitToeicAnswerResponse
import com.penguin.linguae.data.model.Toeic
import com.penguin.linguae.data.model.UpdateToeicEditorRequest
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

    suspend fun getToeicEditorDetail(id: String): Result<ToeicEditorDetail> {
        return try {
            Result.success(api.getToeicEditorDetail(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createToeicEditor(request: CreateToeicEditorRequest): Result<Toeic> {
        return try {
            Result.success(api.createToeicEditor(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateToeicEditor(id: String, request: UpdateToeicEditorRequest): Result<Toeic> {
        return try {
            Result.success(api.updateToeicEditor(id, request))
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
