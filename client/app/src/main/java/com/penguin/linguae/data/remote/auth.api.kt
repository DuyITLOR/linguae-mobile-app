package com.penguin.linguae.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest (
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData
)

data class LoginData(
    val accessToken: String,
    val user: UserData? = null
)

data class UserData(
    val id: String,
    val email: String,
    val fullName: String,
    val avatarUrl: String?
)

interface AuthApi {
    @POST("auth/sign-in")
    suspend fun login(
        @Body request: LoginRequest
    ) : LoginResponse

}