package com.penguin.linguae.data.repository

import android.util.Log
import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.Favorite
import com.penguin.linguae.data.remote.FavoriteApi


class FavoriteRepository {

    private val api = RetrofitClient.create(FavoriteApi::class.java)

    suspend fun getFavoriteByUserId(): Result<List<Favorite>> {
        return try {
            val data = api.getFavoriteByUserId()

            Log.i("API_TEST_FAVORITE", data.toString())
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createFavorite(vocabId: String): Result<Favorite> {
        return try {
            val data = api.createFavorite(vocabId)

            Log.i("API_TEST", data.toString())
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorite(vocabId: String): Result<String> {
        return try {
            api.removeFavorite(vocabId)
            Log.i("API_TEST", "Delete favorite")
            Result.success("Success")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}