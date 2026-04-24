package com.penguin.linguae.feature.admin.matching.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.BorderGray
import com.penguin.linguae.core.ui.theme.PurpleLight
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.TextDark
import com.penguin.linguae.core.ui.theme.TextGray
import com.penguin.linguae.data.model.adminmatching.MatchingDraftPair
import com.penguin.linguae.data.model.adminmatching.MatchingManageMode

@Composable
fun MatchingFormContent(
    topicId: String,
    topicTitle: String,
    mode: MatchingManageMode,
    title: String,
    pairs: List<MatchingDraftPair>,
    isSubmitting: Boolean,
    onTitleChange: (String) -> Unit,
    onLeftChange: (Int, String) -> Unit,
    onRightChange: (Int, String) -> Unit,
    onAddPair: () -> Unit,
    onRemovePair: (Int) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    val previewPairs = pairs.filter {
        it.leftText.isNotBlank() || it.rightText.isNotBlank()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        MatchingFormSection(
            title = "Set Details",
            subtitle = "Keep the copy short and clear so learners can scan quickly."
        ) {
            Surface(
                color = PurpleLight.copy(alpha = 0.45f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Topic", color = TextGray, fontSize = 12.sp)
                        Text(topicTitle, color = TextDark, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = topicId.take(8),
                        color = PurplePrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Matching title") },
                placeholder = { Text("Example: Match the phrasal verbs to their meanings") },
                shape = RoundedCornerShape(18.dp),
                singleLine = true,
                colors = matchingFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        MatchingFormSection(
            title = "Matching Pairs",
            subtitle = "Each row becomes one left-right pair. Add at least 2 complete rows."
        ) {
            pairs.forEachIndexed { index, pair ->
                MatchingPairEditorCard(
                    index = index,
                    pair = pair,
                    onLeftChange = { onLeftChange(pair.id, it) },
                    onRightChange = { onRightChange(pair.id, it) },
                    onRemove = { onRemovePair(pair.id) }
                )
            }

            OutlinedButton(
                onClick = onAddPair,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add another pair", color = PurplePrimary, fontWeight = FontWeight.SemiBold)
            }
        }

        MatchingFormSection(
            title = "Live Preview",
            subtitle = "A quick visual check before the real API wiring lands."
        ) {
            if (previewPairs.isEmpty()) {
                Text(
                    text = "Start typing a few rows and the preview board will appear here.",
                    color = TextGray,
                    fontSize = 13.sp
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MatchingPreviewColumn(
                        title = "Column A",
                        items = previewPairs.map { it.leftText.ifBlank { "..." } },
                        modifier = Modifier.weight(1f)
                    )
                    MatchingPreviewColumn(
                        title = "Column B",
                        items = previewPairs.reversed().map { it.rightText.ifBlank { "..." } },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Button(
            onClick = onSubmit,
            enabled = !isSubmitting,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary, contentColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (mode == MatchingManageMode.EDIT) "Update Matching Set" else "Create Matching Set",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }

        OutlinedButton(
            onClick = onCancel,
            enabled = !isSubmitting,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, BorderGray),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Cancel", color = TextGray, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MatchingPairEditorCard(
    index: Int,
    pair: MatchingDraftPair,
    onLeftChange: (String) -> Unit,
    onRightChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        color = if (index % 2 == 0) Color.White else Color(0xFFFCFBFF),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFEDE9FB)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pair ${index + 1}",
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onRemove) {
                    Text("Remove", color = Color(0xFFE74C3C))
                }
            }

            OutlinedTextField(
                value = pair.leftText,
                onValueChange = onLeftChange,
                label = { Text("Left column") },
                placeholder = { Text("Example: break down") },
                shape = RoundedCornerShape(14.dp),
                colors = matchingFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pair.rightText,
                onValueChange = onRightChange,
                label = { Text("Right column") },
                placeholder = { Text("Example: stop working") },
                shape = RoundedCornerShape(14.dp),
                colors = matchingFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MatchingFormSection(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFE8E3FA)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    color = TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
            content()
        }
    }
}

@Composable
private fun MatchingPreviewColumn(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF8F7FF),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFEDE9FB)),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = title,
                color = PurplePrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            items.forEachIndexed { index, item ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFF0ECFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${index + 1}. $item",
                        color = TextDark,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp)
                    )
                }
            }
        }
    }
}

@Composable
internal fun matchingFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PurplePrimary,
    unfocusedBorderColor = BorderGray,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = PurplePrimary
)
