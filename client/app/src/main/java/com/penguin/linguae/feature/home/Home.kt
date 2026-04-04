package com.penguin.linguae.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.core.ui.theme.*
import com.penguin.linguae.feature.home.components.*
import com.penguin.linguae.feature.home.viewmodel.HomeViewModel
import androidx.compose.runtime.getValue

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = HomeViewModel()
) {
    val uiState by viewModel.homeState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { route ->
            navController.navigate(route)
        }
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
                    wordLearned = uiState.wordLearned,
                )
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    TodayProgress(uiState.todayProgress)
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
                        onPracticeClick = {
                            navController.navigate(Screen.Flashcard.route)
                        },
                        onFavoriteClick = {
                            navController.navigate(Screen.FavoriteVocabulary.route) {
                                launchSingleTop = true
                            }
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
