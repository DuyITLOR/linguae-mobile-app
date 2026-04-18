package com.penguin.linguae.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.data.model.User
import coil.compose.AsyncImage

@Composable
fun AdminScreen(
    modifier: Modifier = Modifier,
    currentUser: User? = null,
    onNavigateProfile: () -> Unit = {},
    onNavigateAddTopic: () -> Unit = {},
    onNavigateAddVocabulary: () -> Unit = {},
) {
    Surface(
        color = AppBackground,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            item {
                AdminHeader(
                    adminName = currentUser.displayName(),
                    avatarUrl = currentUser?.avatarUrl,
                    onNavigateProfile = onNavigateProfile
                )
            }
            item {
                TotalUsersCard(totalUsers = "24.8k", growth = "+12% this month")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminMetricCard(
                        title = "12.4k",
                        subtitle = "Total Words",
                        icon = Icons.Default.MenuBook,
                        iconBackground = Color(0xFFF0EDFF),
                        iconTint = Color(0xFF5E52D9),
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "86",
                        subtitle = "Active Topics",
                        icon = Icons.Default.TrendingUp,
                        iconBackground = Color(0xFFFFEEF6),
                        iconTint = Color(0xFFD54A8A),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                AdminMetricCard(
                    title = "412",
                    subtitle = "Total Exams",
                    icon = Icons.Default.School,
                    iconBackground = Color(0xFFEFF4FF),
                    iconTint = Color(0xFF476DE8),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Text(
                    text = "Management Hub",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF22243A),
                    fontWeight = FontWeight.SemiBold
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminActionItem(
                        title = "Add Word",
                        subtitle = "Update vocabulary database",
                        icon = Icons.Default.MenuBook,
                        onClick = onNavigateAddVocabulary
                    )
                    AdminActionItem(
                        title = "Add Topic",
                        subtitle = "Create new learning category",
                        icon = Icons.Default.Add,
                        onClick = onNavigateAddTopic
                    )
                    AdminActionItem(
                        title = "Upload Exam",
                        subtitle = "Import assessment modules",
                        icon = Icons.Default.FileUpload
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AdminHeader(
    adminName: String,
    avatarUrl: String?,
    onNavigateProfile: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Hello, $adminName",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E2C)
            )
            Text(
                text = "System is running smoothly today.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8D8EA3)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .background(color = Color(0xFFECE9FF), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF6D5CE7),
                    modifier = Modifier.size(18.dp)
                )
            }

            ProfileShortcut(
                avatarUrl = avatarUrl,
                onClick = onNavigateProfile
            )
        }
    }
}

@Composable
private fun ProfileShortcut(
    avatarUrl: String?,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1EEFF))
        ) {
            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Open profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Open profile",
                    tint = Color(0xFF5F55DB),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun TotalUsersCard(totalUsers: String, growth: String) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4F5DE9), Color(0xFF6E46F2))
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(brush = gradient)
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TOTAL USERS",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0x99FFFFFF),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = totalUsers,
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0x26FFFFFF),
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = growth,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBackground: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(30.dp)
                    .background(iconBackground, CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF25253A)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8A8A9D)
            )
        }
    }
}

@Composable
private fun AdminActionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .background(color = Color(0xFFF1EEFF), shape = CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF5F55DB),
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF2B2C40),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9A9AAF)
                )
            }

            Text(
                text = ">",
                color = Color(0xFFB5B6C8),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminScreenPreview() {
    AdminScreen(
        currentUser = User(
            id = "1",
            email = "admin@linguae.app",
            fullName = "Nguyen Admin",
            avatarUrl = null
        )
    )
}

private fun User?.displayName(): String {
    val fullName = this?.fullName?.trim().orEmpty()
    if (fullName.isNotEmpty()) {
        return fullName
    }

    val emailName = this?.email
        ?.substringBefore("@")
        ?.trim()
        .orEmpty()
    if (emailName.isNotEmpty()) {
        return emailName
    }

    return "Admin"
}
