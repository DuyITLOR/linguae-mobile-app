package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.ClozeOption
import com.penguin.linguae.data.model.ClozeQuestion
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ClozeApi {
    @GET("cloze/questions")
    suspend fun questions(): List<ClozeQuestion>

    @GET("cloze/options-with-id/{id}")
    suspend fun optionsWithId(
        @Path("id") id: Int
    ): List<ClozeOption>

    @POST("cloze/options-with-ids")
    suspend fun optionsWithIds(
        @Body request: List<Int>
    ): List<List<ClozeOption>>
}
