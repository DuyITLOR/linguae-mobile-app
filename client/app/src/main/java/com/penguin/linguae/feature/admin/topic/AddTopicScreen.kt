package com.penguin.linguae.feature.admin.topic

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.core.ui.theme.AppBackground
import com.penguin.linguae.core.ui.theme.BorderGray
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurpleDeep
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.data.model.Topic
import com.penguin.linguae.feature.admin.topic.viewmodel.ManageTopicViewModel
import com.penguin.linguae.feature.admin.topic.viewmodel.TopicManageMode
import com.penguin.linguae.feature.learning.topicColorFor
import com.penguin.linguae.feature.learning.topicIconFor

private val topicLevels = listOf("BEGINNER", "INTERMEDIATE", "ADVANCED")

private fun levelColor(level: String): Color = when (level.uppercase()) {
    "BEGINNER" -> Color(0xFF27AE60)
    "INTERMEDIATE" -> Color(0xFFE67E22)
    "ADVANCED" -> Color(0xFFE74C3C)
    else -> Color(0xFF7F8C8D)
}

@Composable
fun ManageTopicScreen(
    viewModel: ManageTopicViewModel = viewModel(),
    onBack: () -> Unit = {},
    onTopicClick: (Topic) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val deleteTarget = viewModel.deleteTarget
    if (deleteTarget != null) {
        val vocabCount = deleteTarget._count.Vocabulary
        AlertDialog(
            onDismissRequest = { viewModel.cancelDelete() },
            title = { Text("Delete \"${deleteTarget.title}\"") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (vocabCount > 0) {
                        Surface(
                            color = Color(0xFFFFF3CD),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text("⚠️", fontSize = 16.sp)
                                Text(
                                    text = "This topic contains $vocabCount word${if (vocabCount > 1) "s" else ""}. Deleting it will also remove all associated vocabulary.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF7D5A00)
                                )
                            }
                        }
                    }
                    Text("This action cannot be undone.")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.executeDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE74C3C))
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelDelete() }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = AppBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Gradient header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.verticalGradient(listOf(PurplePrimary, PurpleDark, PurpleDeep)))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp, vertical = 16.dp)
                ) {
                    IconButton(onClick = {
                        if (viewModel.mode != TopicManageMode.LIST) viewModel.cancelForm() else onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SurfaceColor)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = when (viewModel.mode) {
                                TopicManageMode.LIST -> "Manage Topics"
                                TopicManageMode.CREATE -> "New Topic"
                                TopicManageMode.EDIT -> "Edit Topic"
                            },
                            color = SurfaceColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = when (viewModel.mode) {
                                TopicManageMode.LIST -> "${viewModel.topics.size} topics"
                                TopicManageMode.CREATE -> "Create a new learning category"
                                TopicManageMode.EDIT -> "Update category details"
                            },
                            color = SurfaceColor.copy(alpha = 0.75f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            when (viewModel.mode) {
                TopicManageMode.LIST -> TopicListContent(
                    topics = viewModel.topics,
                    isLoading = viewModel.isLoading,
                    onAdd = { viewModel.startCreate() },
                    onEdit = { viewModel.startEdit(it) },
                    onDelete = { viewModel.confirmDelete(it) },
                    onTopicClick = onTopicClick
                )
                else -> TopicFormContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun TopicListContent(
    topics: List<Topic>,
    isLoading: Boolean,
    onAdd: () -> Unit,
    onEdit: (Topic) -> Unit,
    onDelete: (Topic) -> Unit,
    onTopicClick: (Topic) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("New Topic", fontWeight = FontWeight.SemiBold)
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PurplePrimary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                items(topics) { topic ->
                    TopicManageCard(
                        topic = topic,
                        onClick = { onTopicClick(topic) },
                        onEdit = { onEdit(topic) },
                        onDelete = { onDelete(topic) }
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun TopicManageCard(
    topic: Topic,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (Topic) -> Unit
) {
    val seed = "${topic.id}_${topic.title}_${topic.level}"
    val accent = topicColorFor(seed)
    val fallbackIcon = topicIconFor(seed)
    val lvlColor = levelColor(topic.level)

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f)),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (!topic.icon.isNullOrBlank()) {
                    Text(text = topic.icon, fontSize = 20.sp)
                } else {
                    Icon(
                        imageVector = fallbackIcon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(topic.title, color = TextDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(lvlColor.copy(alpha = 0.13f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(topic.level, color = lvlColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text("· ${topic._count.Vocabulary} words", color = TextGray, fontSize = 12.sp)
                }
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PurplePrimary, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = { onDelete(topic) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE74C3C), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun TopicFormContent(viewModel: ManageTopicViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        TopicFormSection(title = "Topic Details") {
            TopicFormField(label = "Title *") {
                OutlinedTextField(
                    value = viewModel.title,
                    onValueChange = { viewModel.title = it },
                    placeholder = { Text("e.g. Daily Conversation", color = TextGray) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = topicFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            TopicFormField(label = "Description") {
                OutlinedTextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    placeholder = { Text("Brief description of this topic", color = TextGray) },
                    minLines = 3,
                    shape = RoundedCornerShape(18.dp),
                    colors = topicFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            TopicFormField(label = "Icon (emoji or URL)") {
                OutlinedTextField(
                    value = viewModel.icon,
                    onValueChange = { viewModel.icon = it },
                    placeholder = { Text("e.g. 💬", color = TextGray) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = topicFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        TopicFormSection(title = "Settings") {
            TopicFormField(label = "Level") {
                TopicLevelDropdown(selected = viewModel.level, onSelect = { viewModel.level = it })
            }
        }

        Button(
            onClick = { viewModel.submit() },
            enabled = !viewModel.isLoading,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(54.dp)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
                Text(if (viewModel.mode == TopicManageMode.CREATE) "Creating..." else "Saving...", fontWeight = FontWeight.SemiBold)
            } else {
                Text(
                    text = if (viewModel.mode == TopicManageMode.CREATE) "Create Topic" else "Update Topic",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }

        OutlinedButton(
            onClick = { viewModel.cancelForm() },
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, BorderGray),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Cancel", color = TextGray, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicLevelDropdown(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(18.dp),
            colors = topicFieldColors(),
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            topicLevels.forEach { level ->
                DropdownMenuItem(text = { Text(level) }, onClick = { onSelect(level); expanded = false })
            }
        }
    }
}

@Composable
private fun TopicFormSection(title: String, content: @Composable () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFE8E3FA)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(text = title, color = TextDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun TopicFormField(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun topicFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PurplePrimary,
    unfocusedBorderColor = BorderGray,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedLabelColor = PurplePrimary,
    cursorColor = PurplePrimary
)
