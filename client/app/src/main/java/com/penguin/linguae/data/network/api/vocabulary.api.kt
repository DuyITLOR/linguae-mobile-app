package com.penguin.linguae.data.network.api

import com.penguin.linguae.model.Column
import retrofit2.http.GET

interface VocabularyApi {

    @GET("columns")
    suspend fun getVocabularies(): List<Column>
}