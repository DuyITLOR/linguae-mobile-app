package com.penguin.linguae.data.repository

import android.util.Log
import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.Favorite
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.data.remote.FavoriteApi
import com.penguin.linguae.data.remote.TopicApi


class FavoriteRepository {

    private val api = RetrofitClient.create(FavoriteApi::class.java)

    suspend fun getFavoriteByUserId(): Result<Favorite> {
        return try {
            val data = api.getFavoriteByUserId()

            Log.i("API_TEST", data.toString())
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}