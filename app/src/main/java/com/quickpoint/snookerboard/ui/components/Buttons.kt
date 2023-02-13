package com.quickpoint.snookerboard.ui.components

import android.widget.ImageButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.quickpoint.snookerboard.base.Event
import com.quickpoint.snookerboard.domain.objects.MatchSettings.Settings
import com.quickpoint.snookerboard.domain.objects.getHandicap
import com.quickpoint.snookerboard.domain.objects.getSettingsTextIdByKeyAndValue
import com.quickpoint.snookerboard.domain.objects.isSettingsButtonSelected
import com.quickpoint.snookerboard.ui.fragments.rules.RulesViewModel
import com.quickpoint.snookerboard.ui.theme.*
import com.quickpoint.snookerboard.utils.K_INT_MATCH_HANDICAP_FRAME


@Composable
fun ClickableText(text: String, onClick: () -> Unit) = Button(
    modifier = Modifier, onClick = { onClick() }, shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall)
) {
    Text(
        textAlign = TextAlign.Center, text = text.uppercase(), style = MaterialTheme.typography.labelLarge
    )
}

@Composable
fun ButtonDonate(text: String, price: String, image: Painter, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .width(100.dp)
            .height(100.dp)
            .background(MaterialTheme.colorScheme.tertiary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextSubtitle(text)
        Image(image, text)
        TextParagraph(price)
    }
}

@Composable
fun RulesHandicapLabel(
    rulesVm: RulesViewModel,
    key: String,
) {
    val rulesUpdateAction by rulesVm.eventMatchSettingsChange.collectAsState(Event(Unit))
    var handicap by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = rulesUpdateAction) {
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
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall),
        border = BorderStroke(1.dp, if (isSelected) Beige else Black),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Green else CreamBright
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(color = if (isSelected) White else Black)
        )
    }
}

@Composable
fun ToggleButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    painter: Painter,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall),
        border = BorderStroke(1.dp, if (isSelected) Beige else Black),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Green else CreamBright
        )
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Icon(painter = painter, contentDescription = null)
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(color = if (isSelected) White else Black)
            )
        }
    }
}

@Composable
fun BallView(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onContent: (ImageButton) -> Unit,
) = AndroidView(
    modifier = modifier.aspectRatio(1f),
    factory = { context ->
        ImageButton(context).apply { setOnClickListener { onClick() } }
    })
{
    onContent(it)
}