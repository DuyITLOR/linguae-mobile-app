package com.penguin.linguae.feature.admin.toeic

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.CircularProgressIndicator
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.GreenCorrect
import com.penguin.linguae.core.ui.theme.PurpleLight
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.feature.admin.toeic.viewmodel.Part5QuestionDraft
import com.penguin.linguae.feature.admin.toeic.viewmodel.ToeicEditorViewModel
import kotlinx.coroutines.launch

// ── Local palette (light theme — consistent with ToeicListScreen) ─────────────
private val GradientStart    = Color(0xFF4F5DE9)
private val GradientEnd      = Color(0xFF6E46F2)
private val CardSurface      = Color.White
private val CardInputBg      = Color(0xFFF4F5FA)   // AppBackground tint for inputs
private val SectionLabelClr  = Color(0xFF8D8EA3)   // matches SubtleText in ToeicListScreen
private val CorrectGreenClr  = GreenCorrect         // Color(0xFF4CAF50) from theme
private val CorrectGreenBg   = Color(0xFFE8F5E9)
private val DividerColor     = Color(0xFFF0EEFF)   // DividerLight from ToeicListScreen
private val OptionBg         = Color(0xFFF8F7FF)   // very light purple tint
private val OptionBgCorrect  = Color(0xFFE8F5E9)   // light green
private val AccentRed        = Color(0xFFE53935)

