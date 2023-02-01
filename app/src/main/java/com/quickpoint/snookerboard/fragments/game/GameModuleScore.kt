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
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.availablePoints
import com.quickpoint.snookerboard.domain.frameScoreDiff
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.getDisplayFrames

@Composable // Score Chapter
fun GameModuleScore(domainFrame: DomainFrame) {
    val score = domainFrame.score
    if (score.size == 2) {
        StandardRow(modifier = Modifier.padding(bottom = 8.dp)) {
            ScoreFrameContainer("${score[0].framePoints}")
            ScoreMatchContainer(text = "${score[0].matchPoints} ${Settings.getDisplayFrames()} ${score[1].matchPoints}")
            ScoreFrameContainer("${score[1].framePoints}")
        }
        ScoreProgressBar(description = "Remaining", progress = domainFrame.ballStack.availablePoints().toFloat() / domainFrame.frameMax, value = "${domainFrame.ballStack.availablePoints()}")
        ScoreProgressBar(description = "Difference", progress = domainFrame.score.frameScoreDiff().toFloat() / domainFrame.frameMax, value = "${domainFrame.score.frameScoreDiff()}")
    }
}

@Composable
fun ScoreFrameContainer(text: String) {
    TextTitle(text)
}

@Composable
fun ScoreMatchContainer(text: String) {
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