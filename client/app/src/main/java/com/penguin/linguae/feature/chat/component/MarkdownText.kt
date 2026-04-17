package com.penguin.linguae.feature.chat.component


import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.compose.material3.MaterialTheme
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    isFromBot: Boolean = true
) {
    val context = LocalContext.current
    val textColor = if (isFromBot) {
        MaterialTheme.colorScheme.onSurface.toArgb()
    } else {
        MaterialTheme.colorScheme.onPrimary.toArgb()
    }

    val parser = remember { Parser.builder().build() }
    val renderer = remember { HtmlRenderer.builder().build() }

    val html = remember(text) {
        val document = parser.parse(text)
        renderer.render(document)
    }

    AndroidView(
        modifier = modifier,
        factory = {
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
                setTextColor(textColor)
                textSize = 16f
                setLineSpacing(0f, 1.25f)
            }
        },
        update = { textView ->
            textView.setTextColor(textColor)
            textView.text = HtmlCompat.fromHtml(
                html,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    )
}
