package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.CreateTopicPracticeConfigRequest
import com.penguin.linguae.data.model.TopicPracticeConfig
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TopicPracticeConfigApi {
    @GET("topic-practice-config")
    suspend fun getAllPracticeConfig(
        @Query("q") query: String? = null
    ): List<TopicPracticeConfig>

    @GET("topic-practice-config/{id}")
    suspend fun getPracticeConfigById(
        @Path("id") id: String
    ): TopicPracticeConfig

    @GET("topic/{topicId}/practice-config")
    suspend fun getPracticeConfigByTopicId(
        @Path("topicId") topicId: String
    ): List<TopicPracticeConfig>

    @POST("topic-practice-config")
    suspend fun createPracticeConfig(
        @Body request: CreateTopicPracticeConfigRequest
    ): TopicPracticeConfig
}
