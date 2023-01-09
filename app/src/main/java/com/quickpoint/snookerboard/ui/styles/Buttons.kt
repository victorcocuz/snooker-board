package com.quickpoint.snookerboard.ui.styles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ui.theme.Beige
import com.quickpoint.snookerboard.ui.theme.BrownDark
import com.quickpoint.snookerboard.ui.theme.BrownInactive
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.MatchToggleType
import com.quickpoint.snookerboard.utils.Toggle
import com.quickpoint.snookerboard.utils.getToggle
import timber.log.Timber

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
        TextNavParagraphSubTitle(text)
        Image(image, text)
        TextNavParagraph(price)
    }
}

@Composable
fun MatchToggleLayout(matchToggleType: MatchToggleType, mainVm: MainViewModel) {
    Timber.e("MatchToggleLayout")
    val toggles: List<Toggle> by mainVm.matchToggle.observeAsState(listOf(
        Toggle.AdvancedRules,
        Toggle.AdvancedStatistics,
        Toggle.AdvancedBreaks
    ))
    MatchToggleSwitchLayout(
        stringResource(
            when (matchToggleType) {
                MatchToggleType.ADVANCED_RULES -> R.string.f_nav_settings_toggle_advanced_rules_title
                MatchToggleType.ADVANCED_STATISTICS -> R.string.f_nav_settings_toggle_advanced_statistics_title
                MatchToggleType.ADVANCED_BREAKS -> R.string.f_nav_settings_toggle_advanced_breaks_title
            }
        ),
        stringResource(
            when (matchToggleType) {
                MatchToggleType.ADVANCED_RULES -> R.string.f_nav_settings_toggle_advanced_rules_description
                MatchToggleType.ADVANCED_STATISTICS -> R.string.f_nav_settings_toggle_advanced_statistics_description
                MatchToggleType.ADVANCED_BREAKS -> R.string.f_nav_settings_toggle_advanced_breaks_description
            }
        ),
        matchToggleType.getToggle().isEnabled
    ) { mainVm.updateMatchToggle(matchToggleType) }
}

@Composable
fun MatchToggleSwitchLayout(title: String, description: String, isChecked: Boolean, onClickChange: () -> Unit) {
    Timber.e(" isChecked $isChecked")
    Row(
        Modifier
            .fillMaxWidth(1f)
            .clickable(
                onClick = onClickChange
            )
    ) {
        Column(Modifier.weight(1f)) {
            TextNavParagraphSubTitle(title)
            TextNavParagraph(description)
        }
        Switch(
            modifier = Modifier.padding(MaterialTheme.spacing.medium, 0.dp, 0.dp, 0.dp),
            onCheckedChange = { onClickChange() },
            checked = isChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Beige,
                uncheckedThumbColor = Beige,
                checkedTrackColor = BrownDark,
                uncheckedTrackColor = BrownInactive,
                uncheckedBorderColor = BrownInactive,
            )
        )
    }
}