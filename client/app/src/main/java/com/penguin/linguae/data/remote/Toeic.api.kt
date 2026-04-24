package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Toeic
import com.penguin.linguae.data.model.ToeicEditorDetail
import com.penguin.linguae.data.model.CreateToeicEditorRequest
import com.penguin.linguae.data.model.UpdateToeicEditorRequest
import com.penguin.linguae.data.model.ReadingPart5Question
import com.penguin.linguae.data.model.ReadingPart6Question
import com.penguin.linguae.data.model.SubmitToeicAnswerRequest
import com.penguin.linguae.data.model.SubmitToeicAnswerResponse
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT

interface ToeicApi {
    @GET("toeic")
    suspend fun getAllToeic(): List<Toeic>

    @GET("toeic/reading-part-5-questions/{toeicId}")
    suspend fun getReadingPart5Questions(@Path("toeicId") toeicId: String): List<ReadingPart5Question>

    @GET("toeic/reading-part-6-questions/{toeicId}")
    suspend fun getReadingPart6Questions(@Path("toeicId") toeicId: String): List<ReadingPart6Question>

    @GET("toeic/{id}")
    suspend fun getToeicEditorDetail(@Path("id") id: String): ToeicEditorDetail

    @POST("toeic")
    suspend fun createToeicEditor(@Body request: CreateToeicEditorRequest): Toeic

    @PUT("toeic/{id}")
    suspend fun updateToeicEditor(
        @Path("id") id: String,
        @Body request: UpdateToeicEditorRequest
    ): Toeic

    @POST("toeic/test/submit-answer")
    suspend fun submitToeicAnswer(@Body request: SubmitToeicAnswerRequest): SubmitToeicAnswerResponse
}
