package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.api.VocabularyApi
import com.penguin.linguae.data.model.Column


class VocabularyRepository(private val vocabularyService: VocabularyApi) {
    suspend fun getAllColumn(): Result<List<Column>> {
        return try {
            val res = vocabularyService.getVocabularies()
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}