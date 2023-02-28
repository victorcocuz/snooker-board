package com.quickpoint.snookerboard.ui.fragments.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.domain.DomainFrame
import com.quickpoint.snookerboard.domain.availablePoints
import com.quickpoint.snookerboard.domain.frameScoreDiff
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.getDisplayFrames
import com.quickpoint.snookerboard.ui.components.StandardRow
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.components.TextTitle
import com.quickpoint.snookerboard.ui.theme.Beige
import com.quickpoint.snookerboard.ui.theme.BrownMedium
import com.quickpoint.snookerboard.ui.theme.spacing

@Composable
fun GameModuleScore(domainFrame: DomainFrame) {
    val score = domainFrame.score
    if (score.size == 2) {
        StandardRow(modifier = Modifier.fillMaxWidth()) {
            ScoreFrameContainer("${score[0].framePoints}")
            ScoreMatchContainer(text = "${score[0].matchPoints} ${Settings.getDisplayFrames()} ${score[1].matchPoints}")
            ScoreFrameContainer("${score[1].framePoints}")
        }
        ScoreProgressBar(
            description = "Remaining",
            progress = domainFrame.ballStack.availablePoints().toFloat() / domainFrame.frameMax,
            value = "${domainFrame.ballStack.availablePoints()}"
        )
        ScoreProgressBar(
            description = "Difference",
            progress = domainFrame.score.frameScoreDiff().toFloat() / domainFrame.frameMax,
            value = "${domainFrame.score.frameScoreDiff()}"
        )
    }
}

@Composable
fun ScoreFrameContainer(text: String) {
    TextTitle(text, color = Beige)
}

@Composable
fun ScoreMatchContainer(text: String) {
    TextSubtitle(text, color = Beige)
}

@Composable
fun ScoreProgressBar(
    description: String,
    progress: Float,
    value: String,
) = StandardRow(
    Modifier
        .fillMaxWidth()
        .padding(0.dp, 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    TextSubtitle(
        modifier = Modifier.width(100.dp),
        text = description,
         color = Beige
    )
    Spacer(Modifier.width(4.dp))
    LinearProgressIndicator(
        modifier = Modifier
            .height(14.dp)
            .weight(1f)
            .clip(RoundedCornerShape(MaterialTheme.spacing.extraSmall)),
        color = Beige,
        trackColor = BrownMedium,
        progress = progress
    )
    Spacer(Modifier.width(4.dp))
    TextSubtitle(
        modifier = Modifier.width(40.dp),
        text = value,
        textAlign = TextAlign.Center,
        color = Beige
    )
}