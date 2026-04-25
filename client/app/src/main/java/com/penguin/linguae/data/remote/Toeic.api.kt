package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.Toeic
import com.penguin.linguae.data.model.CreateToeicRequest
import com.penguin.linguae.data.model.CreateToeicResponse
import com.penguin.linguae.data.model.CreateReadingPart5QuestionRequest
import com.penguin.linguae.data.model.CreateReadingPart6QuestionRequest
import com.penguin.linguae.data.model.ReadingPart5Question
import com.penguin.linguae.data.model.ReadingPart6Question
import com.penguin.linguae.data.model.SubmitToeicAnswerRequest
import com.penguin.linguae.data.model.SubmitToeicAnswerResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.POST

interface ToeicApi {
    @GET("toeic")
    suspend fun getAllToeic(): List<Toeic>

    @POST("toeic")
    suspend fun createToeic(@Body request: CreateToeicRequest): CreateToeicResponse

    @POST("toeic/reading-part-5-questions")
    suspend fun createReadingPart5Questions(@Body request: List<CreateReadingPart5QuestionRequest>)

    @POST("toeic/reading-part-6-questions")
    suspend fun createReadingPart6Questions(@Body request: List<CreateReadingPart6QuestionRequest>)

    @GET("toeic/reading-part-5-questions/{toeicId}")
    suspend fun getReadingPart5Questions(@Path("toeicId") toeicId: String): List<ReadingPart5Question>

    @GET("toeic/reading-part-6-questions/{toeicId}")
    suspend fun getReadingPart6Questions(@Path("toeicId") toeicId: String): List<ReadingPart6Question>

    @POST("toeic/test/submit-answer")
    suspend fun submitToeicAnswer(@Body request: SubmitToeicAnswerRequest): SubmitToeicAnswerResponse

    @DELETE("toeic/{toeicId}")
    suspend fun deleteToeic(@Path("toeicId") toeicId: String)
}
