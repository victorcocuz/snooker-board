package com.quickpoint.snookerboard.fragments.navdrawer

import androidx.appcompat.widget.SwitchCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.compose.ui.styles.FragmentColumn
import com.quickpoint.snookerboard.compose.ui.styles.TextNavParagraph
import com.quickpoint.snookerboard.compose.ui.styles.TextNavParagraphSubTitle
import com.quickpoint.snookerboard.compose.ui.theme.spacing
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchToggleType
import com.quickpoint.snookerboard.utils.getToggle

@Composable
fun FragmentDrawerSettings(
    navController: NavController,
    mainVm: MainViewModel
) {
    FragmentColumn {
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        SettingsToggleHoist(MatchToggleType.ADVANCED_RULES, mainVm)
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        SettingsToggleHoist(MatchToggleType.ADVANCED_STATISTICS, mainVm)
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        SettingsToggleHoist(MatchToggleType.ADVANCED_BREAKS, mainVm)
    }
}

@Composable
fun SettingsToggleHoist(matchToggleType: MatchToggleType, mainVm: MainViewModel) {
    val togglesEvent: Event<Unit> by mainVm.matchToggleEvent.observeAsState(Event(Unit))
    togglesEvent.getContentIfNotHandled() // Simply used to call the observer, only to trigger composition
    SettingsToggle(
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
fun SettingsToggle(title: String, description: String, isChecked: Boolean, onClickChange: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth(1f)
            .clickable(onClick = onClickChange)
            .padding(MaterialTheme.spacing.small, 0.dp, MaterialTheme.spacing.small, MaterialTheme.spacing.small)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            TextNavParagraphSubTitle(title)
            TextNavParagraph(description)
        }

        AndroidView(
            modifier = Modifier
                .padding(MaterialTheme.spacing.medium, MaterialTheme.spacing.small, 0.dp, 0.dp)
                .align(Alignment.CenterVertically),
            factory = { context ->
                SwitchCompat(context).apply {
                    isClickable = false
                    setTrackResource(R.drawable.toggle_slider_track)
                    setThumbResource(R.drawable.toggle_slider_thumb)
                    setChecked(isChecked)
                }
            },
            update = {
                it.isChecked = isChecked
            })
    }
}

//@Preview(showBackground = true)
//@Composable
//fun FragmentDrawerSettingsPreview() {
//    FragmentDrawerSettings(rememberNavController())
//}