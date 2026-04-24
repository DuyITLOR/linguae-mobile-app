package com.penguin.linguae.feature.admin.matching.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penguin.linguae.core.ui.theme.PurpleDark
import com.penguin.linguae.core.ui.theme.PurpleDeep
import com.penguin.linguae.core.ui.theme.PurplePrimary
import com.penguin.linguae.core.ui.theme.SurfaceColor
import com.penguin.linguae.data.model.adminmatching.MatchingManageMode

@Composable
fun MatchingHeader(
    mode: MatchingManageMode,
    topicTitle: String,
    questionCount: Int,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
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
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = SurfaceColor
                )
            }
            androidx.compose.foundation.layout.Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = when (mode) {
                        MatchingManageMode.LIST -> "Matching: $topicTitle"
                        MatchingManageMode.CREATE -> "New Matching Set"
                        MatchingManageMode.EDIT -> "Edit Matching Set"
                    },
                    color = SurfaceColor,
                    fontSize = 21.sp
                )
                Text(
                    text = when (mode) {
                        MatchingManageMode.LIST -> "$questionCount sets ready for this topic"
                        MatchingManageMode.CREATE -> "Draft both columns before wiring the API"
                        MatchingManageMode.EDIT -> "Refine the prompt and pair wording"
                    },
                    color = SurfaceColor.copy(alpha = 0.78f),
                    fontSize = 13.sp
                )
            }
        }
    }
}
