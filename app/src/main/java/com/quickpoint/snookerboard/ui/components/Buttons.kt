package com.quickpoint.snookerboard.ui.components

import android.widget.ImageButton
import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.base.Event
import com.quickpoint.snookerboard.domain.DomainBall
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.getHandicap
import com.quickpoint.snookerboard.domain.objects.getSettingsTextIdByKeyAndValue
import com.quickpoint.snookerboard.domain.objects.isSettingsButtonSelected
import com.quickpoint.snookerboard.ui.fragments.rules.RulesViewModel
import com.quickpoint.snookerboard.ui.theme.*
import com.quickpoint.snookerboard.utils.BallAdapterType
import com.quickpoint.snookerboard.utils.K_INT_MATCH_HANDICAP_FRAME
import kotlinx.coroutines.launch


@Composable
fun ClickableText(text: String, onClick: () -> Unit) = Button(
    modifier = Modifier, onClick = { onClick() }, shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall)
) {
    Text(textAlign = TextAlign.Center, text = text.uppercase(), style = MaterialTheme.typography.labelLarge)
}

@Composable
fun RulesHandicapLabel(
    rulesVm: RulesViewModel,
    key: String,
) {
    val rulesUpdateAction by rulesVm.eventMatchSettingsChange.collectAsState(Event(Unit))
    var handicap by remember { mutableStateOf(0) }
    LaunchedEffect(rulesUpdateAction) {
        handicap = when (key) {
            K_INT_MATCH_HANDICAP_FRAME -> Settings.handicapFrame
            else -> Settings.handicapMatch
        }
    }
    Text(
        modifier = Modifier.width(60.dp),
        textAlign = TextAlign.Center,
        text = "${getHandicap(handicap, -1)} - ${getHandicap(handicap, 1)}"
    )
}

@Composable
fun ButtonStandardHoist(
    rulesVm: RulesViewModel,
    key: String,
    value: Int = -2,
) {
    val rulesUpdateAction by rulesVm.eventMatchSettingsChange.collectAsState(Event(Unit))
    LaunchedEffect(key1 = rulesUpdateAction, block = {}) // Used to refresh composition when rules change
    ButtonStandard(
        text = stringResource(getSettingsTextIdByKeyAndValue(key, value)),
        isSelected = isSettingsButtonSelected(key, value),
        onClick = { rulesVm.onMatchSettingsChange(key, value) }
    )
}

@Composable
fun ButtonStandard(
    modifier: Modifier = Modifier,
    text: String,
    height: Dp = 40.dp,
    isSelected: Boolean = false,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) = OutlinedButton(
    modifier = modifier.height(height),
    contentPadding = PaddingValues(20.dp, 8.dp),
    shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall),
    border = BorderStroke(1.dp, if (isSelected) Beige else Black),
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Green else CreamBright),
    enabled = isEnabled
) {
    Text(
        text = text.uppercase(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelLarge.copy(color = if (isSelected) White else Black)
    )
}


@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    text: String,
    painter: Painter,
    isSelected: Boolean = false,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
            .padding(2.dp)
            .size(60.dp),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall),
        border = BorderStroke(1.dp, if (isSelected && isEnabled) Beige else Black),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected && isEnabled) Green else Beige
        ),
        enabled = isEnabled
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                tint = if (isSelected) White else Black,
                modifier = Modifier.size(32.dp),
                painter = painter, contentDescription = null
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(color = if (isSelected) White else Black)
            )
        }
    }
}

@Composable
fun BallView(
    modifier: Modifier = Modifier,
    ball: DomainBall,
    ballAdapterType: BallAdapterType,
    isBallSelected: Boolean = false,
    text: String = "",
    onClick: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    Box(contentAlignment = Alignment.Center) {
        AndroidView(
            modifier = modifier
                .aspectRatio(1f)
                .padding(2.dp),
            factory = { context ->
                ImageButton(context).apply {
                    setOnClickListener { coroutineScope.launch { onClick() } }
                    isSelected = isBallSelected
                }
            })
        { it.setBallBackground(ball, ballAdapterType, isBallSelected) }
        TextBallInfo(text)
    }
}

fun ImageView.setBallBackground(item: DomainBall?, ballAdapterType: BallAdapterType, isBallSelected: Boolean) {
    item?.let {
        // TODO: animate ball changes
//        if (ballAdapterType == MATCH) startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_short))
        val ripple = ballAdapterType != BallAdapterType.BREAK
        setBackgroundResource(
            when (item) {
                // COLOR is for potting extra red
                is RED, is COLOR -> if (isBallSelected) R.drawable.ic_ball_red_pressed else if (ripple) R.drawable.ic_ball_red else R.drawable.ic_ball_red_normal
                is YELLOW -> if (isBallSelected) R.drawable.ic_ball_yellow_pressed else if (ripple) R.drawable.ic_ball_yellow else R.drawable.ic_ball_yellow_normal
                is GREEN -> if (isBallSelected) R.drawable.ic_ball_green_pressed else if (ripple) R.drawable.ic_ball_green else R.drawable.ic_ball_green_normal
                is BROWN -> if (isBallSelected) R.drawable.ic_ball_brown_pressed else if (ripple) R.drawable.ic_ball_brown else R.drawable.ic_ball_brown_normal
                is BLUE -> if (isBallSelected) R.drawable.ic_ball_blue_pressed else if (ripple) R.drawable.ic_ball_blue else R.drawable.ic_ball_blue_normal
                is PINK -> if (isBallSelected) R.drawable.ic_ball_pink_pressed else if (ripple) R.drawable.ic_ball_pink else R.drawable.ic_ball_pink_normal
                is BLACK -> if (isBallSelected) R.drawable.ic_ball_black_pressed else if (ripple) R.drawable.ic_ball_black else R.drawable.ic_ball_black_normal
                is FREEBALL -> if (isBallSelected) R.drawable.ic_ball_free_pressed else if (ripple) R.drawable.ic_ball_free else R.drawable.ic_ball_free_normal
                is NOBALL -> if (ripple) R.drawable.ic_ball_miss else R.drawable.ic_ball_miss_normal
                else -> if (isBallSelected) R.drawable.ic_ball_white_pressed else if (ripple) R.drawable.ic_ball_white else R.drawable.ic_ball_white_normal
            }
        )
    }
}

@Composable
fun MainButton(text: String, onclick: () -> Unit) = Button(
    shape = RoundedCornerShape(48.dp),
    onClick = onclick,
) { TextSubtitle(text.uppercase()) }
