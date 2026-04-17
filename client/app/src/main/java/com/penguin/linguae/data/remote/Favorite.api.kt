package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Favorite
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApi {
    @GET("favorite")
    suspend fun getFavoriteByUserId(): List<Favorite>

    @POST("favorite/vocabulary/{id}")
    suspend fun createFavorite(
        @Path("id") id: String
    ): Favorite

    @DELETE("favorite/vocabulary/{id}")
    suspend fun removeFavorite(
        @Path("id") id: String
    )
}