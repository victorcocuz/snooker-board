package com.quickpoint.snookerboard.fragments.game

import android.widget.ImageButton
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.compose.ui.styles.ButtonStandard
import com.quickpoint.snookerboard.compose.ui.styles.StandardRow
import com.quickpoint.snookerboard.compose.ui.styles.ToggleButton
import com.quickpoint.snookerboard.compose.ui.theme.Beige
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.PotType
import com.quickpoint.snookerboard.domain.listOfBallsColors
import com.quickpoint.snookerboard.utils.BallAdapterType
import com.quickpoint.snookerboard.utils.setBallBackground

@Composable
fun GameModuleActions(gameVm: GameViewModel) {
    Divider(color = Beige)
    StandardRow {
        ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_foul), 0.21f) {}
        ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_safe), 0.21f) {}
        ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_safe_miss), 0.3f) {}
        ButtonActionHoist(text = stringResource(R.string.l_game_actions_btn_snooker), 0.28f) {}
    }
    Divider(color = Beige)
    StandardRow(modifier = Modifier.height(80.dp)) {
        val ballList: MutableList<DomainBall>? = gameVm.displayFrame.value?.ballStack
        val balls = when (ballList?.lastOrNull()) {
            is DomainBall.COLOR -> listOfBallsColors
            is DomainBall.WHITE -> listOf(DomainBall.NOBALL())
            null -> listOf()
            else -> listOf(ballList.last())
        }
        LazyRow(
            Modifier
                .fillMaxHeight()
                .weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(balls) { index, ball ->
                AndroidView(
                    factory = { context ->
                        ImageButton(context).apply {
                            setOnClickListener {
                                gameVm.assignPot(PotType.TYPE_HIT, ball)
                            }
                        }
                    })
                { it.setBallBackground(ball, BallAdapterType.MATCH) }
            }
        }
        Spacer(Modifier.width(8.dp))
        Divider(
            Modifier
                .fillMaxHeight()
                .width(1.dp), color = Beige
        )
        Spacer(Modifier.width(8.dp))
        AndroidView(factory = { ImageButton(it) }) {
            it.setBackgroundResource(R.drawable.ic_ball_miss)
        }
    }
    Divider(color = Beige)
    StandardRow {
        ToggleButton(
            text = stringResource(R.string.l_game_actions_btn_long),
            painter = painterResource(R.drawable.ic_temp_shot_type_long)
        ) {}
        ToggleButton(
            text = stringResource(R.string.l_game_actions_btn_rest),
            painter = painterResource(R.drawable.ic_temp_shot_type_rest)
        ) {}
    }
}

@Composable
fun RowScope.ButtonActionHoist(
    text: String,
    weight: Float,
    onAction: () -> Unit,
) {
    ButtonStandard(
        Modifier.weight(weight),
        text = text,
        onClick = { onAction() }
    )
}