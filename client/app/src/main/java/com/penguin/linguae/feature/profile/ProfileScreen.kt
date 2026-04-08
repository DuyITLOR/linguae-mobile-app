package com.penguin.linguae.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.PageBg
import com.penguin.linguae.core.ui.theme.PurpleLight
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.feature.profile.component.ProfileActionCard
import com.penguin.linguae.feature.profile.component.ProfileHeroHeader
import com.penguin.linguae.feature.profile.component.ProfileLogoutDialog
import com.penguin.linguae.feature.profile.component.ProfileSectionLabel

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    Surface(
        color = PageBg,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                ProfileHeroHeader(uiState = uiState)
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    ProfileSectionLabel(text = "Không gian cá nhân")
                    ProfileActionCard(
                        icon = Icons.Default.Edit,
                        iconTint = Color(0xFFFF8A4C),
                        iconBackground = Color(0xFFFFF1E8),
                        title = "Chỉnh sửa hồ sơ",
                        subtitle = "Khung UI cho tên hiển thị, avatar và mục tiêu học."
                    )
                    ProfileActionCard(
                        icon = Icons.Default.Settings,
                        iconTint = PurplePrimary,
                        iconBackground = PurpleLight,
                        title = "Cài đặt",
                        subtitle = "Khu vực đặt thông báo, ngôn ngữ và trải nghiệm của app."
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),

                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextDark,
                        containerColor = Color.Red.copy(alpha = 0.5f)
                    )

                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "Đăng xuất",
                        fontSize = 16.sp,
                        color = TextDark,
                    )
                }
            }
        }

        ProfileLogoutDialog(
            visible = showLogoutDialog,
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        uiState = ProfilePreviewData.sample,
        onLogout = {}
    )
}
