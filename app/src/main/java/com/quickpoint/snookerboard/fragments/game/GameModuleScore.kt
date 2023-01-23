package com.quickpoint.snookerboard.fragments.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.compose.ui.styles.StandardRow
import com.quickpoint.snookerboard.compose.ui.styles.TextParagraphSubTitle
import com.quickpoint.snookerboard.compose.ui.styles.TextTitle
import com.quickpoint.snookerboard.compose.ui.theme.Brown

@Composable // Score Chapter
fun GameModuleScore() {
    StandardRow(modifier = Modifier.padding(bottom = 8.dp)) {
        ScoreFrame("80")
        ScoreMatch(text = "5-3")
        ScoreFrame("75")
    }
    ScoreProgressBar(description = "Remaining", progress = 0.7f, value = "7")
    ScoreProgressBar(description = "Difference", progress = 0.5f, value = "5")
}

@Composable
fun ScoreFrame(text: String) {
    TextTitle(text)
}

@Composable
fun ScoreMatch(text: String) {
    TextParagraphSubTitle(text)
}

@Composable
fun ScoreProgressBar(
    description: String,
    progress: Float,
    value: String,
) = Row(
    Modifier
        .fillMaxWidth()
        .padding(0.dp, 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    TextParagraphSubTitle(description)
    Spacer(Modifier.width(8.dp))
    LinearProgressIndicator(
        modifier = Modifier
            .height(16.dp)
            .weight(1f),
        color = Color.White,
        trackColor = Brown,
        progress = progress
    )
    Spacer(Modifier.width(8.dp))
    TextParagraphSubTitle(value)
}