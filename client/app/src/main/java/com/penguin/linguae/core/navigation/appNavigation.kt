package com.penguin.linguae.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.penguin.linguae.core.network.TokenManager
import com.penguin.linguae.core.network.UserManager
import com.penguin.linguae.feature.auth.ForgotPasswordScreen
import com.penguin.linguae.feature.auth.LoginScreen
import com.penguin.linguae.feature.auth.RegisterScreen
import com.penguin.linguae.feature.cloze.ClozeScreen
import com.penguin.linguae.feature.dailyMission.DailyMissionScreen
import com.penguin.linguae.feature.favorite.FavoriteScreen
import com.penguin.linguae.feature.home.HomeScreen
import com.penguin.linguae.feature.learning.FlashcardListScreen
import com.penguin.linguae.feature.learning.SearchScreen
import com.penguin.linguae.feature.learning.FlashcardScreen
import com.penguin.linguae.feature.learning.TopicScreen
import com.penguin.linguae.feature.learning.VocabularyListScreen
import com.penguin.linguae.feature.learning.VocabularyScreen
import com.penguin.linguae.feature.profile.ProfileEditScreen
import com.penguin.linguae.feature.profile.ProfileScreen
import com.penguin.linguae.feature.statistic.StatisticScreen
import com.penguin.linguae.data.model.ProfileUiState
import com.penguin.linguae.feature.learning.viewmodel.TopicViewModel
import com.penguin.linguae.feature.practiceResult.ResultScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val startRoute = if (TokenManager.getToken() != null) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startRoute
    ) {

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Topic.route) {
            TopicScreen(
                onTopicClick = { topicId, title ->
                    navController.navigate(Screen.VocabularyList.createRoute(topicId, title))
                }
            )
        }

        composable(Screen.Progress.route) {
            StatisticScreen()
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel(),
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateForgotPassword = {
                    navController.navigate(Screen.forogtPassword.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = viewModel(),
                onNavigateLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.forogtPassword.route) {
            ForgotPasswordScreen(
                viewModel = viewModel(),
                onNavigateLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            val currentUser = UserManager.getUser()
            ProfileScreen(
                uiState = ProfileUiState(
                    fullName = currentUser?.fullName ?: "No name",
                    email = currentUser?.email ?: "No email",
                    avatarUrl = currentUser?.avatarUrl
                ),
                onNavigateEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onLogout = {
                    TokenManager.clear()
                    UserManager.clearUser()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.EditProfile.route) {
            val currentUser = UserManager.getUser()
            ProfileEditScreen(
                uiState = ProfileUiState(
                    fullName = currentUser?.fullName ?: "No name",
                    email = currentUser?.email ?: "No email",
                    avatarUrl = currentUser?.avatarUrl
                ),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FlashcardList.route) {
            FlashcardListScreen(
                onTopicClick = { topicId ->
                    navController.navigate(Screen.Flashcard.createRoute(topicId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FavoriteVocabulary.route) {
            FavoriteScreen(
                onBack = { navController.popBackStack() },
                onVocabularyClick = { vocabId ->
                    navController.navigate(Screen.Vocabulary.createRoute(vocabId))
                }
            )
        }

        composable(
            route = Screen.VocabularyList.route,
            arguments = listOf(
                navArgument("topicId") {
                    type = NavType.StringType
                },
                navArgument("title") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""

            VocabularyListScreen(
                topicId = topicId,
                title = title,
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Vocabulary.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val vocabId = backStackEntry.arguments?.getString("id") ?: ""
            VocabularyScreen(
                vocabId = vocabId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Flashcard.route,
            arguments = listOf(
                navArgument("topicId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""

            FlashcardScreen(
                topicId = topicId,
                onBack = { navController.popBackStack() }
            )

        }

        composable(
            route = Screen.Cloze.route,
            arguments = listOf(
                navArgument("topicId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            ClozeScreen(
                navController = navController,
                topicId = topicId,
                onReturn = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onVocabularyClick = { vocabId ->
                    navController.navigate(Screen.Vocabulary.createRoute(vocabId))
                }
            )
        }

        composable(Screen.ResultScreen.route) {
            ResultScreen(navController = navController, onReturn = {navController.popBackStack()})
        }

        composable(Screen.DailyMission.route) {
            DailyMissionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToVocabulary = { vocabId ->
                    navController.navigate(Screen.Vocabulary.createRoute(vocabId))
                },
                onNavigateToFlashcard = { taskId ->
                    navController.navigate(Screen.DailyFlashcard.createRoute(taskId))
                }
            )
        }

        composable(
            route = Screen.DailyFlashcard.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            FlashcardScreen(
                topicId = "",
                onBack = { navController.popBackStack() },
                taskId = taskId
            )
        }
    }

    LaunchedEffect(startRoute) {
        val currentRoute = navController.currentDestination?.route
        if (currentRoute != startRoute) {
            navController.navigate(startRoute) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }
    }
}
