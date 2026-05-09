package com.penguin.linguae.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.penguin.linguae.core.network.TokenManager
import com.penguin.linguae.core.network.UserManager
import com.penguin.linguae.data.model.ProfileUiState
import com.penguin.linguae.data.model.UserRole
import com.penguin.linguae.feature.admin.topic.ManageTopicScreen
import com.penguin.linguae.feature.admin.cloze.ManageClozeScreen
import com.penguin.linguae.feature.admin.matching.MatchingScreen
import com.penguin.linguae.feature.admin.vocabulary.ManageVocabularyScreen
import com.penguin.linguae.feature.admin.AdminScreen
import com.penguin.linguae.feature.admin.topic.TopicContentMenuScreen
import com.penguin.linguae.feature.admin.toeic.ToeicExamInfoScreen
import com.penguin.linguae.feature.admin.toeic.ToeicListScreen
import com.penguin.linguae.feature.admin.toeic.ToeicPart5EditorScreen
import com.penguin.linguae.feature.admin.toeic.ToeicPart6EditorScreen
import com.penguin.linguae.feature.admin.toeic.viewmodel.ToeicEditorViewModel
import com.penguin.linguae.feature.auth.ForgotPasswordScreen
import com.penguin.linguae.feature.auth.LoginScreen
import com.penguin.linguae.feature.auth.RegisterScreen
import com.penguin.linguae.feature.practice.cloze.ClozeScreen
import com.penguin.linguae.feature.practice.matching.MatchingScreen
import com.penguin.linguae.feature.dailyMission.DailyMissionScreen
import com.penguin.linguae.feature.favorite.FavoriteScreen
import com.penguin.linguae.feature.home.HomeScreen
import com.penguin.linguae.feature.learning.FlashcardListScreen
import com.penguin.linguae.feature.learning.SearchScreen
import com.penguin.linguae.feature.learning.FlashcardScreen
import com.penguin.linguae.feature.learning.TopicScreen
import com.penguin.linguae.feature.learning.VocabularyListScreen
import com.penguin.linguae.feature.learning.VocabularyScreen
import com.penguin.linguae.feature.practice.practiceResult.ResultScreen
import com.penguin.linguae.feature.practice.practiceTopic.PracticeTopicScreen
import com.penguin.linguae.feature.practice.topicPracticeConfig.TopicPracticeConfigScreen
import com.penguin.linguae.feature.profile.ProfileEditScreen
import com.penguin.linguae.feature.profile.NotificationSettingsScreen
import com.penguin.linguae.feature.profile.ProfileScreen
import com.penguin.linguae.feature.statistic.StatisticScreen
import com.penguin.linguae.feature.toeic.ToeicMockTestListScreen
import com.penguin.linguae.feature.toeic.ToeicTest

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val startRoute = resolveStartRoute()

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startRoute
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Admin.route) {
            val currentUser = UserManager.getUser()
            AdminScreen(
                currentUser = currentUser,
                onNavigateProfile = {
                    navController.navigate(Screen.Profile.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateAddTopic = {
                    navController.navigate(Screen.AddTopic.route)
                },
                onNavigateManageExam = {
                    navController.navigate(Screen.ToeicList.route)
                }
            )
        }

        composable(Screen.AddTopic.route) {
            ManageTopicScreen(
                viewModel = viewModel(),
                onBack = { navController.popBackStack() },
                onTopicClick = { topic ->
                    navController.navigate(Screen.TopicContentMenu.createRoute(topic.id, topic.title))
                }
            )
        }

        composable(
            route = Screen.TopicContentMenu.route,
            arguments = listOf(
                navArgument("topicId") { type = NavType.StringType },
                navArgument("topicTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            val topicTitle = backStackEntry.arguments?.getString("topicTitle") ?: ""
            TopicContentMenuScreen(
                topicId = topicId,
                topicTitle = topicTitle,
                onBack = { navController.popBackStack() },
                onManageVocab = {
                    navController.navigate(Screen.ManageVocabByTopic.createRoute(topicId, topicTitle))
                },
                onManageCloze = {
                    navController.navigate(Screen.ManageCloze.createRoute(topicId, topicTitle))
                },
                onManageMatching = {
                    navController.navigate(Screen.ManageMatching.createRoute(topicId, topicTitle))
                }
            )
        }

        composable(Screen.ToeicList.route) {
            ToeicListScreen(
                onBack = { navController.popBackStack() },
                onAddExam = {
                    navController.navigate(Screen.ToeicExamInfoEditor.createRoute())
                },
                onEditExam = { toeicId ->
                    navController.navigate(Screen.ToeicExamInfoEditor.createRoute(toeicId))
                }
            )
        }

        composable(
            route = Screen.ToeicExamInfoEditor.route,
            arguments = listOf(
                navArgument("toeicId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val toeicId = backStackEntry.arguments?.getString("toeicId")
            val toeicListEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.ToeicList.route)
            }
            val editorViewModel: ToeicEditorViewModel = viewModel(toeicListEntry)
            LaunchedEffect(toeicId) {
                if (toeicId.isNullOrBlank()) {
                    editorViewModel.startCreateDraft()
                } else {
                    editorViewModel.loadToeicDraft(toeicId)
                }
            }
            ToeicExamInfoScreen(
                onBack = {
                    editorViewModel.clearAllDrafts()
                    navController.navigate(Screen.ToeicList.route) {
                        popUpTo(Screen.ToeicList.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigatePart5 = {
                    navController.navigate(Screen.ToeicPart5Editor.createRoute(toeicId)) {
                        launchSingleTop = true
                    }
                },
                onNavigatePart6 = {
                    navController.navigate(Screen.ToeicPart6Editor.createRoute(toeicId)) {
                        launchSingleTop = true
                    }
                },
                onSaveExam = {
                    navController.navigate(Screen.ToeicList.route) {
                        popUpTo(Screen.ToeicList.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                viewModel = editorViewModel
            )
        }

        composable(
            route = Screen.ToeicPart5Editor.route,
            arguments = listOf(
                navArgument("toeicId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val toeicId = backStackEntry.arguments?.getString("toeicId")
            val toeicListEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.ToeicList.route)
            }
            val editorViewModel: ToeicEditorViewModel = viewModel(toeicListEntry)
            LaunchedEffect(toeicId) {
                if (toeicId.isNullOrBlank()) {
                    editorViewModel.startCreateDraft()
                } else {
                    editorViewModel.loadToeicDraft(toeicId)
                }
            }
            ToeicPart5EditorScreen(
                onBack = {
                    editorViewModel.clearAllDrafts()
                    navController.navigate(Screen.ToeicList.route) {
                        popUpTo(Screen.ToeicList.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                toeicId = toeicId,
                onInfoClick = {
                    navController.navigate(Screen.ToeicExamInfoEditor.createRoute(toeicId)) {
                        launchSingleTop = true
                    }
                },
                onPart6Click = {
                    navController.navigate(Screen.ToeicPart6Editor.createRoute(toeicId)) {
                        launchSingleTop = true
                    }
                },
                onSaveExam = {
                    navController.navigate(Screen.ToeicList.route) {
                        popUpTo(Screen.ToeicList.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                viewModel = editorViewModel
            )
        }

        composable(
            route = Screen.ToeicPart6Editor.route,
            arguments = listOf(
                navArgument("toeicId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val toeicId = backStackEntry.arguments?.getString("toeicId")
            val toeicListEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.ToeicList.route)
            }
            val editorViewModel: ToeicEditorViewModel = viewModel(toeicListEntry)
            LaunchedEffect(toeicId) {
                if (toeicId.isNullOrBlank()) {
                    editorViewModel.startCreateDraft()
                } else {
                    editorViewModel.loadToeicDraft(toeicId)
                }
            }
            ToeicPart6EditorScreen(
                onBack = {
                    editorViewModel.clearAllDrafts()
                    navController.navigate(Screen.ToeicList.route) {
                        popUpTo(Screen.ToeicList.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                toeicId = toeicId,
                onInfoClick = {
                    navController.navigate(Screen.ToeicExamInfoEditor.createRoute(toeicId)) {
                        launchSingleTop = true
                    }
                },
                onPart5Click = {
                    navController.navigate(Screen.ToeicPart5Editor.createRoute(toeicId)) {
                        launchSingleTop = true
                    }
                },
                onSaveExam = {
                    navController.navigate(Screen.ToeicList.route) {
                        popUpTo(Screen.ToeicList.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                viewModel = editorViewModel
            )
        }

        composable(
            route = Screen.ManageVocabByTopic.route,
            arguments = listOf(
                navArgument("topicId") { type = NavType.StringType },
                navArgument("topicTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            val topicTitle = backStackEntry.arguments?.getString("topicTitle") ?: ""
            ManageVocabularyScreen(
                topicId = topicId,
                topicTitle = topicTitle,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ManageCloze.route,
            arguments = listOf(
                navArgument("topicId") { type = NavType.StringType },
                navArgument("topicTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            val topicTitle = backStackEntry.arguments?.getString("topicTitle") ?: ""
            ManageClozeScreen(
                topicId = topicId,
                topicTitle = topicTitle,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ManageMatching.route,
            arguments = listOf(
                navArgument("topicId") { type = NavType.StringType },
                navArgument("topicTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            val topicTitle = backStackEntry.arguments?.getString("topicTitle") ?: ""
            MatchingScreen(
                topicId = topicId,
                topicTitle = topicTitle,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Topic.route) {
            TopicScreen(
                onTopicClick = { topicId, title ->
                    navController.navigate(Screen.VocabularyList.createRoute(topicId, title))
                }
            )
        }

        composable(Screen.Progress.route) {
            StatisticScreen()
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel(),
                onNavigateHome = {
                    navController.navigate(resolveAuthenticatedRoute()) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateForgotPassword = {
                    navController.navigate(Screen.forogtPassword.route)
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

        composable(Screen.forogtPassword.route) {
            ForgotPasswordScreen(
                viewModel = viewModel(),
                onNavigateLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            val currentUser = UserManager.getUser()
            ProfileScreen(
                uiState = ProfileUiState(
                    fullName = currentUser?.fullName ?: "No name",
                    email = currentUser?.email ?: "No email",
                    avatarUrl = currentUser?.avatarUrl
                ),
                onNavigateEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateNotificationSettings = {
                    navController.navigate(Screen.NotificationSettings.route)
                },
                onLogout = {
                    TokenManager.clear()
                    UserManager.clearUser()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.EditProfile.route) {
            val currentUser = UserManager.getUser()
            ProfileEditScreen(
                uiState = ProfileUiState(
                    fullName = currentUser?.fullName ?: "No name",
                    email = currentUser?.email ?: "No email",
                    avatarUrl = currentUser?.avatarUrl
                ),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.NotificationSettings.route) {
            NotificationSettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FlashcardList.route) {
            FlashcardListScreen(
                onTopicClick = { topicId ->
                    navController.navigate(Screen.Flashcard.createRoute(topicId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ToeicMockTestList.route) {
            ToeicMockTestListScreen(
                onBack = { navController.popBackStack() },
                onMockTestClick = { toeicId ->
                    navController.navigate(Screen.ToeicTest.createRoute(toeicId))
                }
            )
        }

        composable(
            route = Screen.ToeicTest.route,
            arguments = listOf(
                navArgument("toeicId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val toeicId = backStackEntry.arguments?.getString("toeicId") ?: ""
            ToeicTest(
                onBack = { navController.popBackStack() },
                toeicId = toeicId
            )
        }

        composable(Screen.FavoriteVocabulary.route) {
            FavoriteScreen(
                onBack = { navController.popBackStack() },
                onVocabularyClick = { vocabId ->
                    navController.navigate(Screen.Vocabulary.createRoute(vocabId))
                }
            )
        }

        composable(
            route = Screen.VocabularyList.route,
            arguments = listOf(
                navArgument("topicId") {
                    type = NavType.StringType
                },
                navArgument("title") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""

            VocabularyListScreen(
                topicId = topicId,
                title = title,
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Vocabulary.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val vocabId = backStackEntry.arguments?.getString("id") ?: ""
            VocabularyScreen(
                vocabId = vocabId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Flashcard.route,
            arguments = listOf(
                navArgument("topicId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""

            FlashcardScreen(
                topicId = topicId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Cloze.route,
            arguments = listOf(
                navArgument("topicId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            ClozeScreen(
                navController = navController,
                topicId = topicId,
                onReturn = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Matching.route,
            arguments = listOf(
                navArgument("topicId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            MatchingScreen(
                navController = navController,
                topicId = topicId,
                onReturn = { navController.popBackStack() }
            )
        }

        composable(Screen.ResultScreen.route) {
            ResultScreen(navController = navController, onReturn = { navController.popBackStack() })
        }

        composable (Screen.PracticeTopic.route) {
            PracticeTopicScreen(
                navController = navController,
                onReturn = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.TopicPracticeConfig.route,
            arguments = listOf(
                navArgument("topicId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            TopicPracticeConfigScreen(
                topicId = topicId,
                navController = navController,
                onReturn = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onVocabularyClick = { vocabId ->
                    navController.navigate(Screen.Vocabulary.createRoute(vocabId))
                }
            )
        }


        composable(Screen.DailyMission.route) {
            DailyMissionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToVocabulary = { vocabId ->
                    navController.navigate(Screen.Vocabulary.createRoute(vocabId))
                },
                onNavigateToFlashcard = { taskId ->
                    navController.navigate(Screen.DailyFlashcard.createRoute(taskId))
                }
            )
        }

        composable(
            route = Screen.DailyFlashcard.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            FlashcardScreen(
                topicId = "",
                onBack = { navController.popBackStack() },
                taskId = taskId
            )
        }
    }
}

private fun resolveStartRoute(): String {
    if (TokenManager.getToken() == null) {
        return Screen.Login.route
    }

    return resolveAuthenticatedRoute()
}

private fun resolveAuthenticatedRoute(): String {
    return when (UserManager.getUser()?.role) {
        UserRole.ADMIN -> Screen.Admin.route
        UserRole.USER -> Screen.Home.route
        null -> Screen.Login.route
    }
}
