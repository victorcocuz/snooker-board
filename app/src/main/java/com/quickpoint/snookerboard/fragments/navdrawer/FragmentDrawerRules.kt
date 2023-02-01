package com.quickpoint.snookerboard.fragments.navdrawer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.compose.ui.styles.FragmentContent
import com.quickpoint.snookerboard.compose.ui.styles.TextHeadline
import com.quickpoint.snookerboard.compose.ui.styles.TextParagraph
import com.quickpoint.snookerboard.compose.ui.styles.TextParagraphSubTitle

@Composable
fun FragmentDrawerRules(
    navController: NavController,
) {
    FragmentContent(Modifier.verticalScroll(rememberScrollState())) {
        TextHeadline(stringResource(R.string.menu_drawer_rules))
        TextParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_01_ball_terminology))
        TextParagraph(stringResource(R.string.f_nav_rules_tv_body_01_ball_terminology))
        TextParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_02_ball_values))
        TextParagraph(stringResource(R.string.f_nav_rules_tv_body_02_ball_values))
        TextParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_03_snooker_terminology))
        TextParagraph(stringResource(R.string.f_nav_rules_tv_body_03_snooker_terminology))
        TextParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_04_fundamentals))
        TextParagraph(stringResource(R.string.f_nav_rules_tv_body_04_fundamentals))
        TextParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_05_foul_rules))
        TextParagraph(stringResource(R.string.f_nav_rules_tv_body_05_foul_rules))
        TextParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_06_game_end))
        TextParagraph(stringResource(R.string.f_nav_rules_tv_body_06_game_end))
        TextParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_07_winner))
        TextParagraph(stringResource(R.string.f_nav_rules_tv_body_07_winner))
        TextParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_08_more_info))
        TextParagraph(stringResource(R.string.f_nav_rules_tv_body_08_more_info))
    }
}

@Preview(showBackground = true)
@Composable
fun FragmentDrawerRulesPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        FragmentDrawerRules(rememberNavController())
    }
}
