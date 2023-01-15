package com.quickpoint.snookerboard.fragments.navdrawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.compose.ui.styles.FragmentColumn
import com.quickpoint.snookerboard.compose.ui.styles.TextNavHeadline
import com.quickpoint.snookerboard.compose.ui.styles.TextNavParagraph
import com.quickpoint.snookerboard.compose.ui.styles.TextNavParagraphSubTitle
import com.quickpoint.snookerboard.compose.ui.styles.TextNavTitle

@Composable
fun FragmentDrawerAbout(
    navController: NavController
) {
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
fun FragmentDrawerAboutPreview() {
    FragmentDrawerAbout(rememberNavController())
}
