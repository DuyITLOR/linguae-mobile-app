package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Vocabulary
import retrofit2.http.GET
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
}