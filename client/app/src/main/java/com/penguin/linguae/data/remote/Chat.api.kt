package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.AskChatRequest
import com.penguin.linguae.data.model.AskChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {
    @POST("chat/ask")
    suspend fun ask(
        @Body request: AskChatRequest
    ): AskChatResponse
}