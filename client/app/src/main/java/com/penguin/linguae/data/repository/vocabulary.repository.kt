package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.remote.VocabularyApi
import com.penguin.linguae.data.model.Column


class VocabularyRepository {

    private val api = RetrofitClient.create(VocabularyApi::class.java)

    suspend fun getAllColumn(): Result<List<Column>> {
        return try {
            val data = api.getAllColumn()
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}