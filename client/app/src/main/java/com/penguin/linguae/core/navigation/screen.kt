package com.penguin.linguae.core.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Topic : Screen("topic_screen")
    object Progress : Screen("progress_screen")
    object Profile : Screen("profile_screen")

    object Login: Screen("login_screen")
    object Register: Screen("register_screen")

    object forogtPassword: Screen("forgot_password_screen")

    object Flashcard : Screen("flashcard_screen")
    object FavoriteVocabulary: Screen("favorite")

    object VocabularyList: Screen("vocabulary_screen/{topicId}") {
        fun createRoute(topicId: String): String {
            return "vocabulary_screen/$topicId"
        }
    }

    object Vocabulary: Screen("vocabulary/{id}") {
        fun createRoute(id: String): String {
            return "vocabulary/$id"
        }
    }
}