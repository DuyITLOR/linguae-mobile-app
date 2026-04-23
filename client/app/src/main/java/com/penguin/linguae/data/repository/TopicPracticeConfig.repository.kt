package com.penguin.linguae.data.repository

import android.util.Log
import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.TopicPracticeConfig
import com.penguin.linguae.data.remote.TopicPracticeConfigApi

class TopicPracticeConfigRepository {

    private val api = RetrofitClient.create(TopicPracticeConfigApi::class.java)

    suspend fun getPracticeConfigByTopicId(topicId: String): Result<List<TopicPracticeConfig>> {
        return try {
            val data = api.getPracticeConfigByTopicId(topicId = topicId)
            Log.i("TopicPracticeConfigRepo", "Fetched practice configs for $topicId: $data")
            Result.success(data)
        } catch (e: Exception) {
            Log.e("TopicPracticeConfigRepo", "Error fetching practice configs for $topicId", e)
            Result.failure(e)
        }
    }
}