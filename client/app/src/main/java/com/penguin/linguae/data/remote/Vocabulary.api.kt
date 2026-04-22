package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.CreateVocabularyRequest
import com.penguin.linguae.data.model.UpdateVocabularyRequest
import com.penguin.linguae.data.model.Vocabulary
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface VocabularyApi {
    @GET("vocabulary")
    suspend fun getAllVocabulary(
        @Query("q") query: String
    ): List<Vocabulary>

    @GET("vocabulary/{id}")
    suspend fun getVocabularyById(
        @Path("id") id: String
    ): Vocabulary

    @GET("vocabulary/topic/{id}")
    suspend fun getVocabularyByTopic(
        @Path("id") topicId: String,
        @Query("q") query: String
    ): List<Vocabulary>

    @POST("vocabulary")
    suspend fun createVocabulary(
        @Body request: CreateVocabularyRequest
    ): Vocabulary

    @PUT("vocabulary/{id}")
    suspend fun updateVocabulary(
        @Path("id") id: String,
        @Body request: UpdateVocabularyRequest
    ): Vocabulary

    @DELETE("vocabulary/{id}")
    suspend fun deleteVocabulary(
        @Path("id") id: String
    ): Vocabulary
}
