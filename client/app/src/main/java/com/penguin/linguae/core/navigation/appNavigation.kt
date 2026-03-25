package com.penguin.linguae.core.navigation

import com.penguin.linguae.feature.auth.LoginScreen
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


        composable(Screen.Home.route) { HomeScreen() }
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
                        popUpTo("login_screen") { inclusive = true }
                    }
                },
                onNavigateRegister = {
                    navController.navigate("register_screen")
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


        composable(route = Screen.Vocabulary.route, arguments = listOf(navArgument("topicId") { type =
            NavType.IntType })) {
            backStackEntry ->
                val topicId = backStackEntry.arguments?.getInt("topicId") ?: 0

//        composable(Screen.Progress.route) { ProgressScreen() }
//        composable(Screen.Profile.route) { ProfileScreen() }
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

        composable(Screen.Flashcard.route) {
            FlashcardScreen (
                onBack = { navController.popBackStack() }
            )
        }
            }
    }
}
