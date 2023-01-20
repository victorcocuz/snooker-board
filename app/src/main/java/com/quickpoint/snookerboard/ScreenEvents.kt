package com.quickpoint.snookerboard

import com.quickpoint.snookerboard.utils.MatchAction

sealed class ScreenEvents {
    data class ShowSnackbar(val message: String) : ScreenEvents()
    data class Navigate(val route: String) : ScreenEvents()
    data class SnookerEvent(val action: MatchAction): ScreenEvents()
}