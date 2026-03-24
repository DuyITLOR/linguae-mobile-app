package com.penguin.linguae.core.navigation

sealed class Screen(val route: String) {
    object Topic: Screen("topic_screen")

    object Login: Screen("login_screen")

    object Flashcard : Screen("flashcard_screen")

    object Vocabulary: Screen("vocabulary_screen/{topicId}") {
        fun createRoute(topicId: Int): String {
            return "vocabulary_screen/$topicId"
        }
    }
}