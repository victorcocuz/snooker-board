package com.quickpoint.snookerboard.ui.screens.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.core.utils.StatisticsType
import com.quickpoint.snookerboard.domain.models.DomainScore
import com.quickpoint.snookerboard.domain.models.emptyDomainScore
import com.quickpoint.snookerboard.domain.utils.MatchState
import com.quickpoint.snookerboard.domain.utils.MatchSettings
import com.quickpoint.snookerboard.domain.utils.MatchSettings.Companion.crtPlayer
import com.quickpoint.snookerboard.navigateToRulesScreen
import com.quickpoint.snookerboard.ui.components.*
import com.quickpoint.snookerboard.ui.screens.game.ScoreFrameContainer
import com.quickpoint.snookerboard.ui.screens.game.ScoreMatchContainer
import com.quickpoint.snookerboard.ui.helpers.setGameStatsValue
import com.quickpoint.snookerboard.ui.helpers.setMatchPoints
import com.quickpoint.snookerboard.ui.helpers.setPercentage
import com.quickpoint.snookerboard.ui.helpers.setStatsTableBackground
import com.quickpoint.snookerboard.ui.theme.Black
import com.quickpoint.snookerboard.ui.theme.BrownDark
import com.quickpoint.snookerboard.ui.theme.White
import com.quickpoint.snookerboard.ui.theme.spacing

@Composable
fun ScreenSummary() {
    val mainVm = LocalView.current.findViewTreeViewModelStoreOwner().let { hiltViewModel<MainViewModel>(it!!) }
    val summaryVm = hiltViewModel<SummaryViewModel>()

    val totalsA by summaryVm.totalsA.collectAsState()
    val totalsB by summaryVm.totalsB.collectAsState()
    val score by summaryVm.score.collectAsState(null)

    mainVm.setupActionBarActions(emptyList(), emptyList()) { }

    LaunchedEffect(Unit) {
        mainVm.turnOffSplashScreen()
        MatchSettings.matchState = MatchState.SUMMARY
    }

    val crtPlayer by remember { mutableStateOf(crtPlayer) }

    FragmentContent {
        ComponentPlayerNames(crtPlayer)
        ContainerRow {
            ScoreFrameContainer("${totalsA.matchPoints}")
            ScoreMatchContainer(text = MatchSettings.getDisplayFrames())
            ScoreFrameContainer("${totalsB.matchPoints}")
        }
        score?.let { score ->
            ContainerColumn(
                Modifier
                    .clip(RoundedCornerShape(MaterialTheme.spacing.small))
                    .border(
                        width = MaterialTheme.spacing.border,
                        shape = RoundedCornerShape(MaterialTheme.spacing.small),
                        color = BrownDark
                    )
            ) {
                SummaryScoreRow(Pair(emptyDomainScore, emptyDomainScore), isLabel = true)
                LazyColumn {
                    itemsIndexed(score) { index, item ->
                        SummaryScoreRow(item = item, index = index, isLabel = false)
                        if (index < score.size) HorizontalDivider()
                    }
                }
                SummaryScoreRow(Pair(totalsA, totalsB), isLabel = true)
            }
        }
        Spacer(Modifier.weight(1f))
        MainButton("Go To Main Menu") { mainVm.navigateToRulesScreen() }
    }

    FragmentExtras {
        BackPressHandler { mainVm.navigateToRulesScreen() }
    }
}

@Composable
fun SummaryScoreRow(item: Pair<DomainScore, DomainScore>, index: Int = 0, isLabel: Boolean) = StandardRow(
    Modifier
        .background(setStatsTableBackground(index, isLabel))
        .fillMaxWidth()
        .height(40.dp)
        .padding(4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    val textColor = if (isLabel) Black else White
    CentredScore(setGameStatsValue(StatisticsType.HIGHEST_BREAK, item.first.highestBreak), textColor)
    CentredScore(setPercentage(item.first.successShots, item.first.missedShots), textColor)
    CentredScore(setGameStatsValue(StatisticsType.FRAME_POINTS, item.first.highestBreak), textColor)
    CentredScore(setMatchPoints(item.first.matchPoints, item.second.matchPoints), textColor)
    CentredScore(setGameStatsValue(StatisticsType.FRAME_POINTS, item.second.highestBreak), textColor)
    CentredScore(setPercentage(item.second.successShots, item.second.missedShots), textColor)
    CentredScore(setGameStatsValue(StatisticsType.HIGHEST_BREAK, item.second.highestBreak), textColor)
}

@Composable
fun RowScope.CentredScore(text: String, textColor: Color) = Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
    TextSubtitle(text = text, textAlign = TextAlign.Center, color = textColor)
}