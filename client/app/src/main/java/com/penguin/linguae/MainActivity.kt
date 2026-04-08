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
import com.penguin.linguae.core.navigation.AppNavigation
import com.penguin.linguae.core.navigation.Screen
import com.penguin.linguae.core.network.TokenManager
import com.penguin.linguae.core.network.UserManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(this)
        UserManager.init(this)
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

    val authScreen = listOf(
        Screen.Login.route,
        Screen.Register.route,
        Screen.forogtPassword.route
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if(currentRoute !in authScreen) {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    Destination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
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
}