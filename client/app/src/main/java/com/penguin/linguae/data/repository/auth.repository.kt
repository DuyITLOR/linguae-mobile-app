package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.remote.AuthApi
import com.penguin.linguae.data.remote.LoginData
import com.penguin.linguae.data.remote.LoginRequest
import com.penguin.linguae.data.remote.LoginWithGoogleRequest
import com.penguin.linguae.data.remote.RegisterRequest

class AuthRepository {
    private val api = RetrofitClient.create(AuthApi::class.java)

    suspend fun login(email: String, password: String): LoginData {
        val response = api.login(
            LoginRequest(email, password)
        )

        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }


    suspend fun register(fullName: String, email: String, password: String) {
        val response = api.register(
            RegisterRequest(fullName, email, password)
        )
        if (!response.success) {
            throw Exception(response.message)
        }
    }

    suspend fun loginWithGoogle(idToken: String) : String {
        val response = api.loginWithGoogle(
            LoginWithGoogleRequest(idToken)
        )
        if(response.success && response.data != null) {
            return response.data.accessToken
        } else {
            throw Exception(response.message)
        }
    }
}