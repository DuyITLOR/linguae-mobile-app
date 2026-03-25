package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.Vocabulary
import com.penguin.linguae.data.remote.VocabularyApi


class VocabularyRepository {

    private val api = RetrofitClient.create(VocabularyApi::class.java)

    suspend fun getAllVocabulary(): Result<List<Vocabulary>> {
        return try {
            val data = api.getAllVocabulary()
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVocabularyByTopic(topicId: String): Result<List<Vocabulary>> {
        return try {
            val data = api.getVocabularyByTopic(topicId)
            Result.success(data)
        } catch(e: Exception) {
            Result.failure(e)
        }
    }
}