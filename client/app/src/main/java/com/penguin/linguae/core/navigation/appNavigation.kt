package com.penguin.linguae.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.penguin.linguae.feature.auth.LoginScreen
import com.penguin.linguae.feature.auth.RegisterScreen
import com.penguin.linguae.feature.favorite.FavoriteScreen
import com.penguin.linguae.feature.home.HomeScreen
import com.penguin.linguae.feature.learning.FlashcardScreen
import com.penguin.linguae.feature.learning.TopicScreen
import com.penguin.linguae.feature.learning.VocabularyListScreen
import com.penguin.linguae.feature.learning.VocabularyScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = Screen.Login.route
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
    }
}