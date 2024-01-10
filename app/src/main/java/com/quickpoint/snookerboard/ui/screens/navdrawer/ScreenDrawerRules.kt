package com.quickpoint.snookerboard.ui.screens.navdrawer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.SingleParagraph
import com.quickpoint.snookerboard.ui.components.TextHeadline

@Composable
fun ScreenDrawerRules() = FragmentContent(Modifier.verticalScroll(rememberScrollState())) {
    TextHeadline(stringResource(R.string.menu_drawer_rules))
    SingleParagraph(stringResource(R.string.dr_rules_tv_heading_01), stringResource(R.string.dr_rules_tv_body_01))
    SingleParagraph(stringResource(R.string.dr_rules_tv_heading_02), stringResource(R.string.dr_rules_tv_body_02))
    SingleParagraph(stringResource(R.string.dr_rules_tv_heading_03), stringResource(R.string.dr_rules_tv_body_03))
    SingleParagraph(stringResource(R.string.dr_rules_tv_heading_04), stringResource(R.string.dr_rules_tv_body_04))
    SingleParagraph(stringResource(R.string.dr_rules_tv_heading), stringResource(R.string.dr_rules_tv_body_05))
    SingleParagraph(stringResource(R.string.dr_rules_tv_heading_06), stringResource(R.string.dr_rules_tv_body_06))
    SingleParagraph(stringResource(R.string.dr_rules_tv_heading_07), stringResource(R.string.dr_rules_tv_body_07))
    SingleParagraph(stringResource(R.string.dr_rules_tv_heading_08), stringResource(R.string.dr_rules_tv_body_08))
}

@Preview(showBackground = true)
@Composable
fun FragmentDrawerRulesPreview() = Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colors.background
) { ScreenDrawerRules() }

