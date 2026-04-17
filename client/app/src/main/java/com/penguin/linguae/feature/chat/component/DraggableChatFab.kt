package com.penguin.linguae.feature.chat.component

import androidx.compose.foundation.gestures.detectDragGestures
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
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .size(62.dp)
            .offset {
                IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
            }
            .pointerInput(maxX, maxY) {
                detectDragGestures(
                    onDragEnd = {
                        val snappedX = if (offsetX < (maxX + sidePaddingPx) / 2f) {
                            sidePaddingPx
                        } else {
                            maxX.coerceAtLeast(sidePaddingPx)
                        }
                        val snappedY = offsetY.coerceIn(
                            topPaddingPx,
                            maxY.coerceAtLeast(topPaddingPx)
                        )
                        onOffsetChange(snappedX, snappedY)
                    }
                ) { change, dragAmount ->
                    change.consume()
                    val newX = (offsetX + dragAmount.x)
                        .coerceIn(sidePaddingPx, maxX.coerceAtLeast(sidePaddingPx))
                    val newY = (offsetY + dragAmount.y)
                        .coerceIn(topPaddingPx, maxY.coerceAtLeast(topPaddingPx))
                    onOffsetChange(newX, newY)
                }
            }
    ) {
        Icon(
            imageVector = Icons.Filled.ChatBubble,
            contentDescription = "Mở chatbot"
        )
    }
}
