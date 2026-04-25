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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.PageBg
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.data.model.Toeic
import com.penguin.linguae.data.repository.ToeicRepository
import kotlinx.coroutines.launch

@Composable
fun ToeicMockTestListScreen(
    onBack: () -> Unit,
    onMockTestClick: (String) -> Unit = {}
) {
    val repository = remember { ToeicRepository() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var mockTests by remember { mutableStateOf<List<Toeic>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun fetchMockTests() {
        isLoading = true
        errorMessage = null

        try {
            repository.getAllToeic()
                .onSuccess { data ->
                    mockTests = data
                    errorMessage = null
                }
                .onFailure { throwable ->
                    throw throwable
                }
        } catch (throwable: Throwable) {
            val message = resolveToeicListErrorMessage(throwable)
            errorMessage = message
            snackbarHostState.showSnackbar(message)
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchMockTests()
    }

    Scaffold(
        containerColor = PageBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ToeicHeader(
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

            when {
                isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PurplePrimary)
                        }
                    }
                }

                errorMessage != null -> {
                    item {
                        ToeicListErrorState(
                            message = errorMessage.orEmpty(),
                            onRetry = {
                                coroutineScope.launch {
                                    fetchMockTests()
                                }
                            }
                        )
                    }
                }

                else -> {
                    itemsIndexed(mockTests) { index, mockTest ->
                        ToeicMockTestCard(
                            mockTest = mockTest,
                            order = index + 1,
                            onClick = { onMockTestClick(mockTest.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToeicListErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE9E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color(0xFFD64545),
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            text = "Kết nối có vấn đề",
            color = TextDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            color = Color(0xFF8A8A94),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Try again",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun resolveToeicListErrorMessage(throwable: Throwable): String {
    val rawMessage = throwable.message.orEmpty().lowercase()
    val isConnectionIssue = rawMessage.contains("failed to connect") ||
        rawMessage.contains("timeout") ||
        rawMessage.contains("unable to resolve host") ||
        rawMessage.contains("network")

    return if (isConnectionIssue) {
        "Vui lòng kiểm tra kết nối mạng và thử lại."
    } else {
        "Không thể tải danh sách đề thi. Vui lòng thử lại."
    }
}

@Composable
private fun ToeicHeader(
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
                        text = "PRACTICE SET",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "TOEIC Mock Tests",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
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
    mockTest: Toeic,
    order: Int,
    onClick: () -> Unit
) {
    val accent = PurplePrimary
    val cardBackground = listOf(Color.White, Color(0xFFF7F5FF))

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(cardBackground))
            .border(
                width = 1.dp,
                color = accent.copy(alpha = 0.2f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
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
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = order.toString(),
                        color = accent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
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
                            color = TextDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        StatusBadge("OPEN")
                    }
                    Text(
                        text = "Level ${mockTest.level}",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
            }
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
