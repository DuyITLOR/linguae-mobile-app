package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.ForgotPasswordRequest
import com.penguin.linguae.data.model.ForgotPasswordResponse
import com.penguin.linguae.data.model.LoginRequest
import com.penguin.linguae.data.model.LoginResponse
import com.penguin.linguae.data.model.LoginWithGoogleRequest
import com.penguin.linguae.data.model.LoginWithGoogleResponse
import com.penguin.linguae.data.model.RegisterRequest
import com.penguin.linguae.data.model.RegisterResponse
import com.penguin.linguae.data.model.ResetPasswordRequest
import com.penguin.linguae.data.model.ResetPasswordResponse
import retrofit2.http.Body
import retrofit2.http.POST
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

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ) : ForgotPasswordResponse


    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): ResetPasswordResponse
}






