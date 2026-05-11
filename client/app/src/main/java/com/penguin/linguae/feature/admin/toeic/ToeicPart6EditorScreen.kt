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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
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
import com.penguin.linguae.feature.admin.toeic.viewmodel.Part6PassageDraft
import com.penguin.linguae.feature.admin.toeic.viewmodel.Part6QuestionDraft
import com.penguin.linguae.feature.admin.toeic.viewmodel.ToeicEditorViewModel
import kotlinx.coroutines.launch

// ── Palette (mirrors Part5EditorScreen — all light theme) ─────────────────────
private val P6GradientStart  = Color(0xFF4F5DE9)
private val P6GradientEnd    = Color(0xFF6E46F2)
private val P6CardSurface    = Color.White
private val P6CardInputBg    = Color(0xFFF4F5FA)
private val P6SectionLbl     = Color(0xFF8D8EA3)
private val P6CorrectGreen   = GreenCorrect
private val P6CorrectGreenBg = Color(0xFFE8F5E9)
private val P6Divider        = Color(0xFFF0EEFF)
private val P6OptionBg       = Color(0xFFF8F7FF)
private val P6OptionCorrect  = Color(0xFFE8F5E9)
private val P6AccentRed      = Color(0xFFE53935)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun ToeicPart6EditorScreen(
    onBack: () -> Unit,
    toeicId: String?,
    onSaveExam: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onPart5Click: () -> Unit = {},
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
                P6TopBar(onBack = onBack)

                Spacer(modifier = Modifier.height(12.dp))

                P6PartNavigation(onInfoClick = onInfoClick, onPart5Click = onPart5Click)

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (viewModel.part6Passages.isEmpty()) {
                        P6EmptyState(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp)
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            itemsIndexed(
                                items = viewModel.part6Passages,
                                key = { _, p -> p.id }
                            ) { index, passage ->
                                PassageCard(
                                    passageIndex = index,
                                    draft = passage,
                                    onUpdatePassage = { updated ->
                                        viewModel.updatePart6Passage(passage.id, updated)
                                    },
                                    onDeletePassage = {
                                        viewModel.removePart6Passage(passage.id)
                                    },
                                    onAddQuestion = {
                                        viewModel.addQuestionToPassage(passage.id)
                                    },
                                    onUpdateQuestion = { qId, updated ->
                                        viewModel.updateQuestionInPassage(passage.id, qId, updated)
                                    },
                                    onDeleteQuestion = { qId ->
                                        viewModel.removeQuestionFromPassage(passage.id, qId)
                                    }
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
                        if (viewModel.part6Passages.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(viewModel.part6Passages.size) { index ->
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
                            P6AddFab(
                                isAdding = isAdding,
                                label = "Thêm bài đọc",
                                onClick = {
                                    isAdding = true
                                    viewModel.addPart6Passage()
                                    scope.launch {
                                        kotlinx.coroutines.delay(100)
                                        listState.animateScrollToItem(viewModel.part6Passages.size)
                                        kotlinx.coroutines.delay(300)
                                        isAdding = false
                                    }
                                }
                            )
                            P6SaveFab(
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
private fun P6TopBar(onBack: () -> Unit) {
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

// ── Part navigation ───────────────────────────────────────────────────────────

@Composable
private fun P6PartNavigation(
    onInfoClick: () -> Unit = {},
    onPart5Click: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 16.dp)
            .background(Color(0xFFEDEDED), RoundedCornerShape(22.dp))
            .padding(4.dp)
    ) {
        P6PartTab(text = "Thông tin", isSelected = false, onClick = onInfoClick,  modifier = Modifier.weight(1f))
        P6PartTab(text = "Part 5",    isSelected = false, onClick = onPart5Click, modifier = Modifier.weight(1f))
        P6PartTab(text = "Part 6",    isSelected = true,  onClick = {},           modifier = Modifier.weight(1f))
    }
}

@Composable
private fun P6PartTab(
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
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PurplePrimary else TextGray
        )
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun P6EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(P6GradientStart, P6GradientEnd)),
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
                text = "Chưa có bài đọc nào",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            Text(
                text = "Nhấn nút bên dưới để thêm bài đọc Part 6",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }
    }
}

// ── FABs ──────────────────────────────────────────────────────────────────────

@Composable
private fun P6AddFab(
    isAdding: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
            .background(
                brush = Brush.horizontalGradient(listOf(P6GradientStart, P6GradientEnd)),
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
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(text = label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun P6SaveFab(
    isSaving: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
            .background(
                brush = Brush.horizontalGradient(listOf(P6GradientStart, P6GradientEnd)),
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
                    contentDescription = "Lưu bài",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(text = "Lưu bài", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

// ── Passage card ──────────────────────────────────────────────────────────────

@Composable
private fun PassageCard(
    passageIndex: Int,
    draft: Part6PassageDraft,
    onUpdatePassage: (Part6PassageDraft) -> Unit,
    onDeletePassage: () -> Unit,
    onAddQuestion: () -> Unit,
    onUpdateQuestion: (String, Part6QuestionDraft) -> Unit,
    onDeleteQuestion: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = P6CardSurface),
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
            // ── Header ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(listOf(P6GradientStart, P6GradientEnd)),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Bài đọc ${passageIndex + 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(P6AccentRed.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                        .clickable { onDeletePassage() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Xoá bài đọc",
                        tint = P6AccentRed.copy(alpha = 0.75f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Divider(color = P6Divider, thickness = 1.dp)

            // ── Passage text ─────────────────────────────────────────────────
            P6SectionLabel(text = "ĐOẠN VĂN")

            Text(
                text = "Nhập đoạn văn — xuống dòng để tạo đoạn mới",
                style = MaterialTheme.typography.labelSmall,
                color = P6SectionLbl,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            OutlinedTextField(
                value = draft.passage,
                onValueChange = { onUpdatePassage(draft.copy(passage = it)) },
                placeholder = {
                    Text(
                        text = "Nhập đoạn văn Part 6 tại đây...",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 12,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = P6CardInputBg,
                    unfocusedContainerColor = P6CardInputBg,
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = P6Divider,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark,
                    cursorColor = PurplePrimary
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextDark)
            )

            Divider(color = P6Divider, thickness = 1.dp)

            // ── Questions section ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                P6SectionLabel(text = "CÂU HỎI (${draft.questions.size})")

                Box(
                    modifier = Modifier
                        .background(PurpleLight, RoundedCornerShape(8.dp))
                        .clickable { onAddQuestion() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Thêm câu hỏi",
                            tint = PurplePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Thêm câu",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PurplePrimary
                        )
                    }
                }
            }

            if (draft.questions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(P6OptionBg, RoundedCornerShape(12.dp))
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chưa có câu hỏi — nhấn \"Thêm câu\" để bắt đầu",
                        style = MaterialTheme.typography.bodySmall,
                        color = P6SectionLbl
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    draft.questions.forEachIndexed { qIndex, question ->
                        QuestionSubCard(
                            questionIndex = qIndex,
                            draft = question,
                            onUpdate = { updated ->
                                onUpdateQuestion(question.id, updated)
                            },
                            onDelete = { onDeleteQuestion(question.id) }
                        )
                    }
                }
            }
        }
    }
}

// ── Sub-question card ─────────────────────────────────────────────────────────

@Composable
private fun QuestionSubCard(
    questionIndex: Int,
    draft: Part6QuestionDraft,
    onUpdate: (Part6QuestionDraft) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(P6OptionBg, RoundedCornerShape(14.dp))
            .border(1.dp, P6Divider, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Câu ${questionIndex + 1}  ·  Title: ${draft.title}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = PurplePrimary
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(P6AccentRed.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Xoá câu hỏi",
                    tint = P6AccentRed.copy(alpha = 0.75f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Title (int) input
        OutlinedTextField(
            value = if (draft.title == 0) "" else draft.title.toString(),
            onValueChange = { raw ->
                val parsed = raw.toIntOrNull() ?: 0
                onUpdate(draft.copy(title = parsed))
            },
            label = {
                Text("Title (số thứ tự)", style = MaterialTheme.typography.labelSmall, color = P6SectionLbl)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = P6Divider,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark,
                cursorColor = PurplePrimary
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextDark)
        )

        Divider(color = P6Divider)

        // Options 2×2 grid
        P6SectionLabel(text = "4 ĐÁP ÁN & ĐÁP ÁN ĐÚNG")

        val labels = listOf("A", "B", "C", "D")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (row in 0..1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0..1) {
                        val optIdx = row * 2 + col
                        P6OptionInput(
                            label = labels[optIdx],
                            value = draft.options[optIdx],
                            isCorrect = draft.answer == optIdx,
                            onValueChange = { newText ->
                                val newOpts = draft.options.toMutableList()
                                newOpts[optIdx] = newText
                                onUpdate(draft.copy(options = newOpts))
                            },
                            onMarkCorrect = { onUpdate(draft.copy(answer = optIdx)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// ── Option input ──────────────────────────────────────────────────────────────

@Composable
private fun P6OptionInput(
    label: String,
    value: String,
    isCorrect: Boolean,
    onValueChange: (String) -> Unit,
    onMarkCorrect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor     = if (isCorrect) P6OptionCorrect else Color.White
    val borderColor = if (isCorrect) P6CorrectGreen else PurpleLight

    Column(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(10.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        color = if (isCorrect) P6CorrectGreen else PurpleLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 10.sp,
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
                        text = "${label.lowercase()}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                },
                shape = RoundedCornerShape(6.dp),
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(
                    color = if (isCorrect) P6CorrectGreenBg else Color(0xFFEDEDED),
                    shape = RoundedCornerShape(6.dp)
                )
                .border(
                    1.dp,
                    if (isCorrect) P6CorrectGreen.copy(alpha = 0.5f) else Color.Transparent,
                    RoundedCornerShape(6.dp)
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
                        tint = P6CorrectGreen,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Text(
                    text = if (isCorrect) "Đúng" else "Đúng?",
                    fontSize = 11.sp,
                    fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCorrect) P6CorrectGreen else P6SectionLbl
                )
            }
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
private fun P6SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = P6SectionLbl,
        letterSpacing = 1.sp
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(
    name = "Part6 Phone Preview",
    device = Devices.PIXEL_4,
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ToeicPart6EditorScreenPreview() {
    ToeicPart6EditorScreen(
        onBack = {},
        toeicId = null,
        onPart5Click = {}
    )
}
