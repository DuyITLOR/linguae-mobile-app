package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.CreateTopicRequest
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.data.model.TopicIncludeVocab
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TopicApi {
    @GET("topic")
    suspend fun getAllTopic(
        @Query("q") query: String
    ): List<Topic>

    @GET("topic/{topicId}")
    suspend fun getTopicById(
        @Path("topicId") topicId: String
    ): TopicIncludeVocab

    @POST("topic")
    suspend fun createTopic(
        @Body request: CreateTopicRequest
    ): Topic
}
