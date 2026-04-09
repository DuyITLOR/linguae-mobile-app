package com.penguin.linguae.core.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Topic : Screen("topic_screen")
    object Progress : Screen("progress_screen")
    object Profile : Screen("profile_screen")

    object Login: Screen("login_screen")
    object Register: Screen("register_screen")
    object forogtPassword: Screen("forgot_password_screen")

    object FlashcardList : Screen("flashcard/list")

    object Flashcard : Screen("flashcard_screen")
    object FavoriteVocabulary: Screen("favorite")
    object DailyMission : Screen("daily_mission_screen")

    object DailyFlashcard : Screen("daily_flashcard_screen/{taskId}") {
        fun createRoute(taskId: String) = "daily_flashcard_screen/$taskId"
    }

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

    object Cloze: Screen("cloze_screen/{topicId}") {
        fun createRoute(topicId: String): String {
            return "cloze_screen/$topicId"
        }
    }
}