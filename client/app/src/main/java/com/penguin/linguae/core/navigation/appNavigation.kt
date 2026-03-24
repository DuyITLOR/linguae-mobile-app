package com.penguin.linguae.core.navigation

import LoginScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.penguin.linguae.feature.learning.FlashcardScreen
import com.penguin.linguae.feature.learning.TopicScreen
import com.penguin.linguae.feature.learning.VocabularyScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login_screen") {
        composable(route = Screen.Topic.route) {
            TopicScreen(
                onTopicClick = { topicId ->
                    navController.navigate(Screen.Vocabulary.createRoute(topicId))
                }
            )
        }

        composable("login_screen") {
            LoginScreen(
                viewModel = viewModel(),
                onNavigateHome = {
                    navController.navigate(Screen.Topic.route) {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }
            )
        }


        composable(route = Screen.Vocabulary.route, arguments = listOf(navArgument("topicId") { type =
            NavType.IntType })) {
            backStackEntry ->
                val topicId = backStackEntry.arguments?.getInt("topicId") ?: 0

            VocabularyScreen(
                topicId = topicId,
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