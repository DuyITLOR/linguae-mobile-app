package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.remote.AuthApi
import com.penguin.linguae.data.remote.LoginRequest

class AuthRepository {
    private val api = RetrofitClient.create(AuthApi::class.java)

    suspend fun login(email: String, password: String): String {
        val response = api.login(
            LoginRequest(email, password)
        )

        if (response.success) {
            return response.data.accessToken
        } else {
            throw Exception(response.message)
        }
    }
}