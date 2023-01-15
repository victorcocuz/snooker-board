package com.quickpoint.snookerboard.compose.ui.styles

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.compose.ui.theme.spacing


@Composable
fun TextNavHeadline(text: String) {
    Text(
        modifier = Modifier
            .padding(0.dp, MaterialTheme.spacing.small, 0.dp, 0.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        text = text,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun TextNavTitle(text: String) {
    Text(
        modifier = Modifier.padding(0.dp, MaterialTheme.spacing.smallMedium, 0.dp, 0.dp),
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun TextNavParagraphSubTitle(text: String) {
    Text(
        modifier = Modifier.padding(0.dp, MaterialTheme.spacing.small, 0.dp, 0.dp),
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun TextNavParagraph(text: String) {
    Text(
        text = text, style = MaterialTheme.typography.bodySmall
    )
}