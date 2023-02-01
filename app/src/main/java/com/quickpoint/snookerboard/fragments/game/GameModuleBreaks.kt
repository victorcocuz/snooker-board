package com.quickpoint.snookerboard.fragments.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.compose.ui.styles.BallView
import com.quickpoint.snookerboard.compose.ui.styles.StandardRow
import com.quickpoint.snookerboard.domain.DomainBreak
import com.quickpoint.snookerboard.domain.ballsList
import com.quickpoint.snookerboard.domain.displayShots
import com.quickpoint.snookerboard.utils.BallAdapterType
import com.quickpoint.snookerboard.utils.setBallBackground

@Composable
fun GameModuleBreaks(frameStack: List<DomainBreak>) {
    StandardRow {
        LazyColumn(
            Modifier
                .fillMaxHeight()
                .weight(0.5f)
        ) {
            items(frameStack.displayShots()) { domainBreak ->
                LazyVerticalGrid(
                    modifier = Modifier.height(100.dp),
                    columns = GridCells.Fixed(6), content = {
                    items(domainBreak.ballsList(0)) { domainBall ->
                        BallView(onContent = { it.setBallBackground(domainBall, BallAdapterType.BREAK) }
                        )
                    }
                })
            }
        }
        LazyColumn(
            Modifier
                .fillMaxHeight()
                .weight(0.5f)
                .background(Color.Blue)
        ) {

        }

    }
}