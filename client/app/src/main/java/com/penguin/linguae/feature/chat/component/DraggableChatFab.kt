package com.penguin.linguae.feature.chat.component

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun DraggableChatFab(
    offsetX: Float,
    offsetY: Float,
    maxX: Float,
    maxY: Float,
    sidePaddingPx: Float,
    topPaddingPx: Float,
    onOffsetChange: (Float, Float) -> Unit,
    onClick: () -> Unit,
    onCloseBubble: () -> Unit
) {
    val currentOffsetX by rememberUpdatedState(offsetX)
    val currentOffsetY by rememberUpdatedState(offsetY)

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .offset {
                IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
            }
            .pointerInput(maxX, maxY, sidePaddingPx, topPaddingPx) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newX = (currentOffsetX + dragAmount.x)
                        .coerceIn(sidePaddingPx, maxX.coerceAtLeast(sidePaddingPx))
                    val newY = (currentOffsetY + dragAmount.y)
                        .coerceIn(topPaddingPx, maxY.coerceAtLeast(topPaddingPx))
                    onOffsetChange(newX, newY)
                }
            }
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Filled.ChatBubble,
                contentDescription = "Mở chatbot"
            )
        }

    }
}
