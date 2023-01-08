package com.quickpoint.snookerboard.fragments.navdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ui.styles.*
import com.quickpoint.snookerboard.ui.theme.SnookerBoardTheme

class NavRulesFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                SnookerBoardTheme {
                    GenericSurface {
                        FragmentNavRules()
                    }
                }
            }
        }
    }
}

@Composable
fun FragmentNavRules() {
    FragmentColumn(Modifier.verticalScroll(rememberScrollState())) {
        TextNavHeadline(stringResource(R.string.menu_drawer_rules))
        TextNavParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_01_ball_terminology))
        TextNavParagraph(stringResource(R.string.f_nav_rules_tv_body_01_ball_terminology))
        TextNavParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_02_ball_values))
        TextNavParagraph(stringResource(R.string.f_nav_rules_tv_body_02_ball_values))
        TextNavParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_03_snooker_terminology))
        TextNavParagraph(stringResource(R.string.f_nav_rules_tv_body_03_snooker_terminology))
        TextNavParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_04_fundamentals))
        TextNavParagraph(stringResource(R.string.f_nav_rules_tv_body_04_fundamentals))
        TextNavParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_05_foul_rules))
        TextNavParagraph(stringResource(R.string.f_nav_rules_tv_body_05_foul_rules))
        TextNavParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_06_game_end))
        TextNavParagraph(stringResource(R.string.f_nav_rules_tv_body_06_game_end))
        TextNavParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_07_winner))
        TextNavParagraph(stringResource(R.string.f_nav_rules_tv_body_07_winner))
        TextNavParagraphSubTitle(stringResource(R.string.f_nav_rules_tv_heading_08_more_info))
        TextNavParagraph(stringResource(R.string.f_nav_rules_tv_body_08_more_info))
    }
}

@Preview(showBackground = true)
@Composable
fun FragmentNavRulesPreview() {
    FragmentNavRules()
}
