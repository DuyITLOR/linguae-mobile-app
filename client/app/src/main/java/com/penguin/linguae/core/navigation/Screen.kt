package com.penguin.linguae.core.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Admin : Screen("admin_screen")
    object Topic : Screen("topic_screen")
    object Progress : Screen("progress_screen")
    object Profile : Screen("profile_screen")
    object EditProfile : Screen("edit_profile_screen")

    object Login: Screen("login_screen")
    object Register: Screen("register_screen")
    object forogtPassword: Screen("forgot_password_screen")

    object FlashcardList : Screen("flashcard/list")
    object ToeicMockTestList : Screen("toeic/mock-test-list")
    object ToeicTest: Screen("toeic/test/{toeicId}") {
        fun createRoute(toeicId: String) = "toeic/test/${toeicId}"
    }

    object Flashcard : Screen("flashcard/{topicId}/practice") {
        fun createRoute(topicId: String) = "flashcard/${topicId}/practice"
    }
    object FavoriteVocabulary: Screen("favorite")
    object DailyMission : Screen("daily_mission_screen")

    object DailyFlashcard : Screen("daily_flashcard_screen/{taskId}") {
        fun createRoute(taskId: String) = "daily_flashcard_screen/$taskId"
    }

    object VocabularyList: Screen("vocabulary_screen/{topicId}/{title}") {
        fun createRoute(topicId: String, title: String): String {
            return "vocabulary_screen/$topicId/${android.net.Uri.encode(title)}"
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

    object ResultScreen: Screen("result_screen")
    object PracticeTopic: Screen("practice_topic_screen")
    object TopicPracticeConfig: Screen("topic_practice_config/{topicId}") {
        fun createRoute(topicId: String) = "topic_practice_config/$topicId"
    }

    object Search : Screen("search_screen")

    object AddTopic : Screen("admin/add_topic")

    object ManageVocabByTopic : Screen("admin/topic/{topicId}/vocabulary/{topicTitle}") {
        fun createRoute(topicId: String, topicTitle: String) =
            "admin/topic/$topicId/vocabulary/${android.net.Uri.encode(topicTitle)}"
    }

    object ManageCloze : Screen("admin/topic/{topicId}/cloze/{topicTitle}") {
        fun createRoute(topicId: String, topicTitle: String) =
            "admin/topic/$topicId/cloze/${android.net.Uri.encode(topicTitle)}"
    }

    object TopicContentMenu : Screen("admin/topic/{topicId}/menu/{topicTitle}") {
        fun createRoute(topicId: String, topicTitle: String) =
            "admin/topic/$topicId/menu/${android.net.Uri.encode(topicTitle)}"
    }
    object ToeicList : Screen("admin/toeic_list")
}
