package com.penguin.linguae.core.navigation

import com.penguin.linguae.feature.auth.LoginScreen
import com.penguin.linguae.core.navigation.Screen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.penguin.linguae.feature.auth.RegisterScreen
import com.penguin.linguae.feature.home.HomeScreen
import com.penguin.linguae.feature.learning.FlashcardScreen
import com.penguin.linguae.feature.learning.TopicScreen
import com.penguin.linguae.feature.learning.VocabularyScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = Screen.Login.route
    ) {
        // 1. Màn hình Home
        composable(Screen.Home.route) { HomeScreen() }

        // 2. Màn hình Topic
        composable(Screen.Topic.route) {
            TopicScreen(
                onTopicClick = { topicId ->
                    navController.navigate(Screen.Vocabulary.createRoute(topicId))
                }
            )
        }

        // 3. Màn hình Login
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel(),
                onNavigateHome = {
                    navController.navigate(Screen.Topic.route) {
                        // Xóa sạch stack cũ để không quay lại màn hình Login được nữa
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // 4. Màn hình Register
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = viewModel(),
                onNavigateLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 5. Màn hình Vocabulary
        composable(
            route = Screen.Vocabulary.route,
            arguments = listOf(navArgument("topicId") { type = NavType.IntType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt("topicId") ?: 0
            VocabularyScreen(
                topicId = topicId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 6. Màn hình Flashcard
        composable(Screen.Flashcard.route) {
            FlashcardScreen (
                onBack = { navController.popBackStack() }
            )
        }
    }
}
