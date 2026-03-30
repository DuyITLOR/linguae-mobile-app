package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Favorite
import retrofit2.http.GET
import retrofit2.http.Path

interface FavoriteApi {
    @GET("favourite")
    suspend fun getFavoriteByUserId(): Favorite
}