package com.quickpoint.snookerboard.ui.screens.navdrawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.models.DomainAppReleaseDetails
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.TextHeadline
import com.quickpoint.snookerboard.ui.components.TextParagraph
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.components.TextTitle

@Composable
fun ScreenDrawerAbout() = FragmentContent {

    val navDrawerViewModel: NavDrawerViewModel = hiltViewModel()
    var releaseDetailsState by remember { mutableStateOf<List<DomainAppReleaseDetails>>(emptyList()) }

    LaunchedEffect(Unit) {
        navDrawerViewModel.releaseDetails.collect { releaseDetails ->
            releaseDetailsState = releaseDetails
        }
    }

    LazyColumn(modifier = Modifier.weight(1f)) {
        item {
            TextHeadline(stringResource(R.string.menu_drawer_about))
            TextParagraph(stringResource(R.string.dr_about_tv_description))
            TextTitle(stringResource(R.string.dr_about_tv_version_title))
        }
        items(releaseDetailsState) { releaseDetails ->
            Column {
                TextSubtitle(releaseDetails.versionNumber)
                releaseDetails.notes.forEach {
                    TextParagraph(it)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FragmentDrawerAboutPreview() = ScreenDrawerAbout()

