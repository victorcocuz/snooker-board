package com.quickpoint.snookerboard.core

import com.quickpoint.snookerboard.core.utils.MatchAction

sealed class ScreenEvents {
    data class SnackAction(val action: MatchAction): ScreenEvents()
    data class ShowSnackbar(val message: String) : ScreenEvents()
    data class Navigate(val route: String) : ScreenEvents()
    data class SnookerEvent(val action: MatchAction): ScreenEvents()
}