package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.UpdateUserRoleRequest
import com.penguin.linguae.data.model.UpdateUserRoleResponse
import com.penguin.linguae.data.model.UserListResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("/users/number-of-users")
    suspend fun getNumberOfUsers(): Int

    @GET("/users")
    suspend fun getAllUsers(): UserListResponse

    @PATCH("/users/{id}/role")
    suspend fun updateUserRole(
        @Path("id") id: String,
        @Body request: UpdateUserRoleRequest
    ): UpdateUserRoleResponse

    @DELETE("/users/{id}")
    suspend fun deleteUser(
        @Path("id") id: String
    ): retrofit2.Response<Unit>

    @GET("/users/search")
    suspend fun searchUsers(@Query("q") query: String): UserListResponse
}