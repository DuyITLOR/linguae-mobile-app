package com.penguin.linguae.ui.navigation

sealed class Screen(val route: String) {
    object Topic: Screen("topic_screen")

    object Vocabulary: Screen("vocabulary_screen/{topicId}") {
        fun createRoute(topicId: Int): String {
            return "vocabulary_screen/$topicId"
        }
    }
}