package com.penguin.linguae.data.repository
import android.util.Log
import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.data.remote.TopicApi


class TopicRepository {

    private val api = RetrofitClient.create(TopicApi::class.java)

    suspend fun getAllTopic(query: String): Result<List<Topic>> {
        return try {
            val data = api.getAllTopic(query)

            Log.i("API_TEST", data.toString())
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}