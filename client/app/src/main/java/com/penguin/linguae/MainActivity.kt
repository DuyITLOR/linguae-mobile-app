package com.penguin.linguae

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.penguin.linguae.data.model.Destination
import com.penguin.linguae.data.model.UserRole
import com.penguin.linguae.core.navigation.AppNavigation
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.core.network.TokenManager
import com.penguin.linguae.core.network.UserManager
import com.penguin.linguae.core.notification.LearningReminderScheduler
import com.penguin.linguae.feature.chat.ChatScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(this)
        UserManager.init(this)
        LearningReminderScheduler.ensureChannel(this)
        setContent{
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = Destination.entries.map { it.route }
    val authRoutes = setOf(
        Screen.Login.route,
        Screen.Register.route,
        Screen.forogtPassword.route
    )
    val isAdminRoute =
        currentRoute == Screen.Admin.route || currentRoute?.startsWith("admin/") == true
    val isAdminProfileRoute =
        currentRoute == Screen.Profile.route && UserManager.getUser()?.role == UserRole.ADMIN
    val shouldShowChat =
        currentRoute !in authRoutes &&
            !isAdminRoute &&
            !isAdminProfileRoute

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (currentRoute in bottomBarRoutes && !isAdminProfileRoute) {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    Destination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                if (destination == Destination.Home) {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                } else {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(destination.icon, contentDescription = destination.description)
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(contentPadding)
        )
    }
    if (shouldShowChat) {
        ChatScreen()
    }
}
