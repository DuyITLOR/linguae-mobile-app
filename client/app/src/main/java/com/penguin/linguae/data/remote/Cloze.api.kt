package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.ClozeOption
import com.penguin.linguae.data.model.ClozeQuestion
import com.penguin.linguae.data.model.CreateClozeQuestionRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ClozeApi {
    @GET("cloze/questions")
    suspend fun questions(): List<ClozeQuestion>

    @GET("cloze/options-with-id")
    suspend fun optionsWithId(
        @Query("id") id: Int
    ): List<ClozeOption>

    @POST("cloze/options-with-ids")
    suspend fun optionsWithIds(
        @Body ids: List<Int>
    ): List<ClozeOption>

    @GET("cloze/questions-by-topic-id")
    suspend fun questionWithTopicId(
        @Query("topicId") topicId: String
    ): List<ClozeQuestion>

    @POST("cloze/create")
    suspend fun createQuestion(
        @Body request: CreateClozeQuestionRequest
    ): ClozeQuestion

    @DELETE("cloze/{id}")
    suspend fun deleteQuestion(
        @Path("id") id: Int
    )

    @PUT("cloze/{id}")
    suspend fun updateQuestion(
        @Path("id") id: Int,
        @Body request: CreateClozeQuestionRequest
    ): ClozeQuestion
}
