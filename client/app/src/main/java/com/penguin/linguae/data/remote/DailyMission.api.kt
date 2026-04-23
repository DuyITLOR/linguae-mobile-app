package com.penguin.linguae.data.remote

import com.penguin.linguae.data.model.CompleteWordRequest
import com.penguin.linguae.data.model.CompleteWordResponse
import com.penguin.linguae.data.model.DailyActivityStats
import com.penguin.linguae.data.model.DailyMission
import com.penguin.linguae.data.model.DailyMissionSummary
import com.penguin.linguae.data.model.StatisticsResponse
import com.penguin.linguae.data.model.TaskWordsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DailyMissionApi {

    @GET("daily-mission/today")
    suspend fun getTodayMission(): DailyMission

    @GET("daily-mission/today/summary")
    suspend fun getTodaySummary(): DailyMissionSummary

    @GET("daily-mission/task/{taskId}/words")
    suspend fun getTaskWords(@Path("taskId") taskId: String): TaskWordsResponse

    @GET("daily-mission/statistics")
    suspend fun getStatistics(): StatisticsResponse

    @GET("daily-mission/weekly-activity")
    suspend fun getWeeklyActivity(
        @Query("weekOffset") weekOffset: Int
    ): List<DailyActivityStats>

    @POST("daily-mission/task/{taskId}/complete")
    suspend fun completeWord(
        @Path("taskId") taskId: String,
        @Body body: CompleteWordRequest
    ): CompleteWordResponse
}
