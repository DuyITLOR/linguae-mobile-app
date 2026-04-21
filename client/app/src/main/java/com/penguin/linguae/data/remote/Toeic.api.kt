package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Toeic
import com.penguin.linguae.data.model.ReadingPart5Question
import com.penguin.linguae.data.model.ReadingPart6Question
import retrofit2.http.GET
import retrofit2.http.Path

interface ToeicApi {
    @GET("toeic")
    suspend fun getAllToeic(): List<Toeic>

    @GET("toeic/reading-part-5-questions/{toeicId}")
    suspend fun getReadingPart5Questions(@Path("toeicId") toeicId: String): List<ReadingPart5Question>

    @GET("toeic/reading-part-6-questions/{toeicId}")
    suspend fun getReadingPart6Questions(@Path("toeicId") toeicId: String): List<ReadingPart6Question>
}
