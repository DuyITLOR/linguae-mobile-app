package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Topic
import retrofit2.http.GET
import retrofit2.http.Query

interface TopicApi {
    @GET("topic")
    suspend fun getAllTopic(
        @Query("q") query: String
    ): List<Topic>
}