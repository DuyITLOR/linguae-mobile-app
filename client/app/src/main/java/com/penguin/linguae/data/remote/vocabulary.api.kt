package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Column
import retrofit2.http.GET

interface VocabularyApi {

    @GET("columns")
    suspend fun getAllColumn(): List<Column>
}