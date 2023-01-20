package com.quickpoint.snookerboard.domain.objects

import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.PotAction
import com.quickpoint.snookerboard.domain.PotAction.*
import com.quickpoint.snookerboard.utils.*
import com.quickpoint.snookerboard.utils.MatchAction.FRAME_START_NEW
import timber.log.Timber

enum class MatchState { RULES_IDLE, RULES_PENDING, GAME_IN_PROGRESS, GAME_SAVED, SUMMARY, NONE }

// The DOMAIN rules
sealed class MatchSettings(
    var matchState: MatchState,
    var uniqueId: Long,
    var availableFrames: Int,
    var availableReds: Int,
    var foulModifier: Int,
    var startingPlayer: Int,
    var crtPlayer: Int,
    var crtFrame: Long,
    var availablePoints: Int,
    var counterRetake: Int,
    var handicapFrame: Int,
    var handicapMatch: Int,
    var ongoingPointsWithoutReturn: Int,
) {
    object Settings : MatchSettings(MatchState.NONE, 0, 2, 15, 0, -1, -1, 0, 0, 0, 0, 0, 0) {

        // Assign methods
        fun setMatchState(state: MatchState): Int {
            matchState = state
            Timber.i("Match state updated: $matchState")
            return -1
        }

        fun getHandicap(value: Int, pol: Int) = if (pol * value > 0) pol * value else 0

        fun resetRules(): Int {
            uniqueId = -1
            availableFrames = 2
            availableReds = 15
            foulModifier = 0
            startingPlayer = -1
            crtPlayer = -1
            crtFrame = 1
            availablePoints = 0
            counterRetake = 0
            handicapFrame = 0
            handicapMatch = 0
            ongoingPointsWithoutReturn = 0
            return -1
        }

        fun assignRules(
            uniqueId: Long,
//            availableFrames: Int,
//            availableReds: Int,
//            foulModifier: Int,
//            startingPlayer: Int,
            crtPlayer: Int,
            crtFrame: Long,
            maxAvailablePoints: Int,
            retakeCounter: Int,
//            handicapFrame: Int,
//            handicapMatch: Int,
            ongoingPointsWithoutReturn: Int,
        ) {
            this.uniqueId = uniqueId
//            this.availableFrames = availableFrames
//            this.availableReds = availableReds
//            this.foulModifier = foulModifier
//            this.startingPlayer = startingPlayer
            this.crtPlayer = crtPlayer
            this.crtFrame = crtFrame
            this.availablePoints = maxAvailablePoints
            this.counterRetake = retakeCounter
//            this.handicapFrame = handicapFrame
//            this.handicapMatch = handicapMatch
            this.ongoingPointsWithoutReturn = ongoingPointsWithoutReturn
            Timber.i("assignRules(): ${getAsText()}")
        }

        // Getter methods
        fun getMatchStateFromOrdinal(ordinal: Int) {
            matchState = when (ordinal) {
                0 -> MatchState.RULES_IDLE
                1 -> MatchState.RULES_PENDING
                2 -> MatchState.GAME_IN_PROGRESS
                3 -> MatchState.GAME_SAVED
                4 -> MatchState.SUMMARY
                else -> MatchState.NONE // Always 5
            }
        }

        fun assignUniqueId(): Long {
            uniqueId++
            return uniqueId
        }

        // Setter methods
        fun setSettingsValue(key: String, value: Int): Int {
            when (key) {
                K_INT_MATCH_STARTING_PLAYER -> {
                    startingPlayer = if (value == 2) (0..1).random() else value
                    return startingPlayer
                }
                K_INT_MATCH_HANDICAP_FRAME -> {
                    handicapFrame = if (value == 0) 0 else handicapFrame + value
                    return handicapFrame
                }
                K_INT_MATCH_HANDICAP_MATCH -> {
                    handicapMatch = if (value == 0) 0 else handicapMatch + value
                    return handicapMatch
                }
                K_INT_MATCH_AVAILABLE_FRAMES -> availableFrames = value
                K_INT_MATCH_AVAILABLE_REDS -> availableReds = value
                K_INT_MATCH_FOUL_MODIFIER -> foulModifier = value
            }
            return value
        }

        // Helper methods
        fun getOtherPlayer() = 1 - crtPlayer

        fun resetFrameAndGetFirstPlayer(action: MatchAction) {
            if (action == FRAME_START_NEW) {
                crtFrame += 1
                startingPlayer = 1 - startingPlayer
            }
            availablePoints = availableReds * 8 + 27
            crtPlayer = startingPlayer
            counterRetake = 0
        }

        fun setNextPlayerFromPotAction(potAction: PotAction) {
            crtPlayer = when (potAction) {
                SWITCH -> 1 - crtPlayer
                FIRST -> startingPlayer
                CONTINUE, RETAKE -> crtPlayer
            }
        }

        // Text and logs
        fun getAsText() =
            "Frames: $availableFrames, Reds: $availableReds, Foul: $foulModifier, First: $startingPlayer, CrtPlayer: $crtPlayer, Count: $crtFrame, Max: $availableFrames"

        fun getDisplayFrames() = "(" + (availableFrames * 2 - 1).toString() + ")"
    }
}

fun isSettingsButtonSelected(key: String, value: Int): Boolean = value == when (key) {
    K_INT_MATCH_STARTING_PLAYER -> MatchSettings.Settings.startingPlayer
    K_INT_MATCH_AVAILABLE_FRAMES -> MatchSettings.Settings.availableFrames
    K_INT_MATCH_AVAILABLE_REDS -> MatchSettings.Settings.availableReds
    K_INT_MATCH_FOUL_MODIFIER -> MatchSettings.Settings.foulModifier
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