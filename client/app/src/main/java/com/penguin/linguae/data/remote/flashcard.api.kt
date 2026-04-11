package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Topic
import retrofit2.http.GET

interface FlashcardApi {
    @GET("topic")
    suspend fun getAllTopics(): List<Topic>
}