package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Topic
import retrofit2.http.GET

interface TopicApi {
    @GET("topic")
    suspend fun getAllTopic(): List<Topic>
}