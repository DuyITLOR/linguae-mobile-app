package com.penguin.linguae.feature.admin.toeic

import android.widget.Toast

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.data.model.Toeic
import com.penguin.linguae.feature.admin.toeic.viewmodel.ToeicListViewModel

private val HeaderGradientStart = Color(0xFF4F5DE9)
private val HeaderGradientEnd = Color(0xFF6E46F2)
private val CardSurface = Color.White
private val SubtleText = Color(0xFF8D8EA3)
private val DividerLight = Color(0xFFF0EEFF)

@Composable
fun ToeicListScreen(
    onBack: () -> Unit,
    onAddExam: () -> Unit = {},
    onEditExam: (String) -> Unit = {},
    viewModel: ToeicListViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadToeicTests()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Surface(
        color = AppBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // ── Top bar ─────────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Manage Exams",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )
                    IconButton(
                        onClick = onAddExam,
                        modifier = Modifier
                            .background(PurplePrimary, CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add exam",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ── Gradient hero banner ───────────────────────────────────────
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(HeaderGradientStart, HeaderGradientEnd)
                                )
                            )
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Exam Library",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Organize, edit, and publish your TOEIC learning materials.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.72f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Search bar ─────────────────────────────────────────────────
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search exams...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SubtleText
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = SubtleText,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(14.dp),
                            ambientColor = Color(0x0D000000)
                        ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextDark),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = CardSurface,
                        focusedContainerColor = CardSurface,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = PurplePrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── Content area ────────────────────────────────────────────────
                when {
                    viewModel.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PurplePrimary)
                        }
                    }

                    viewModel.error != null -> {
                        val errorMessage = resolveToeicAdminErrorMessage(viewModel.error)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color(0xFFFFE9E9), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = null,
                                        tint = Color(0xFFE53935),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Text(
                                    text = "Kết nối có vấn đề",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDark
                                )
                                Text(
                                    text = errorMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SubtleText,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = PurplePrimary),
                                    modifier = Modifier.clickable { viewModel.retry() }
                                ) {
                                    Text(
                                        "Retry",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        modifier = Modifier.padding(
                                            horizontal = 20.dp,
                                            vertical = 10.dp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    viewModel.toeicTests.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No exams available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SubtleText
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(viewModel.toeicTests.filter { exam ->
                                searchQuery.isEmpty() || exam.title.contains(
                                    searchQuery,
                                    ignoreCase = true
                                )
                            }) { exam ->
                                ExamCard(
                                    exam = exam,
                                    onEdit = { onEditExam(exam.id) },
                                    onDelete = {
                                        viewModel.deleteToeic(exam.id) {
                                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (viewModel.isDeleting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PurplePrimary)
                }
            }
        }
    }
}

private fun resolveToeicAdminErrorMessage(rawError: String?): String {
    val message = rawError.orEmpty().lowercase()
    val isConnectionIssue = message.contains("failed to connect") ||
        message.contains("timeout") ||
        message.contains("unable to resolve host") ||
        message.contains("network")

    return if (isConnectionIssue) {
        "Vui lòng kiểm tra kết nối mạng và thử lại."
    } else {
        "Không thể tải danh sách đề thi. Vui lòng thử lại."
    }
}

// ── Exam card ────────────────────────────────────────────────────────────────

@Composable
private fun ExamCard(
    exam: Toeic,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isLive by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x0A000000)
            )
            .clickable(interactionSource = interactionSource, indication = null) { },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Tags row ────────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LevelBadge(level = exam.level)
            }

            // ── Title ───────────────────────────────────────────────────
            Text(
                text = exam.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // ── Meta row ────────────────────────────────────────────────
            Text(
                text = "Created Oct 24, 2023",
                style = MaterialTheme.typography.labelSmall,
                color = SubtleText
            )

            // ── Divider ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DividerLight)
            )

            // ── Actions row ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ActionIconButton(
                        icon = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = PurplePrimary.copy(alpha = 0.8f),
                        onClick = onEdit
                    )
                    ActionIconButton(
                        icon = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFE53935).copy(alpha = 0.75f),
                        onClick = onDelete
                    )
                }
            }
        }
    }
}

// ── Small reusable composables ───────────────────────────────────────────────

@Composable
private fun LevelBadge(level: String) {
    val color = when (level.uppercase()) {
        "BEGINNER" -> Color(0xFF6D28D9)
        "ADVANCED" -> Color(0xFFD946EF)
        "INTERMEDIATE" -> Color(0xFF06B6D4)
        "PUBLISHING" -> Color(0xFF3B82F6)
        else -> PurplePrimary
    }
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.10f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = level.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun StatusBadge(label: String) {
    Box(
        modifier = Modifier
            .background(
                color = DividerLight,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = PurplePrimary
        )
    }
}

@Composable
private fun ActionIconButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(34.dp)
            .background(
                color = tint.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
    }
}

// ── Preview ──────────────────────────────────────────────────────────────────

@Preview(
    name = "phone",
    device = Devices.PIXEL_4,
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ToeicListScreenPreview() {
    ToeicListScreen(
        onBack = {},
        onAddExam = {}
    )
}
