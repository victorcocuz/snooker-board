package com.quickpoint.snookerboard.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun TextParagraph(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    Text(
        modifier = modifier,
        text = text, textAlign = textAlign, style = style
    )
}

@Composable
fun TextHeadline(text: String) = TextParagraph(
    modifier = Modifier.fillMaxWidth(),
    text = text,
    textAlign = TextAlign.Center,
    style = MaterialTheme.typography.headlineSmall
)

@Composable
fun TextTitle(text: String) = TextParagraph(
    text = text,
    style = MaterialTheme.typography.titleLarge
)


@Composable
fun TextSubtitle(text: String) = TextParagraph(
    text = text,
    style = MaterialTheme.typography.bodyLarge
)
