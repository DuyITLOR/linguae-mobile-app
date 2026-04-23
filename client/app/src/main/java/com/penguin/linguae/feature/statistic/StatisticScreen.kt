package com.penguin.linguae.feature.statistic

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.PrimaryPurple
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.feature.statistic.components.MissionHistoryRow
import com.penguin.linguae.feature.statistic.components.MissionProgressCard
import com.penguin.linguae.feature.statistic.components.StatisticHeader
import com.penguin.linguae.feature.statistic.components.StreakStatCard
import com.penguin.linguae.feature.statistic.components.WeeklyActivityChart
import com.penguin.linguae.feature.statistic.viewmodel.StatisticViewModel

@Composable
fun StatisticScreen(viewModel: StatisticViewModel = viewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Surface(
        color = AppBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            }

            uiState.isError -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Could not load data",
                        color = TextDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.loadStatistics() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                    ) {
                        Text("Retry")
                    }
                }
            }

            uiState.data != null -> {
                val data = uiState.data!!
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        StatisticHeader(
                            currentStreak = data.streak.currentStreak,
                            totalVocabLearned = data.totalVocabularyLearned
                        )
                    }

                    item {
                        StreakStatCard(
                            currentStreak = data.streak.currentStreak,
                            bestStreak = data.streak.bestStreak,
                            totalVocabLearned = data.totalVocabularyLearned,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    item {
                        WeeklyActivityChart(
                            activity = uiState.weeklyActivity,
                            weekOffset = uiState.weekOffset,
                            isLoading = uiState.weeklyActivityLoading,
                            onPrevWeek = { viewModel.prevWeek() },
                            onNextWeek = { viewModel.nextWeek() },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    item {
                        MissionProgressCard(
                            todayProgress = data.todayProgress,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    item {
                        Text(
                            text = "Mission History",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        )
                    }

                    if (data.missionHistory.isEmpty()) {
                        item {
                            Text(
                                text = "No mission history yet.",
                                color = TextGray,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    } else {
                        items(data.missionHistory) { historyItem ->
                            MissionHistoryRow(
                                item = historyItem,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}
