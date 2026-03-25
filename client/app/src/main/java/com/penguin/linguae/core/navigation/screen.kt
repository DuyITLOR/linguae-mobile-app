package com.penguin.linguae.core.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Topic : Screen("topic_screen")
    object Progress : Screen("progress_screen")
    object Profile : Screen("profile_screen")

    object Login: Screen("login_screen")

    object Register: Screen("register_screen")

    object Flashcard : Screen("flashcard_screen")

    object Vocabulary: Screen("vocabulary_screen/{topicId}") {
        fun createRoute(topicId: Int): String {
            return "vocabulary_screen/$topicId"
        }
    }
}