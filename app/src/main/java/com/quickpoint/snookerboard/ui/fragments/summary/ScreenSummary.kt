package com.quickpoint.snookerboard.ui.fragments.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.domain.DomainScore
import com.quickpoint.snookerboard.domain.emptyDomainScore
import com.quickpoint.snookerboard.domain.objects.MatchSettings
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings.crtPlayer
import com.quickpoint.snookerboard.domain.objects.MatchState
import com.quickpoint.snookerboard.domain.objects.getDisplayFrames
import com.quickpoint.snookerboard.navigateToRulesScreen
import com.quickpoint.snookerboard.ui.components.*
import com.quickpoint.snookerboard.ui.fragments.game.ScoreFrameContainer
import com.quickpoint.snookerboard.ui.fragments.game.ScoreMatchContainer
import com.quickpoint.snookerboard.ui.helpers.setGameStatsValue
import com.quickpoint.snookerboard.ui.helpers.setMatchPoints
import com.quickpoint.snookerboard.ui.helpers.setPercentage
import com.quickpoint.snookerboard.ui.helpers.setStatsTableBackground
import com.quickpoint.snookerboard.ui.theme.Black
import com.quickpoint.snookerboard.ui.theme.BrownDark
import com.quickpoint.snookerboard.ui.theme.White
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.GenericViewModelFactory
import com.quickpoint.snookerboard.utils.StatisticsType

@Composable
fun ScreenSummary(
    mainVm: MainViewModel
) {
    val summaryVm: SummaryViewModel = viewModel(factory = GenericViewModelFactory())

    val totalsA by summaryVm.totalsA.collectAsState()
    val totalsB by summaryVm.totalsB.collectAsState()
    val score: ArrayList<Pair<DomainScore, DomainScore>>? by summaryVm.score.observeAsState()

    mainVm.setupActionBarActions(emptyList(), emptyList()) { }

    LaunchedEffect(Unit) {
        mainVm.turnOffSplashScreen()
        MatchSettings.Settings.matchState = MatchState.SUMMARY
    }

    val crtPlayer by remember { mutableStateOf(crtPlayer) }

    FragmentContent {
        ComponentPlayerNames(crtPlayer)
        ContainerRow {
                ScoreFrameContainer("${totalsA.matchPoints}")
                ScoreMatchContainer(text = MatchSettings.Settings.getDisplayFrames())
                ScoreFrameContainer("${totalsB.matchPoints}")
        }
        score?.let { score ->
            ContainerRow(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(MaterialTheme.spacing.small))
                    .border(
                        width = MaterialTheme.spacing.border,
                        shape = RoundedCornerShape(MaterialTheme.spacing.small),
                        color = BrownDark
                    )
            ) {
                SummaryScoreRow(Pair(emptyDomainScore, emptyDomainScore))
                LazyColumn(Modifier.weight(1f)) {
                    itemsIndexed(score) { index, item ->
                        SummaryScoreRow(item = item, index = index, size = score.size)
                        HorizontalDivider()
                    }
                }
                SummaryScoreRow(Pair(totalsA, totalsB))
            }
        }
        MainButton("Go To Main Menu") { mainVm.navigateToRulesScreen() }
    }

    FragmentExtras {
        BackPressHandler { mainVm.navigateToRulesScreen() }
    }
}

@Composable
fun SummaryScoreRow(item: Pair<DomainScore, DomainScore>, index: Int = 0, size: Int = 1) = StandardRow(
    Modifier
        .background(setStatsTableBackground(index = index, size))
        .fillMaxWidth()
        .height(40.dp)
        .padding(4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    val textColor = if (size == 1) Black else White
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