package com.quickpoint.snookerboard.ui.screens.navdrawer

import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.data.database.SnookerDatabase
import com.quickpoint.snookerboard.data.repository.AppReleaseDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavDrawerViewModel @Inject constructor(
    val database: SnookerDatabase,
    versionsRepository: AppReleaseDetailsRepository,
) : ViewModel() {
    val releaseDetails = versionsRepository.releaseDetails
}