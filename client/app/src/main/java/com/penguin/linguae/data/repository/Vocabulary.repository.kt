package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.CreateVocabularyRequest
import com.penguin.linguae.data.model.Vocabulary
import com.penguin.linguae.data.remote.VocabularyApi

class VocabularyRepository {

    private val api = RetrofitClient.create(VocabularyApi::class.java)

    suspend fun getAllVocabulary(query: String): Result<List<Vocabulary>> {
        return try {
            val data = api.getAllVocabulary(query)
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVocabularyById(id: String): Result<Vocabulary> {
        return try {
            val data = api.getVocabularyById(id)
            Result.success(data)
        } catch(e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVocabularyByTopic(topicId: String, query: String): Result<List<Vocabulary>> {
        return try {
            val data = api.getVocabularyByTopic(topicId, query)
            Result.success(data)
        } catch(e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createVocabulary(request: CreateVocabularyRequest): Result<Vocabulary> {
        return try {
            Result.success(api.createVocabulary(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
