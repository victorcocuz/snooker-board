@file:Suppress("unused", "unused")

package com.quickpoint.snookerboard.utils

const val FACTOR_BALL_MATCH = 8
const val PRODUCT_COFFEE = "snookerboard_support_coffee"
const val PRODUCT_BEER = "snookerboard_support_beer"
const val PRODUCT_LUNCH = "snookerboard_support_lunch"

const val GOOGLE_FORM_URI = "https://forms.gle/VgsbELD5HxDxkHmZ7"
const val EMAIL_BASE_URI = "mailto:"
const val EMAIL_SUBJECT_LOGS = "Action Logs"
const val EMAIL_SUBJECT_IMPROVE = "App Improvement Suggestions"

enum class BallAdapterType { MATCH, FOUL, BREAK }
enum class StatisticsType { FRAME_ID, HIGHEST_BREAK, FRAME_POINTS }
enum class PlayerTagType { MATCH, STATISTICS } // Create different player tags for display during match and for statistics