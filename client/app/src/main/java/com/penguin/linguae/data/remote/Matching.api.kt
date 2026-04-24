package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.adminmatching.CreateMatchingQuestionRequest
import com.penguin.linguae.data.model.adminmatching.MatchingQuestionUi
import com.penguin.linguae.data.model.adminmatching.UpdateMatchingQuestionRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MatchingApi {
    @GET("matching")
    suspend fun getMatchingQuestions(
        @Query("topicId") topicId: String? = null
    ): List<MatchingQuestionUi>

    @POST("matching")
    suspend fun createMatchingQuestion(
        @Body request: CreateMatchingQuestionRequest
    ): MatchingQuestionUi

    @PATCH("matching/{id}")
    suspend fun updateMatchingQuestion(
        @Path("id") id: String,
        @Body request: UpdateMatchingQuestionRequest
    ): MatchingQuestionUi

    @DELETE("matching/{id}")
    suspend fun deleteMatchingQuestion(
        @Path("id") id: String
    ): MatchingQuestionUi
}
