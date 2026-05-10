package com.penguin.linguae.data.repository

import com.penguin.linguae.core.network.RetrofitClient
import com.penguin.linguae.data.model.ClozeQuestion
import com.penguin.linguae.data.model.CompleteWordRequest
import com.penguin.linguae.data.model.CompleteWordResponse
import com.penguin.linguae.data.model.DailyActivityStats
import com.penguin.linguae.data.model.DailyMission
import com.penguin.linguae.data.model.DailyMissionSummary
import com.penguin.linguae.data.model.MatchingPairUi
import com.penguin.linguae.data.model.StatisticsResponse
import com.penguin.linguae.data.model.TaskWordsResponse
import com.penguin.linguae.data.remote.DailyMissionApi

class DailyMissionRepository {

    private val api = RetrofitClient.create(DailyMissionApi::class.java)

    suspend fun getTodayMission(): Result<DailyMission> {
        return try {
            Result.success(api.getTodayMission())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTodaySummary(): Result<DailyMissionSummary> {
        return try {
            Result.success(api.getTodaySummary())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTaskWords(taskId: String): Result<TaskWordsResponse> {
        return try {
            Result.success(api.getTaskWords(taskId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStatistics(): Result<StatisticsResponse> {
        return try {
            Result.success(api.getStatistics())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeeklyActivity(weekOffset: Int): Result<List<DailyActivityStats>> {
        return try {
            Result.success(api.getWeeklyActivity(weekOffset))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeWord(taskId: String, vocabularyId: String): Result<CompleteWordResponse> {
        return try {
            Result.success(api.completeWord(taskId, CompleteWordRequest(vocabularyId)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDailyClozeQuestions(taskId: String): Result<List<ClozeQuestion>> {
        return try {
            Result.success(api.getDailyClozeQuestions(taskId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDailyMatchingQuestions(taskId: String): Result<List<MatchingPairUi>> {
        return try {
            Result.success(api.getDailyMatchingQuestions(taskId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeDailyExercise(taskId: String): Result<CompleteWordResponse> {
        return try {
            Result.success(api.completeDailyExercise(taskId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
