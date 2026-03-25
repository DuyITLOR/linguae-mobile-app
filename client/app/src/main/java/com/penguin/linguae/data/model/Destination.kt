package com.penguin.linguae.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.penguin.linguae.core.navigation.Screen

enum class Destination(
    val screen: Screen,
    val label: String,
    val description: String,
    val icon: ImageVector
) {
    Home(Screen.Home, "Trang chủ", "Home", Icons.Default.Home),
    Topic(Screen.Topic, "Chủ đề", "Topic", Icons.Default.Book),
    Progress(Screen.Progress, "Tiến độ", "Progress", Icons.Default.BarChart),
    Profile(Screen.Profile, "Hồ sơ", "Profile", Icons.Default.Person);

    val route get() = screen.route
}