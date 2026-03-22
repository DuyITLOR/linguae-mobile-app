package com.penguin.linguae.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.penguin.linguae.ui.screen.TopicScreen
import com.penguin.linguae.ui.screen.VocabularyScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Topic.route) {
        composable(route = Screen.Topic.route) {
            TopicScreen(
                onTopicClick = { topicId ->
                    navController.navigate(Screen.Vocabulary.createRoute(topicId))
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
    }
}