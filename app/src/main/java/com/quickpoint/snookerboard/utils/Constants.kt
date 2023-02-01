package com.quickpoint.snookerboard.utils

object Constants {
    const val FACTOR_BALL_MATCH = 8
    const val PRODUCT_COFFEE = "snookerboard_support_coffee"
    const val PRODUCT_BEER = "snookerboard_support_beer"
    const val PRODUCT_LUNCH = "snookerboard_support_lunch"

    const val GOOGLE_FORM_URI = "https://forms.gle/VgsbELD5HxDxkHmZ7"
    const val EMAIL_BASE_URI = "mailto:"
    const val EMAIL_SUBJECT_LOGS = "Action Logs"
    const val EMAIL_SUBJECT_IMPROVE = "App Improvement Suggestions"

    // Navigation Drawer IDs
    const val NAV_ID_DRAWER_RULES = "id_drawer_rules"
    const val NAV_ID_DRAWER_IMPROVE = "id_drawer_improve"
    const val NAV_ID_DRAWER_SUPPORT = "id_drawer_support"
    const val NAV_ID_DRAWER_SETTINGS = "id_drawer_settings"
    const val NAV_ID_DRAWER_ABOUT = "id_drawer_about"

    // Navigation Routes
    const val ROUTE_SCREEN_MAIN = "screen_main"
    const val ROUTE_SCREEN_RULES = "screen_rules"
    const val ROUTE_SCREEN_GAME = "screen_fragment_game"
    const val ROUTE_SCREEN_SUMMARY = "screen_fragment_summary"
    const val ROUTE_SCREEN_ABOUT = "screen_drawer_about"
    const val ROUTE_SCREEN_DRAWER_IMPROVE = "screen_drawer_improve"
    const val ROUTE_SCREEN_DRAWER_RULES = "screen_drawer_rules"
    const val ROUTE_SCREEN_DRAWER_SETTINGS = "screen_drawer_settings"
    const val ROUTE_SCREEN_DRAWER_SUPPORT = "screen_drawer_support"
    const val ROUTE_SCREEN_DIALOG_GENERIC = "screen_dialog_generic"
    const val ROUTE_SCREEN_DIALOG_FOUL = "screen_dialog_foul"
}

enum class BallAdapterType { MATCH, FOUL, BREAK }
enum class StatisticsType { FRAME_ID, HIGHEST_BREAK, FRAME_POINTS }
enum class PlayerTagType { MATCH, STATISTICS } // Create different player tags for display during match and for statistics
