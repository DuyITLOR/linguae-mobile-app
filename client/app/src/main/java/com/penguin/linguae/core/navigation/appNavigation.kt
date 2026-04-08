package com.penguin.linguae.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.penguin.linguae.feature.learning.FlashcardScreen
import com.penguin.linguae.feature.learning.TopicScreen
import com.penguin.linguae.feature.learning.VocabularyListScreen
import com.penguin.linguae.feature.learning.VocabularyScreen
import com.penguin.linguae.feature.profile.ProfilePreviewData
import com.penguin.linguae.feature.profile.ProfileScreen

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
                onTopicClick = { topicId ->
                    navController.navigate(Screen.VocabularyList.createRoute(topicId))
                }
            )
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
            ProfileScreen(
                uiState = ProfilePreviewData.sample,
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

        composable(Screen.FavoriteVocabulary.route) {
            FavoriteScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.VocabularyList.route,
            arguments = listOf(
                navArgument("topicId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""

            VocabularyListScreen(
                topicId = topicId,
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

        composable(Screen.Flashcard.route) {
            FlashcardScreen (
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
                topicId = topicId,
                onReturn = { navController.popBackStack() }
            )
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
                onBack = { navController.popBackStack() },
                taskId = taskId
            )
        }
    }
}