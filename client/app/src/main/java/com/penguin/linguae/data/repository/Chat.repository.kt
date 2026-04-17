package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.AskChatData
import com.penguin.linguae.data.model.AskChatRequest
import com.penguin.linguae.data.remote.ChatApi

class ChatRepository {
    private val api = RetrofitClient.create(ChatApi::class.java)

    suspend fun ask(message: String): AskChatData {
        val response = api.ask(
            AskChatRequest(message = message.trim())
        )

        if (!response.success) {
            throw IllegalStateException(response.message)
        }
        return response.data
    }
}
