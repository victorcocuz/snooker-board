package com.quickpoint.snookerboard.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.quickpoint.snookerboard.ui.theme.Beige
import com.quickpoint.snookerboard.ui.theme.BrownDark

@Composable
fun TextParagraph(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    color: Color = MaterialTheme.typography.bodySmall.color,
) {
    Text(
        modifier = modifier,
        text = text, textAlign = textAlign, style = style, color = color
    )
}

@Composable
fun TextBallInfo(text: String) = TextParagraph(
    text = text,
    textAlign = TextAlign.Center,
    style = MaterialTheme.typography.bodyLarge.copy(color = BrownDark),
    color = Beige
)

@Composable
fun TextHeadline(text: String) = TextParagraph(
    modifier = Modifier.fillMaxWidth(),
    text = text,
    textAlign = TextAlign.Center,
    style = MaterialTheme.typography.headlineSmall
)

@Composable
fun TextTitle(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.typography.bodyLarge.color,
) = TextParagraph(
    text = text,
    modifier = modifier,
    textAlign = textAlign,
    style = MaterialTheme.typography.titleLarge,
    color = color
)


@Composable
fun TextSubtitle(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.typography.bodyLarge.color,
) = TextParagraph(
    text = text,
    modifier = modifier,
    textAlign = textAlign,
    style = MaterialTheme.typography.bodyLarge,
    color = color
)
