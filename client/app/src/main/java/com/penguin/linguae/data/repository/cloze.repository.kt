package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.ClozeOption
import com.penguin.linguae.data.model.ClozeQuestion
import com.penguin.linguae.data.remote.ClozeApi

class ClozeRepository {
    private val api = RetrofitClient.create(ClozeApi::class.java)

    suspend fun getClozeQuestions(): List<ClozeQuestion> {
        val questions = api.questions()
        if (questions.isEmpty())
            throw Exception("No cloze questions found")
        return questions
    }

    suspend fun getClozeOptionsForQuestion(id: Int): List<ClozeOption> {
        val options = api.optionsWithId(id)
        if (options.isEmpty())
            throw Exception("No options found for question $id")
        return options
    }

    suspend fun getClozeOptionsForQuestions(ids: List<Int>): List<List<ClozeOption>> {
        val options = api.optionsWithIds(ids)
        if (options.isEmpty())
            throw Exception("No options found for questions $ids")
        if (options.any { it.isEmpty() })
            throw Exception("Some questions have no options")
        return options
    }
}