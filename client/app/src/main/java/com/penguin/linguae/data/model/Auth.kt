package com.penguin.linguae.data.model

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


data class ForgotPasswordRequest(
    val email: String
)

data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String,
    val data: ForgotPasswordRequest
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)


data class ResetPasswordResponse(
    val success: Boolean,
    val statusCode: Int,
    val message: String,
    val data: Any
)