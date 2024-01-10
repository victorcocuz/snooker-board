package com.quickpoint.snookerboard.ui.screens.navdrawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ui.components.*

@Composable
fun ScreenDrawerAbout() = FragmentContent {
        val versions = listOf(
            stringArrayResource(R.array.dr_about_tv_version_1_0_11_list),
            stringArrayResource(R.array.dr_about_tv_version_1_0_10_list),
            stringArrayResource(R.array.dr_about_tv_version_1_0_9_list),
            stringArrayResource(R.array.dr_about_tv_version_1_0_8_list),
            stringArrayResource(R.array.dr_about_tv_version_1_0_7_list),
            stringArrayResource(R.array.dr_about_tv_version_1_0_6_list),
            stringArrayResource(R.array.dr_about_tv_version_1_0_5_list),
            stringArrayResource(R.array.dr_about_tv_version_1_0_4_list),
            stringArrayResource(R.array.dr_about_tv_version_1_0_3_list),
            stringArrayResource(R.array.dr_about_tv_version_1_0_2_list),
            stringArrayResource(R.array.dr_about_tv_version_1_0_1_list),
            stringArrayResource(R.array.dr_about_tv_version_1_0_0_list)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                TextHeadline(stringResource(R.string.menu_drawer_about))
                TextParagraph(stringResource(R.string.dr_about_tv_description))
                TextTitle(stringResource(R.string.dr_about_tv_version_title))
            }
            items(versions) { version ->
                Column {
                    version.forEachIndexed { index, s ->
                        if (index == 0) TextSubtitle(s)
                        else TextParagraph(s)
                    }
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun FragmentDrawerAboutPreview() = ScreenDrawerAbout()
