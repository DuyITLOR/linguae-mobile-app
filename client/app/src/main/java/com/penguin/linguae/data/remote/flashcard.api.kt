package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.FlashcardReviewRequest
import com.penguin.linguae.data.model.FlashcardTopic
import com.penguin.linguae.data.model.Topic
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FlashcardApi {
    @GET("flashcard/topics")
    suspend fun getAllFlashcardTopics(): List<FlashcardTopic>

    @POST("flashcard")
    suspend fun reviewFlashcard(
        @Body request: FlashcardReviewRequest
    ): Response<Void>
}