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
}