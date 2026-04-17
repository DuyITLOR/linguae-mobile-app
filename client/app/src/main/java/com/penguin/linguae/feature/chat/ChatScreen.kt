package com.penguin.linguae.feature.chat

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penguin.linguae.feature.chat.component.ChatSheet
import com.penguin.linguae.feature.chat.component.DraggableChatFab
import com.penguin.linguae.feature.chat.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = viewModel()
) {
    var isChatOpen by rememberSaveable { mutableStateOf(false) }
    var isBubbleVisible by rememberSaveable { mutableStateOf(true) }
    var draft by rememberSaveable { mutableStateOf("") }
    val messages by chatViewModel.messages.collectAsStateWithLifecycle()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val bubbleSize = 60.dp
        val bubbleSizePx = with(density) { bubbleSize.toPx() }
        val sidePaddingPx = with(density) { 16.dp.toPx() }
        val topPaddingPx = with(density) { 110.dp.toPx() }
        val bottomPaddingPx = with(density) { 120.dp.toPx() }

        val maxX = constraints.maxWidth.toFloat() - bubbleSizePx - sidePaddingPx
        val maxY = constraints.maxHeight.toFloat() - bubbleSizePx - bottomPaddingPx

        var offsetX by rememberSaveable {
            mutableFloatStateOf((constraints.maxWidth * 0.85f).coerceAtLeast(0f))
        }
        var offsetY by rememberSaveable {
            mutableFloatStateOf((constraints.maxHeight * 0.73f).coerceAtLeast(0f))
        }

        LaunchedEffect(maxX, maxY) {
            offsetX = offsetX.coerceIn(sidePaddingPx, maxX.coerceAtLeast(sidePaddingPx))
            offsetY = offsetY.coerceIn(topPaddingPx, maxY.coerceAtLeast(topPaddingPx))
        }

        if (isBubbleVisible) {
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
                onClick = { isChatOpen = true },
                onCloseBubble = {
                    isBubbleVisible = false
                    isChatOpen = false
                },
            )
        }
    }

    if (isChatOpen) {
        ChatSheet(
            draft = draft,
            isLoading = chatViewModel.isLoading,
            messages = messages,
            onDraftChange = { draft = it },
            onSend = {
                if (draft.isNotBlank()) {
                    chatViewModel.ask(draft)
                    draft = ""
                }
            },
            onClose = { isChatOpen = false }
        )
    }
}
