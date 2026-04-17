package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.ProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part

interface ProfileApi {
    @Multipart
    @PATCH("users/info")
    suspend fun updateMyProfile(
        @Part("fullName") fullName: RequestBody? = null,
        @Part avatar: MultipartBody.Part? = null,
    ): ProfileResponse
}
