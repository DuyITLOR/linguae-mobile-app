package com.penguin.linguae.data.model

data class ChatUiMessage(
    val id: String,
    val text: String,
    val isFromBot: Boolean
)
data class AskChatRequest(
    val message: String
)

data class AskChatResponse(
    val success: Boolean,
    val statusCode: Int,
    val message: String,
    val data: AskChatData
)

data class AskChatData(
    val answer: String,
    val model: String
)