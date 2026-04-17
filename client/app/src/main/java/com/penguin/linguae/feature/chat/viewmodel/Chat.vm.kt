package com.penguin.linguae.feature.chat.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.penguin.linguae.data.model.ChatUiMessage
import com.penguin.linguae.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChatViewModel(
    private val repo: ChatRepository = ChatRepository()
) : ViewModel() {

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    private val _messages = MutableStateFlow(
        listOf(
            ChatUiMessage(
                id = "welcome",
                text = "Xin chào, mình là trợ lý Linguae. Bạn muốn học gì hôm nay?",
                isFromBot = true
            )
        )
    )
    val messages: StateFlow<List<ChatUiMessage>> = _messages.asStateFlow()

    fun ask(message: String) {
        val trimmedMessage = message.trim()

        if (trimmedMessage.isEmpty() || isLoading) return

        val userMessage = ChatUiMessage(
            id = "user_${System.currentTimeMillis()}",
            text = trimmedMessage,
            isFromBot = false
        )

        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                val result = repo.ask(trimmedMessage)

                val botMessage = ChatUiMessage(
                    id = "bot_${System.currentTimeMillis()}",
                    text = result.answer,
                    isFromBot = true
                )

                _messages.value = _messages.value + botMessage
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val messageError = try {
                    val json = Gson().fromJson(errorBody, Map::class.java)
                    json["message"]?.toString() ?: "Không gửi được tin nhắn"
                } catch (ex: Exception) {
                    "Không gửi được tin nhắn"
                }

                error = messageError
                appendErrorMessage(messageError)
            } catch (e: Exception) {
                val messageError = e.message ?: "Không thể kết nối đến máy chủ"
                error = messageError
                appendErrorMessage(messageError)
            } finally {
                isLoading = false
            }
        }
    }

    fun clearError() {
        error = null
    }

    private fun appendErrorMessage(message: String) {
        val botMessage = ChatUiMessage(
            id = "error_${System.currentTimeMillis()}",
            text = message,
            isFromBot = true
        )

        _messages.value = _messages.value + botMessage
    }
}
