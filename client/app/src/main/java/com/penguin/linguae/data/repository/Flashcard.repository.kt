package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.FlashcardReviewRequest
import com.penguin.linguae.data.model.FlashcardReviewStatus
import com.penguin.linguae.data.model.FlashcardTopic
import com.penguin.linguae.data.remote.FlashcardApi

class FlashcardRepository {

    private val api = RetrofitClient.create(FlashcardApi::class.java)

    suspend fun reviewFlashcard(
        vocabularyId: String,
        status: FlashcardReviewStatus
    ): Result<Unit> {
        return try {
            val response = api.reviewFlashcard(
                FlashcardReviewRequest(
                    vocabularyId = vocabularyId,
                    status = status
                )
            )

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Flashcard review request failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllFlashcardTopics(): Result<List<FlashcardTopic>> {
        return try {
            Result.success(api.getAllFlashcardTopics())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}