@Composable
fun ToeicPart5EditorScreen(
    onBack: () -> Unit,
    toeicId: String?,
    onSaveExam: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onPart6Click: () -> Unit = {},
    viewModel: ToeicEditorViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var isAdding by remember { mutableStateOf(false) }

    Surface(
        color = AppBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // ── Top bar ──────────────────────────────────────────────────────
                EditorTopBar(onBack = onBack)

                Spacer(modifier = Modifier.height(12.dp))

                // ── Part navigation tabs ─────────────────────────────────────────
                PartNavigation(onInfoClick = onInfoClick, onPart6Click = onPart6Click)

                Spacer(modifier = Modifier.height(16.dp))

                // ── Question list + add button ───────────────────────────────────
                Box(modifier = Modifier.weight(1f)) {
                    if (viewModel.part5Questions.isEmpty()) {
                        EmptyState(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp)
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            itemsIndexed(
                                items = viewModel.part5Questions,
                                key = { _, q -> q.id }
                            ) { index, question ->
                                QuestionCard(
                                    index = index,
                                    draft = question,
                                    onUpdate = { updated ->
                                            viewModel.updatePart5Question(question.id, updated)
                                    },
                                    onDelete = { viewModel.removePart5Question(question.id) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(120.dp)) }
                        }
                    }

                    // ── Bottom Controls (Nav & FABs) ─────────────────────────────
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (viewModel.part5Questions.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(viewModel.part5Questions.size) { index ->
                                    val isSelected = index == listState.firstVisibleItemIndex
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(if (isSelected) PurplePrimary else Color.White, CircleShape)
                                            .border(1.dp, if (isSelected) PurplePrimary else PurpleLight, CircleShape)
                                            .clickable {
                                                scope.launch {
                                                    listState.animateScrollToItem(index)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = (index + 1).toString(),
                                            color = if (isSelected) Color.White else PurplePrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AddQuestionFab(
                                isAdding = isAdding,
                                onClick = {
                                    isAdding = true
                                    viewModel.addPart5Question()
                                    scope.launch {
                                        kotlinx.coroutines.delay(100)
                                        listState.animateScrollToItem(viewModel.part5Questions.size)
                                        kotlinx.coroutines.delay(300)
                                        isAdding = false
                                    }
                                }
                            )
                            SaveExamFab(
                                isSaving = viewModel.isSaving,
                                onClick = {
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
                            )
                        }
                    }
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
private fun EditorTopBar(onBack: () -> Unit) {
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

// ── Part 5 / Part 6 navigation ────────────────────────────────────────────────

@Composable
private fun PartNavigation(
    onInfoClick: () -> Unit = {},
    onPart6Click: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 16.dp)
            .background(Color(0xFFEDEDED), RoundedCornerShape(22.dp))
            .padding(4.dp)
    ) {
        PartTab(text = "Thông tin", isSelected = false, onClick = onInfoClick,  modifier = Modifier.weight(1f))
        PartTab(text = "Part 5",    isSelected = true,  onClick = {},            modifier = Modifier.weight(1f))
        PartTab(text = "Part 6",    isSelected = false, onClick = onPart6Click,  modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PartTab(
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

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(GradientStart, GradientEnd)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = "Chưa có câu hỏi nào",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            Text(
                text = "Nhấn nút bên dưới để thêm câu hỏi Part 5",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }
    }
}

// ── Floating add button ───────────────────────────────────────────────────────

@Composable
private fun AddQuestionFab(
    isAdding: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
            .background(
                brush = Brush.horizontalGradient(listOf(GradientStart, GradientEnd)),
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(enabled = !isAdding) { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isAdding) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add question",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Thêm câu hỏi",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun SaveExamFab(
    isSaving: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
            .background(
                brush = Brush.horizontalGradient(listOf(GradientStart, GradientEnd)),
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(enabled = !isSaving) { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Save exam",
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
    }
}

// ── Question card ─────────────────────────────────────────────────────────────

@Composable
private fun QuestionCard(
    index: Int,
    draft: Part5QuestionDraft,
    onUpdate: (Part5QuestionDraft) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(tween(200))
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Card header ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Question badge
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(listOf(GradientStart, GradientEnd)),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Câu ${index + 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Delete button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(AccentRed.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Xoá câu hỏi",
                        tint = AccentRed.copy(alpha = 0.75f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Divider(color = DividerColor, thickness = 1.dp)


            // ── CÂU HỎI section ─────────────────────────────────────────────
            SectionLabel(text = "CÂU HỎI")

            // Hint text
            Text(
                text = "Nhập câu — dùng ______ cho chỗ trống",
                style = MaterialTheme.typography.labelSmall,
                color = SectionLabelClr,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // Sentence input
            OutlinedTextField(
                value = draft.sentence,
                onValueChange = { onUpdate(draft.copy(sentence = it)) },
                placeholder = {
                    Text(
                        text = "The manager asked the staff to ______ the report by Friday.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardInputBg,
                    unfocusedContainerColor = CardInputBg,
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = DividerColor,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark,
                    cursorColor = PurplePrimary
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextDark)
            )

            // Blank hint
            Text(
                text = "Nhấn vào thẻ trống màu tím để chỉnh sửa vị trí chỗ trống.",
                style = MaterialTheme.typography.labelSmall,
                color = SectionLabelClr
            )

            Divider(color = DividerColor, thickness = 1.dp)

            // ── 4 ĐÁP ÁN section ────────────────────────────────────────────
            SectionLabel(text = "4 ĐÁP ÁN & ĐÁP ÁN ĐÚNG")

            // 2×2 grid of options
            val labels = listOf("A", "B", "C", "D")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (row in 0..1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (col in 0..1) {
                            val optionIndex = row * 2 + col
                            OptionInput(
                                label = labels[optionIndex],
                                value = draft.options[optionIndex],
                                isCorrect = draft.answer == optionIndex,
                                onValueChange = { newText ->
                                    val newOptions = draft.options.toMutableList()
                                    newOptions[optionIndex] = newText
                                    onUpdate(draft.copy(options = newOptions))
                                },
                                onMarkCorrect = {
                                    onUpdate(draft.copy(answer = optionIndex))
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Option input ──────────────────────────────────────────────────────────────

@Composable
private fun OptionInput(
    label: String,
    value: String,
    isCorrect: Boolean,
    onValueChange: (String) -> Unit,
    onMarkCorrect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor      = if (isCorrect) OptionBgCorrect else OptionBg
    val borderColor  = if (isCorrect) CorrectGreenClr else PurpleLight

    Column(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Label badge + text field row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Letter badge
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = if (isCorrect) CorrectGreenClr else PurpleLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) Color.White else PurplePrimary
                )
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = {
                    Text(
                        text = label.lowercase() + "...",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark,
                    cursorColor = PurplePrimary
                ),
                textStyle = MaterialTheme.typography.bodySmall.copy(color = TextDark)
            )
        }

        // Correct button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(
                    color = if (isCorrect) CorrectGreenBg else Color(0xFFEDEDED),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isCorrect) CorrectGreenClr.copy(alpha = 0.5f) else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onMarkCorrect() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isCorrect) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = CorrectGreenClr,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = if (isCorrect) "Đúng" else "Đúng?",
                    fontSize = 12.sp,
                    fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCorrect) CorrectGreenClr else SectionLabelClr
                )
            }
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = SectionLabelClr,
        letterSpacing = 1.sp
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(
    name = "Phone Preview",
    device = Devices.PIXEL_4,
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ToeicEditorScreenPreview() {
    ToeicPart5EditorScreen(
        onBack = {},
        toeicId = null,
        onPart6Click = {}
    )
}