package com.penguin.linguae.core.network.api

import com.penguin.linguae.data.model.Column
import retrofit2.http.GET

interface VocabularyApi {

    @GET("columns")
    suspend fun getVocabularies(): List<Column>
}