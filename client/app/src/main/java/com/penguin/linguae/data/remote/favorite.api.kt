package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Favorite
import retrofit2.http.GET
import retrofit2.http.Path

interface FavoriteApi {
    @GET("favourite/user/{id}")
    suspend fun getFavoriteByUserId(
        @Path("id") id: String
    ): Favorite
}