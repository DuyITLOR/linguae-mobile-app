package com.penguin.linguae.data.repository

import android.util.Log
import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.remote.UserApi

class UserRepository {
    private val api = RetrofitClient.create(UserApi::class.java)

    suspend fun getNumberOfUsers(): Result<Int> {
        return try {
            val data = api.getNumberOfUsers()
            Log.i("UserRepository", "Total Number: $data")
            Result.success(data)
        } catch (e: Exception) {
            Log.e(
                "UserRepository",
                "Error fetching number of user:",
                e
            )
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): Result<List<com.penguin.linguae.data.model.User>> {
        return try {
            val response = api.getAllUsers()
            if (response.success) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching all users", e)
            Result.failure(e)
        }
    }

    suspend fun updateUserRole(id: String, role: com.penguin.linguae.data.model.UserRole): Result<Unit> {
        return try {
            val response = api.updateUserRole(id, com.penguin.linguae.data.model.UpdateUserRoleRequest(role))
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user role", e)
            Result.failure(e)
        }
    }

    suspend fun searchUsers(query: String): Result<List<com.penguin.linguae.data.model.User>> {
        return try {
            val response = api.searchUsers(query)
            if (response.success) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error searching users", e)
            Result.failure(e)
        }
    }

    suspend fun deleteUser(id: String): Result<Unit> {
        return try {
            val response = api.deleteUser(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Lỗi khi xóa người dùng"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error deleting user", e)
            Result.failure(e)
        }
    }
}