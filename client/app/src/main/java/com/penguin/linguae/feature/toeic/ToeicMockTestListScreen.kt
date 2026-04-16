package com.penguin.linguae.feature.toeic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.PageBg
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray

private data class ToeicProgressUi(
    val title: String,
    val completedUnits: Int,
    val totalUnits: Int,
    val progress: Float
)

private data class ToeicMockTestUi(
    val id: String,
    val order: String,
    val title: String,
    val subtitle: String,
    val duration: String,
    val progress: Float,
    val status: ToeicMockTestStatus
)

private enum class ToeicMockTestStatus {
    Completed,
    Current,
    Locked
}

@Composable
fun ToeicMockTestListScreen(
    onBack: () -> Unit,
    onMockTestClick: (String) -> Unit = {}
) {
    val progress = remember {
        ToeicProgressUi(
            title = "TOEIC Expert",
            completedUnits = 1,
            totalUnits = 10,
            progress = 0.1f
        )
    }
    val mockTests = remember {
        listOf(
            ToeicMockTestUi(
                id = "toeic_mock_1",
                order = "01",
                title = "TOEIC Mock Test 1",
                subtitle = "Foundational Listening & Reading",
                duration = "45 min",
                progress = 1f,
                status = ToeicMockTestStatus.Completed
            ),
            ToeicMockTestUi(
                id = "toeic_mock_2",
                order = "02",
                title = "TOEIC Mock Test 2",
                subtitle = "Workplace Communication Contexts",
                duration = "45 min",
                progress = 0.65f,
                status = ToeicMockTestStatus.Current
            ),
            ToeicMockTestUi(
                id = "toeic_mock_3",
                order = "03",
                title = "TOEIC Mock Test 3",
                subtitle = "Intermediate Business Analysis",
                duration = "45 min",
                progress = 0f,
                status = ToeicMockTestStatus.Locked
            ),
            ToeicMockTestUi(
                id = "toeic_mock_4",
                order = "04",
                title = "TOEIC Mock Test 4",
                subtitle = "Advanced Strategic Vocabulary",
                duration = "45 min",
                progress = 0f,
                status = ToeicMockTestStatus.Locked
            )
        )
    }

    Scaffold(containerColor = PageBg) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ToeicHeader(
                    progress = progress,
                    totalTests = mockTests.size,
                    onBack = onBack
                )
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Available Tests",
                        color = TextDark,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TestCountBadge(count = mockTests.size)
                }
            }

            items(mockTests) { mockTest ->
                ToeicMockTestCard(
                    mockTest = mockTest,
                    onClick = { onMockTestClick(mockTest.id) }
                )
            }
        }
    }
}

@Composable
private fun ToeicHeader(
    progress: ToeicProgressUi,
    totalTests: Int,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF8F6FF),
                        Color(0xFFF2EEFF)
                    )
                )
            )
            .padding(start = 20.dp, end = 20.dp, top = 44.dp, bottom = 12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextDark
                    )
                }
                Text(
                    text = "Practice Exams",
                    color = TextDark,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4D5AE8),
                                PurplePrimary,
                                PurpleDark
                            )
                        )
                    )
                    .padding(22.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "CURRENT PROGRESS",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = progress.title,
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Completed ${progress.completedUnits}/${progress.totalUnits} Units",
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(progress.progress * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progress.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.24f)
                    )
                    TestCountBadge(
                        count = totalTests,
                        modifier = Modifier.align(Alignment.Start),
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TestCountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFE8E0FF),
    contentColor: Color = PurplePrimary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = "$count total",
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ToeicMockTestCard(
    mockTest: ToeicMockTestUi,
    onClick: () -> Unit
) {
    val accent = when (mockTest.status) {
        ToeicMockTestStatus.Completed -> Color(0xFF33C759)
        ToeicMockTestStatus.Current -> PurplePrimary
        ToeicMockTestStatus.Locked -> Color(0xFFD8DAE6)
    }
    val cardBackground = when (mockTest.status) {
        ToeicMockTestStatus.Completed -> listOf(Color.White, Color(0xFFF7F5FF))
        ToeicMockTestStatus.Current -> listOf(Color(0xFFE8E4FF), Color(0xFFF0EDFF))
        ToeicMockTestStatus.Locked -> listOf(Color(0xFFF3F4F8), Color(0xFFF7F8FB))
    }
    val enabled = mockTest.status != ToeicMockTestStatus.Locked

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(cardBackground))
            .border(
                width = 1.dp,
                color = accent.copy(alpha = if (enabled) 0.2f else 0.12f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = if (enabled) 0.15f else 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (mockTest.status == ToeicMockTestStatus.Locked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF)
                        )
                    } else {
                        Text(
                            text = mockTest.order,
                            color = accent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mockTest.title,
                            color = if (enabled) TextDark else TextGray,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        when (mockTest.status) {
                            ToeicMockTestStatus.Completed -> CompletedBadge()
                            ToeicMockTestStatus.Current -> StatusBadge("CURRENT")
                            ToeicMockTestStatus.Locked -> Unit
                        }
                    }
                    Text(
                        text = mockTest.subtitle,
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
            }

            when (mockTest.status) {
                ToeicMockTestStatus.Completed -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF33C759),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "2/2 skills",
                        color = PurplePrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = mockTest.duration,
                        color = Color(0xFFE25578),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                ToeicMockTestStatus.Current -> {
                    Text(
                        text = "Progress",
                        color = TextDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    LinearProgressIndicator(
                        progress = { mockTest.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = PurplePrimary,
                        trackColor = PurplePrimary.copy(alpha = 0.18f)
                    )
                    Text(
                        text = "${(mockTest.progress * 100).toInt()}%",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.End)
                    )
                }

                ToeicMockTestStatus.Locked -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFB3B7C6),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (mockTest.order == "03") {
                            "Complete test 2 to unlock"
                        } else {
                            "Level 5 required"
                        },
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (enabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = accent
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(PurplePrimary.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = PurplePrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CompletedBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(3) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFC83D),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
