package com.penguin.linguae.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.User
import com.penguin.linguae.data.remote.ProfileApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileRepository {
    private val api = RetrofitClient.create(ProfileApi::class.java)

    suspend fun updateMyProfile(
        context: Context,
        fullName: String?,
        avatarUri: Uri?,
    ): User {
        val trimmedFullName = fullName?.trim()?.takeIf { it.isNotEmpty() }

        if (trimmedFullName == null && avatarUri == null) {
            throw IllegalArgumentException("Vui lòng cung cấp thông tin để cập nhật")
        }

        val fullNameBody = trimmedFullName?.toRequestBody("text/plain".toMediaType())
        val avatarPart = avatarUri?.let { createAvatarPart(context, it) }

        val response = api.updateMyProfile(
            fullName = fullNameBody,
            avatar = avatarPart,
        )

        if (!response.success) {
            throw IllegalStateException(response.message)
        }

        return response.data
    }

    private fun createAvatarPart(
        context: Context,
        uri: Uri,
    ): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val fileName = resolveFileName(context, uri, mimeType)

        val bytes = contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        } ?: throw IllegalStateException("Không thể đọc ảnh đã chọn")

        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            "avatar",
            fileName,
            requestBody,
        )
    }

    private fun resolveFileName(
        context: Context,
        uri: Uri,
        mimeType: String,
    ): String {
        val contentResolver = context.contentResolver

        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                val nameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameColumnIndex >= 0) {
                    val displayName = cursor.getString(nameColumnIndex)
                    if (!displayName.isNullOrBlank()) {
                        return displayName
                    }
                }
            }

        val extension = when (mimeType.lowercase()) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }

        return "avatar_${System.currentTimeMillis()}.$extension"
    }
}
