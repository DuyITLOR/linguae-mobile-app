package com.penguin.linguae.feature.admin.topic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.*

@Composable
fun TopicContentMenuScreen(
    topicId: String,
    topicTitle: String,
    onBack: () -> Unit = {},
    onManageVocab: () -> Unit = {},
    onManageCloze: () -> Unit = {},
    onManageMatching: () -> Unit = {}
) {
    Scaffold(
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.verticalGradient(listOf(PurplePrimary, PurpleDark, PurpleDeep)))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp, vertical = 16.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SurfaceColor)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = topicTitle,
                            color = SurfaceColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Content Management",
                            color = SurfaceColor.copy(alpha = 0.75f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                MenuActionCard(
                    title = "Manage Vocabulary",
                    subtitle = "Add, edit, or remove words and examples",
                    icon = Icons.Default.MenuBook,
                    iconBackground = Color(0xFFECE9FF),
                    iconTint = Color(0xFF6D5CE7),
                    onClick = onManageVocab
                )

                MenuActionCard(
                    title = "Manage Cloze Practice",
                    subtitle = "Create fill-in-the-blank exercises",
                    icon = Icons.Default.School,
                    iconBackground = Color(0xFFE7F9EF),
                    iconTint = Color(0xFF27AE60),
                    onClick = onManageCloze
                )

                MenuActionCard(
                    title = "Manage Matching Practice",
                    subtitle = "Build two-column matching activities",
                    icon = Icons.Default.CompareArrows,
                    iconBackground = Color(0xFFECE9FF),
                    iconTint = Color(0xFF6D5CE7),
                    onClick = onManageMatching
                )
            }
        }
    }
}

@Composable
private fun MenuActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .background(iconBackground, CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowBack, // Will be rotated or just use a chevron
                contentDescription = null,
                tint = Color(0xFFB5B6C8),
                modifier = Modifier.size(20.dp).padding(start = 4.dp) // placeholder for chevron
            )
        }
    }
}
