package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.Toeic
import com.penguin.linguae.data.remote.ToeicApi

class ToeicRepository {

    private val api = RetrofitClient.create(ToeicApi::class.java)

    suspend fun getAllToeic(): Result<List<Toeic>> {
        return try {
            Result.success(api.getAllToeic())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
