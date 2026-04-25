package com.penguin.linguae.feature.admin.toeic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.PurpleLight
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.feature.admin.toeic.viewmodel.ToeicEditorViewModel
import kotlinx.coroutines.launch

// ── Palette ───────────────────────────────────────────────────────────────────
private val EIGradientStart = Color(0xFF4F5DE9)
private val EIGradientEnd   = Color(0xFF6E46F2)
private val EICardSurface   = Color.White
private val EIInputBg       = Color(0xFFF4F5FA)
private val EIDivider       = Color(0xFFF0EEFF)
private val EISectionLbl    = Color(0xFF8D8EA3)

private val levelOptions = listOf("BEGINNER", "INTERMEDIATE", "ADVANCED")

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun ToeicExamInfoScreen(
    onBack: () -> Unit,
    onNavigatePart5: () -> Unit = {},
    onNavigatePart6: () -> Unit = {},
    onSaveExam: () -> Unit = {},
    viewModel: ToeicEditorViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Local mutable copy — synced FROM viewModel on first compose, written BACK on any change
    var draft by remember { mutableStateOf(viewModel.examInfo) }
    LaunchedEffect(viewModel.examInfo) {
        draft = viewModel.examInfo
    }

    Surface(color = AppBackground, modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // ── Top bar ──────────────────────────────────────────────────
                EITopBar(onBack = onBack)

                Spacer(modifier = Modifier.height(12.dp))

                // ── Tab navigation ───────────────────────────────────────────
                EITabNavigation(
                    onPart5Click = onNavigatePart5,
                    onPart6Click = onNavigatePart6
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Form ─────────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Card wrapper
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                            .background(EICardSurface, RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header badge
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(EIGradientStart, EIGradientEnd)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Thông tin chung",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Divider(color = EIDivider)

                        // ── Title field ──────────────────────────────────────
                        EISectionLabel(text = "TIÊU ĐỀ ĐỀ THI")
                        OutlinedTextField(
                            value = draft.title,
                            onValueChange = {
                                draft = draft.copy(title = it)
                                viewModel.updateExamInfo(draft)
                            },
                            placeholder = {
                                Text(
                                    "VD: TOEIC Practice Test 01",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextGray
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = EIInputBg,
                                unfocusedContainerColor = EIInputBg,
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = EIDivider,
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark,
                                cursorColor = PurplePrimary
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextDark)
                        )

                        Divider(color = EIDivider)

                        // ── Level picker ─────────────────────────────────────
                        EISectionLabel(text = "CẤP ĐỘ")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            levelOptions.forEach { level ->
                                val selected = draft.level == level
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            color = if (selected) PurpleLight else EIInputBg,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            width = if (selected) 1.5.dp else 1.dp,
                                            color = if (selected) PurplePrimary else EIDivider,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            draft = draft.copy(level = level)
                                            viewModel.updateExamInfo(draft)
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (selected) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = PurplePrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Text(
                                            text = level,
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selected) PurplePrimary else EISectionLbl
                                        )
                                    }
                                }
                            }
                        }

                        Divider(color = EIDivider)
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            // ── Save FAB ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
                    .background(
                        brush = Brush.horizontalGradient(listOf(EIGradientStart, EIGradientEnd)),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .clickable {
                        scope.launch {
                            val errorMessage = viewModel.saveQuestion()
                            if (errorMessage != null) {
                                snackbarHostState.showSnackbar(errorMessage)
                            } else {
                                snackbarHostState.showSnackbar("Lưu bài thành công")
                                onSaveExam()
                            }
                        }
                    }
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Lưu bài",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Lưu bài",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun EITopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 16.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextDark,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = "Manage Exam",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextDark,
            modifier = Modifier.weight(1f)
        )
    }
}

// ── 3-tab navigation ──────────────────────────────────────────────────────────

@Composable
private fun EITabNavigation(
    onPart5Click: () -> Unit,
    onPart6Click: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 16.dp)
            .background(Color(0xFFEDEDED), RoundedCornerShape(22.dp))
            .padding(4.dp)
    ) {
        EITab(
            text = "Thông tin",
            isSelected = true,
            onClick = {},
            modifier = Modifier.weight(1f)
        )
        EITab(
            text = "Part 5",
            isSelected = false,
            onClick = onPart5Click,
            modifier = Modifier.weight(1f)
        )
        EITab(
            text = "Part 6",
            isSelected = false,
            onClick = onPart6Click,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EITab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PurplePrimary else TextGray
        )
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
private fun EISectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = EISectionLbl,
        letterSpacing = 1.sp
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(
    name = "ExamInfo Phone Preview",
    device = Devices.PIXEL_4,
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ToeicExamInfoScreenPreview() {
    ToeicExamInfoScreen(onBack = {})
}
