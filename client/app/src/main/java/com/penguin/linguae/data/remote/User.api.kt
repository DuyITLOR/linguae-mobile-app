package com.penguin.linguae.data.remote

import retrofit2.http.GET

interface UserApi {
    @GET("/users/number-of-users")
    suspend fun getNumberOfUsers(): Int
}