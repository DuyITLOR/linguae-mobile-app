package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Vocabulary
import retrofit2.http.GET

interface VocabularyApi {
    @GET("vocabulary")
    suspend fun getAllVocabulary(): List<Vocabulary>
}