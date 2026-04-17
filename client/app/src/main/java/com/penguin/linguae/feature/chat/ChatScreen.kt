package com.penguin.linguae.feature.chat

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.penguin.linguae.data.model.ChatUiMessage
import com.penguin.linguae.feature.chat.component.ChatSheet
import com.penguin.linguae.feature.chat.component.DraggableChatFab

@Composable
fun ChatScreen(modifier: Modifier = Modifier) {
    var isChatOpen by rememberSaveable { mutableStateOf(false) }
    var draft by rememberSaveable { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatUiMessage(
                id = "welcome",
                text = "Xin chào, mình là trợ lý Linguae. Bạn muốn học gì hôm nay?",
                isFromBot = true
            )
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val bubbleSize = 62.dp
        val bubbleSizePx = with(density) { bubbleSize.toPx() }
        val sidePaddingPx = with(density) { 16.dp.toPx() }
        val topPaddingPx = with(density) { 110.dp.toPx() }
        val bottomPaddingPx = with(density) { 120.dp.toPx() }

        val maxX = constraints.maxWidth.toFloat() - bubbleSizePx - sidePaddingPx
        val maxY = constraints.maxHeight.toFloat() - bubbleSizePx - bottomPaddingPx

        var offsetX by rememberSaveable {
            mutableFloatStateOf((constraints.maxWidth * 0.78f).coerceAtLeast(0f))
        }
        var offsetY by rememberSaveable {
            mutableFloatStateOf((constraints.maxHeight * 0.72f).coerceAtLeast(0f))
        }

        LaunchedEffect(maxX, maxY) {
            offsetX = offsetX.coerceIn(sidePaddingPx, maxX.coerceAtLeast(sidePaddingPx))
            offsetY = offsetY.coerceIn(topPaddingPx, maxY.coerceAtLeast(topPaddingPx))
        }

        DraggableChatFab(
            offsetX = offsetX,
            offsetY = offsetY,
            maxX = maxX,
            maxY = maxY,
            sidePaddingPx = sidePaddingPx,
            topPaddingPx = topPaddingPx,
            onOffsetChange = { x, y ->
                offsetX = x
                offsetY = y
            },
            onClick = { isChatOpen = true }
        )
    }

     if (isChatOpen) {
         ChatSheet(
             draft = draft,
             messages = messages,
             onDraftChange = { draft = it },
             onClose = { isChatOpen = false }
         )
     }
}
