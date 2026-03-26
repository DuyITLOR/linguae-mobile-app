package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.User
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
    val user: User
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: RegisterData?
)

data class RegisterData(
    val accessToken: String,
    val user: User
)

data class LoginWithGoogleResponse(
    val success: Boolean,
    val message: String,
    val data: LoginWithGoogleData?
)

data class LoginWithGoogleData(
    val accessToken: String,
    val user: User
)

data class LoginWithGoogleRequest(
    val idToken: String
)




interface AuthApi {
    @POST("auth/sign-in")
    suspend fun login(
        @Body request: LoginRequest
    ) : LoginResponse

    @POST("auth/sign-up")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("auth/google")
    suspend fun loginWithGoogle(
        @Body request: LoginWithGoogleRequest
    ) : LoginWithGoogleResponse
}






