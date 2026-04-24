package com.penguin.linguae.data.repository

import android.util.Log
import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.CreateTopicRequest
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.data.model.TopicIncludeVocab
import com.penguin.linguae.data.model.UpdateTopicRequest
import com.penguin.linguae.data.remote.TopicApi
import org.json.JSONObject
import retrofit2.HttpException


class TopicRepository {

    private val api = RetrofitClient.create(TopicApi::class.java)

    private fun parseHttpError(e: HttpException): Exception {
        val message = try {
            val body = e.response()?.errorBody()?.string()
            if (!body.isNullOrBlank()) JSONObject(body).optString("message").takeIf { it.isNotBlank() } else null
        } catch (_: Exception) {
            null
        }
        return Exception(message ?: e.message())
    }

    suspend fun getAllTopic(query: String): Result<List<Topic>> {
        return try {
            val data = api.getAllTopic(query)

            Log.i("API_TEST", data.toString())
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopicById(topicId: String): Result<TopicIncludeVocab> {
        return try {
            val data = api.getTopicById(topicId = topicId)

            Log.i("API_TEST", data.toString())
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTopic(request: CreateTopicRequest): Result<Topic> {
        return try {
            Result.success(api.createTopic(request))
        } catch (e: HttpException) {
            Result.failure(parseHttpError(e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTopic(topicId: String, request: UpdateTopicRequest): Result<Topic> {
        return try {
            Result.success(api.updateTopic(topicId, request))
        } catch (e: HttpException) {
            Result.failure(parseHttpError(e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTopic(topicId: String): Result<Unit> {
        return try {
            api.deleteTopic(topicId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
