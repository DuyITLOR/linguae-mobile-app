package com.penguin.linguae.data.model

enum class UserRole {
    USER,
    ADMIN
}

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val avatarUrl: String?,
    val role: UserRole = UserRole.USER,
)
