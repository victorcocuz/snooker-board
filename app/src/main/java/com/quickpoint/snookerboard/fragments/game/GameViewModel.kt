package com.quickpoint.snookerboard.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.PotType.*
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.FrameEvent
import com.quickpoint.snookerboard.utils.MatchAction
import timber.log.Timber

class GameViewModel: ViewModel() {

    // Observables
    private val _eventGameAction = MutableLiveData<Event<MatchAction>>()
    val eventGameAction: LiveData<Event<MatchAction>> = _eventGameAction
    private fun assignEventGameAction(matchAction: MatchAction) {
        _eventGameAction.value = Event(matchAction)
    }

    private val _isFrameUpdateInProgress = MutableLiveData(false)
    val isFrameUpdateInProgress: LiveData<Boolean> = _isFrameUpdateInProgress // Deactivate all buttons & options menu if frame is updating

    // Variables
    private var player: CurrentPlayer = CurrentPlayer.PLAYER01
    var ballStack: MutableList<DomainBall> = mutableListOf()
    var frameStack: MutableList<DomainBreak> = mutableListOf()
    var rules = DomainMatchInfo.RULES

    fun loadMatchPartBLoadIntoVm(frame: DomainFrame?) = frame?.let { // Will load latest frame once observed from play fragment
        Timber.i("LOAD MATCH PART B LOAD INTO VM")
        player = it.frameScore.asCurrentScore() ?: player
        frameStack = it.frameStack
        ballStack = it.ballStack
        rules.frameMax = it.frameMax
        assignEventGameAction(MatchAction.FRAME_UPDATE_RECORD)
    }

    fun resetFrame() { // Reset all frame values on match end if chosen to discard current frame, when resetting match , when starting a new frame or on rerack action from the options menu
        Timber.i("RESET FRAME")
        rules.frameMax = rules.matchReds * 8 + 27
        player.getFirst().resetFrameScore()
        player.getSecond().resetFrameScore()
        frameStack.clear()
        ballStack.clear()
        ballStack.addBalls(WHITE(), BLACK(), PINK(), BLUE(), BROWN(), GREEN(), YELLOW())
        repeat(rules.matchReds) { ballStack.addBalls(COLOR(), RED()) }
        assignEventGameAction(MatchAction.FRAME_RESET_COMPLETE) // Once the reset is complete, create a new event to be triggered
        assignEventGameAction(MatchAction.FRAME_UPDATE_RECORD)
    }

    // Handler functions
    fun handleFrameEvent(frameEvent: FrameEvent, pot: DomainPot?, removeRed: Boolean?, freeBall: Boolean?) {
        Timber.i("HANDLE FRAME EVENT")
        _isFrameUpdateInProgress.value = true
        when (frameEvent) {
            FrameEvent.HANDLE_FOUL -> handleFoul(pot!!, removeRed!!, freeBall!!)
            FrameEvent.HANDLE_POT -> handleFrameUpdate(pot!!)
            FrameEvent.HANDLE_UNDO -> handleUndo()
        }
        _isFrameUpdateInProgress.value = false
        assignEventGameAction(MatchAction.FRAME_RESET_COMPLETE)
        assignEventGameAction(MatchAction.FRAME_UPDATE_RECORD)
    }

    private fun handleFoul(pot: DomainPot, removeRed: Boolean, freeBall: Boolean) {
        Timber.i("HANDLE FOUL")
        handleFrameUpdate(pot)
        if (removeRed) handleFrameUpdate(DomainPot.REMOVERED)
        if (freeBall) rules.frameMax += ballStack.addFreeBall()
    }

    private fun handleFrameUpdate(pot: DomainPot) = ballStack.apply {
        Timber.i("HANDLE FRAME UPDATE")
        frameStack.addToFrameStack(
            when (pot.potType) {
                in listOf(HIT, FREE, ADDRED) -> DomainPot.HIT(pot.ball)
                FOUL -> DomainPot.FOUL(pot.ball, pot.potAction)
                else -> pot
            },
            player.getPlayerAsInt(),
            rules.matchFrameCount
        )
        player.calculatePoints(pot, 1, this.last(), rules.matchFoul, frameStack)
        when (pot.potType) {
            in listOf(HIT, FREE) -> removeBalls(1)
            in listOf(REMOVERED, ADDRED) -> removeBalls(2)
            else -> {
                if (last() is FREEBALL) {
                    frameStack.addToFrameStack(DomainPot.FREEMISS, player.getPlayerAsInt(), rules.matchFrameCount)
                    rules.frameMax -= removeFreeBall()
                }
                if (last() is COLOR) removeBalls(1)
            }
        }
        if (size == 1) if (player.isFrameEqual()) addBalls(BLACK()) // Query end frame exception; see fragment
        if (pot.potAction == PotAction.SWITCH) player = player.getOther()
    }

    private fun handleUndo(): Any = ballStack.apply {
        Timber.i("HANDLE UNDO")
        player = player.getPlayerFromInt(frameStack.last().player)
        val lastPot = frameStack.removeFromFrameStack()
        when (lastPot.potType) {
            HIT -> addBalls(if (isNextColor()) COLOR() else lastPot.ball)
            ADDRED -> addBalls(RED(), COLOR())
            FREE -> {
                rules.frameMax += addFreeBall()
                handleUndo()
            }
            REMOVERED -> {
                if (last() is FREEBALL) removeBalls(if (inColors()) 1 else 2)
                if (isNextColor()) addBalls(COLOR(), RED()) else addBalls(RED(), COLOR())
                handleUndo()
            }
            FOUL -> {
                if (last() is FREEBALL) rules.frameMax -= removeFreeBall()
                if (frameStack.isPreviousRed() && isNextColor()) addBalls(COLOR())
            }
            SAFE -> if (frameStack.isPreviousRed() && isNextColor()) addBalls(COLOR())
            MISS -> if (frameStack.isPreviousRed() && isNextColor()) addBalls(COLOR())
        }
        player.calculatePoints(lastPot, -1, ballStack.last(), rules.matchFoul, frameStack)
    }
}