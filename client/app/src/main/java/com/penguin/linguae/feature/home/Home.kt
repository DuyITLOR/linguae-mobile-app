package com.penguin.linguae.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.feature.home.components.HomeHeader
import com.penguin.linguae.feature.home.components.QuickLearn
import com.penguin.linguae.feature.home.components.TodayProgress
import com.penguin.linguae.feature.home.viewmodel.HomeViewModel
import androidx.compose.runtime.getValue

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = HomeViewModel()
) {
    val uiState by viewModel.homeState.collectAsStateWithLifecycle()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Refresh progress bar whenever the user returns to HomeScreen
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshProgress()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Surface(
        color = AppBackground,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                HomeHeader(
                    name = uiState.user?.fullName ?: "bạn",
                    dayStreak = uiState.streak,
                )
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    TodayProgress(
                        progress = uiState.todayProgress,
                        onViewMission = {
                            navController.navigate(Screen.DailyMission.route)
                        }
                    )
                }
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    QuickLearn(
                        onVocabularyClick = {
                            navController.navigate(Screen.Topic.route)
                        },
                        onFlashcardClick = {
                            navController.navigate(Screen.FlashcardList.route)
                        },
                        onFavoriteClick = {
                            navController.navigate(Screen.FavoriteVocabulary.route) {
                                launchSingleTop = true
                            }
                        },
                        onToeicMockTestClick = {
                            navController.navigate(Screen.ToeicMockTestList.route)
                        },
                        onPracticeClick = {
                            navController.navigate(Screen.PracticeTopic.route)
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
