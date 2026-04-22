package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.CreateTopicRequest
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.data.model.TopicIncludeVocab
import com.penguin.linguae.data.model.UpdateTopicRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @PUT("topic/{id}")
    suspend fun updateTopic(
        @Path("id") id: String,
        @Body request: UpdateTopicRequest
    ): Topic

    @DELETE("topic/{id}")
    suspend fun deleteTopic(
        @Path("id") id: String
    ): Unit
}
