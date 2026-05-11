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

data class UserListResponse(
    val success: Boolean,
    val message: String,
    val data: List<User>
)

data class UpdateUserRoleRequest(
    val role: UserRole
)

data class UpdateUserRoleResponse(
    val success: Boolean,
    val message: String,
    val data: Any?
)
