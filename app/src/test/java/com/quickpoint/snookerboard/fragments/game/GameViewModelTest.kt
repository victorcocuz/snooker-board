package com.quickpoint.snookerboard.fragments.game

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quickpoint.snookerboard.database.SnookerDatabase
import com.quickpoint.snookerboard.domain.*
import com.quickpoint.snookerboard.domain.BallType.*
import com.quickpoint.snookerboard.domain.DomainBall.*
import com.quickpoint.snookerboard.domain.DomainPot.*
import com.quickpoint.snookerboard.domain.PotAction.*
import com.quickpoint.snookerboard.repository.SnookerRepository
import com.quickpoint.snookerboard.utils.MatchRules.*
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
internal class GameViewModelTest {
    companion object {
        private val printlnTree = object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                println("$tag: $message")
            }
        }

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            Timber.plant(printlnTree)
        }

        @JvmStatic
        @AfterClass
        fun cleanupClass() {
            Timber.uproot(printlnTree)
        }

    }

    private val gameVm = GameViewModel(ApplicationProvider.getApplicationContext(),
        SnookerRepository(SnookerDatabase.getDatabase(ApplicationProvider.getApplicationContext())))

    @Test
    fun handle_and_undo_pot_general() {
        gameVm.apply {
            RULES.assignRules(0, 1, 1, 4, 1, -1, 1, 0)
            resetMatch()
            assertThat(ballStack.size).isEqualTo(7 + RULES.reds * 2)

            val actionLogs: Array<DomainActionLog> = Gson().fromJson(jsonLogs, object : TypeToken<Array<DomainActionLog>>() {}.type)
            for (actionLog in actionLogs) {
                actionLog.apply {
                    if (description == "handlePot()") {
                        assignPot(potType, ballType.getBallFromValues(ballPoints), potAction ?: CONTINUE)
                    }
                }
            }
            repeat(actionLogs.size) { assignPot(null) } // Undo until no balls left
            assertThat(ballStack.size).isEqualTo(RULES.reds * 2 + 7)
            assertThat(frameStack.size).isEqualTo(0)
            assertThat(score[0].framePoints).isEqualTo(0)
            assertThat(score[1].framePoints).isEqualTo(0)
            assertThat(score[0].matchPoints).isEqualTo(0)
            assertThat(score[1].matchPoints).isEqualTo(0)
            assertThat(score[0].successShots).isEqualTo(0)
            assertThat(score[1].successShots).isEqualTo(0)
            assertThat(score[0].missedShots).isEqualTo(0)
            assertThat(score[1].missedShots).isEqualTo(0)
            assertThat(score[0].safetySuccessShots).isEqualTo(0)
            assertThat(score[1].safetySuccessShots).isEqualTo(0)
            assertThat(score[0].safetyMissedShots).isEqualTo(0)
            assertThat(score[1].safetyMissedShots).isEqualTo(0)
            assertThat(score[0].snookers).isEqualTo(0)
            assertThat(score[1].snookers).isEqualTo(0)
            assertThat(score[0].fouls).isEqualTo(0)
            assertThat(score[1].fouls).isEqualTo(0)
            assertThat(score[0].highestBreak).isEqualTo(0)
            assertThat(score[1].highestBreak).isEqualTo(0)
            assertThat(score[0].cumulatedValues()).isEqualTo(0)
            assertThat(score[1].cumulatedValues()).isEqualTo(0)
        }
    }
}
//RULES.assignRules(0, 1, 15, 1, 0, 0, 1, 0)
private const val jsonLogsNew = "[" +
        "{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":2,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_RED\",\"breakCount\":3,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":4,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
        ",{\"ballPoints\":2,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_YELLOW\",\"breakCount\":5,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":6,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":7,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":8,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":9,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE\"}" +
        ",{\"ballPoints\":2,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_YELLOW\",\"breakCount\":10,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FOUL\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_RED\",\"breakCount\":11,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
        ",{\"ballPoints\":6,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_PINK\",\"breakCount\":12,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":13,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":14,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":15,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
        ",{\"ballPoints\":2,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_YELLOW\",\"breakCount\":16,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":17,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":18,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
        ",{\"ballPoints\":3,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_GREEN\",\"breakCount\":19,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":20,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":21,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":22,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_WHITE\",\"breakCount\":23,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FOUL\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_WHITE\",\"breakCount\":24,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":25,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":26,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":27,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
        ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":27,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_WHITE\",\"breakCount\":28,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":29,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":30,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SNOOKER\"}" +
        ",{\"ballPoints\":6,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_PINK\",\"breakCount\":31,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":32,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":33,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":34,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
        ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":34,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_RED\",\"breakCount\":34,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":35,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":34,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_RED\",\"breakCount\":34,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//        ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":34,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":33,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":32,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":31,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//        ",{\"ballPoints\":6,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_PINK\",\"breakCount\":30,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":29,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SNOOKER\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":28,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_WHITE\",\"breakCount\":27,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//        ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":27,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":26,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":25,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":24,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_WHITE\",\"breakCount\":23,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_WHITE\",\"breakCount\":22,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FOUL\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":21,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":20,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":19,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//        ",{\"ballPoints\":3,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_GREEN\",\"breakCount\":18,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":17,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":16,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//        ",{\"ballPoints\":2,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_YELLOW\",\"breakCount\":15,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":14,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":13,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":12,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//        ",{\"ballPoints\":6,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_PINK\",\"breakCount\":11,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_RED\",\"breakCount\":10,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//        ",{\"ballPoints\":2,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_YELLOW\",\"breakCount\":9,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FOUL\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":8,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":7,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":6,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":5,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//        ",{\"ballPoints\":2,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_YELLOW\",\"breakCount\":4,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":3,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
//        ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_RED\",\"breakCount\":2,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//        ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":1,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE\"}" +
        "]"

//            RULES.assignRules(0, 1, 1, 4, 1, -1, 1, 0)
private const val jsonLogs =
    "[" +
            "{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_RED\",\"breakCount\":1,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_YELLOW\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":2,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_YELLOW\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":3,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
            ",{\"ballPoints\":2,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_YELLOW\",\"breakCount\":4,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":5,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":6,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":7,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
            ",{\"ballPoints\":3,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":8,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":9,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":10,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":11,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":12,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":13,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":14,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE\"}" +
            ",{\"ballPoints\":3,\"ballStackLast\":\"TYPE_BROWN\",\"ballType\":\"TYPE_GREEN\",\"breakCount\":15,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_BROWN\",\"ballType\":\"TYPE_WHITE\",\"breakCount\":16,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BROWN\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":17,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_BROWN\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":18,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
            ",{\"ballPoints\":4,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_BROWN\",\"breakCount\":19,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_WHITE\",\"breakCount\":20,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FOUL\"}" +
            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":21,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":22,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":23,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":24,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
            ",{\"ballPoints\":6,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_PINK\",\"breakCount\":25,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":26,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":27,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":28,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_PINK\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":29,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
            ",{\"ballPoints\":6,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_PINK\",\"breakCount\":29,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_BLACK\",\"breakCount\":30,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":31,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":32,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":33,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_BLACK\",\"breakCount\":34,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":35,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":36,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":37,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_BLACK\",\"breakCount\":38,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":39,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":40,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":41,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_WHITE\",\"ballType\":\"TYPE_BLACK\",\"breakCount\":41,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
            "]"