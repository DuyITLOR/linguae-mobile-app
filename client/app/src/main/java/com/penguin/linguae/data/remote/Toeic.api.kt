package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Toeic
import retrofit2.http.GET

interface ToeicApi {
    @GET("toeic")
    suspend fun getAllToeic(): List<Toeic>
}
