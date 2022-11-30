@file:Suppress("unused", "unused")

package com.quickpoint.snookerboard.utils

const val FACTOR_BALL_MATCH = 8
const val PRODUCT_COFFEE = "snookerboard_support_coffee"
const val PRODUCT_BEER = "snookerboard_support_beer"
const val PRODUCT_LUNCH = "snookerboard_support_lunch"

const val EMAIL_URI = "mailto:"
const val EMAIL_LOGS_SUBJECT = "Action Logs"

enum class BallAdapterType { MATCH, FOUL, BREAK }
enum class StatisticsType { FRAME_ID, HIGHEST_BREAK, FRAME_POINTS }
enum class PlayerTagType { MATCH, STATISTICS } // Create different player tags for display during match and for statistics
