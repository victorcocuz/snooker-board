package com.quickpoint.snookerboard.domain.utils

import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.data.*
import com.quickpoint.snookerboard.domain.utils.MatchState.*

enum class MatchState { RULES_IDLE, GAME_IN_PROGRESS, SUMMARY }

fun getHandicap(value: Int, pol: Int) = if (pol * value > 0) pol * value else 0
fun getMatchStateFromOrdinal(ordinal: Int): MatchState = when (ordinal) {
    0 -> RULES_IDLE
    1 -> GAME_IN_PROGRESS
    else -> SUMMARY
}

fun isSettingsButtonSelected(key: String, value: Int): Boolean = value == when (key) {
    K_INT_MATCH_STARTING_PLAYER -> MatchSettings.startingPlayer
    K_INT_MATCH_AVAILABLE_FRAMES -> MatchSettings.availableFrames
    K_INT_MATCH_AVAILABLE_REDS -> MatchSettings.availableReds
    K_INT_MATCH_FOUL_MODIFIER -> MatchSettings.foulModifier
    else -> -1000
}

fun getSettingsTextIdByKeyAndValue(key: String, value: Int): Int = when {
    key == K_INT_MATCH_STARTING_PLAYER && value == 0 -> R.string.l_rules_main_tv_player_a_label
    key == K_INT_MATCH_STARTING_PLAYER && value == 2 -> R.string.l_rules_main_btn_breaks_coin_toss
    key == K_INT_MATCH_STARTING_PLAYER && value == 1 -> R.string.l_rules_main_tv_player_b_label
    key == K_INT_MATCH_AVAILABLE_REDS && value == 6 -> R.string.l_rules_extra_btn_reds_six
    key == K_INT_MATCH_AVAILABLE_REDS && value == 10 -> R.string.l_rules_extra_btn_reds_ten
    key == K_INT_MATCH_AVAILABLE_REDS && value == 15 -> R.string.l_rules_extra_btn_reds_fifteen
    key == K_INT_MATCH_FOUL_MODIFIER && value == -3 -> R.string.l_rules_extra_btn_foul_one
    key == K_INT_MATCH_FOUL_MODIFIER && value == -2 -> R.string.l_rules_extra_btn_foul_two
    key == K_INT_MATCH_FOUL_MODIFIER && value == -1 -> R.string.l_rules_extra_btn_foul_three
    key == K_INT_MATCH_FOUL_MODIFIER && value == 0 -> R.string.l_rules_extra_btn_foul_four
    key == K_INT_MATCH_HANDICAP_FRAME && value == -10 -> R.string.l_rules_extra_btn_handicap_frame_less
    key == K_INT_MATCH_HANDICAP_FRAME && value == 10 -> R.string.l_rules_extra_btn_handicap_frame_more
    key == K_INT_MATCH_HANDICAP_MATCH && value == -1 -> R.string.l_rules_extra_btn_handicap_match_less
    else -> R.string.l_rules_extra_btn_handicap_match_more // key == K_INT_MATCH_HANDICAP_MATCH && value == 1
}