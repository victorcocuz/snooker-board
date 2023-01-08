package com.quickpoint.snookerboard.fragments.navdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ui.styles.FragmentColumn
import com.quickpoint.snookerboard.ui.styles.GenericSurface
import com.quickpoint.snookerboard.ui.styles.TextNavHeadline
import com.quickpoint.snookerboard.ui.styles.TextNavParagraph
import com.quickpoint.snookerboard.ui.styles.TextNavParagraphSubTitle
import com.quickpoint.snookerboard.ui.styles.TextNavTitle
import com.quickpoint.snookerboard.ui.theme.SnookerBoardTheme

class NavAboutFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SnookerBoardTheme {
                    GenericSurface {
                        FragmentNavAbout()
                    }
                }
            }
        }
    }
}

@Composable
fun FragmentNavAbout() {
    FragmentColumn {
        val versions = listOf(
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_11_list),
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_10_list),
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_9_list),
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_8_list),
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_7_list),
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_6_list),
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_5_list),
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_4_list),
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_3_list),
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_2_list),
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_1_list),
            stringArrayResource(R.array.f_nav_about_tv_version_1_0_0_list)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                TextNavHeadline(stringResource(R.string.menu_drawer_about))
                TextNavParagraph(stringResource(R.string.f_nav_about_tv_description))
                TextNavTitle(stringResource(R.string.f_nav_about_tv_version_title))
            }
            itemsIndexed(versions) { index, version ->
                Column {
                    version.forEachIndexed { index, s ->
                        if (index == 0) TextNavParagraphSubTitle(s)
                        else TextNavParagraph(s)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FragmentNavAboutPreview() {
    FragmentNavAbout()
}
