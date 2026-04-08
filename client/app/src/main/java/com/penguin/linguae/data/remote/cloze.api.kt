package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.ClozeOption
import com.penguin.linguae.data.model.ClozeQuestion
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

}
