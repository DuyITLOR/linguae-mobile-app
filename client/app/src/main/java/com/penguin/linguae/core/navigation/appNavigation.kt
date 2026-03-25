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
import com.penguin.linguae.feature.home.HomeScreen
import com.penguin.linguae.feature.learning.FlashcardScreen
import com.penguin.linguae.feature.learning.TopicScreen
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
            HomeScreen()
        }

        composable(Screen.Topic.route) {
            TopicScreen(
                onTopicClick = { topicId ->
                    navController.navigate(Screen.Vocabulary.createRoute(topicId))
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel(),
                onNavigateHome = {
                    navController.navigate(Screen.Topic.route) {
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

        composable(
            route = Screen.Vocabulary.route,
            arguments = listOf(
                navArgument("topicId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""

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