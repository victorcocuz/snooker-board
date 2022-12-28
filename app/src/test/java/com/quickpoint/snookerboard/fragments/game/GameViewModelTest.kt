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
import com.quickpoint.snookerboard.utils.MatchSettings.*
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import timber.log.Timber

@Config(manifest=Config.NONE)
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
            SETTINGS.assignRules(0, 1, 15, 4, 1, 0, 1, 0, 0, 0,0)
            resetMatch()
            assertThat(ballStack.size).isEqualTo(7 + SETTINGS.reds * 2)

            val actionLogs: Array<DomainActionLog> = Gson().fromJson(jsonLogsNew, object : TypeToken<Array<DomainActionLog>>() {}.type)
            actionLogs.forEachIndexed{index, actionLog ->
                actionLog.apply {
                    assertThat(description).isEqualTo("handlePot()")
                    Timber.e("index $index")
                    if (description == "handlePot()") {
                        assignPot(potType, ballType.getBallFromValues(ballPoints), potAction ?: CONTINUE)
                    }
                }
            }

            repeat(actionLogs.size) { assignPot(null) } // Undo until no balls left
            assertThat(ballStack.size).isEqualTo(SETTINGS.reds * 2 + 7)
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

//RULES.assignRules(0, 1, 15, 1, 0, 0, 1, 0, 0, 0)
private const val jsonLogsNew = "[" +
        "{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_RED\",\"breakCount\":1,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}," +
        "{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":2,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE_MISS\"}," +
        "{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":3,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SNOOKER\"}," +
        "{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":4,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE\"}," +
        "{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":5,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE_MISS\"}" +
//        "{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":4,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE_MISS\"}," +
//        "{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":3,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE\"}," +
//        "{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":2,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SNOOKER\"}," +
//        "{\"ballPoints\":0,\"ballStackLast\":\"TYPE_RED\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":1,\"description\":\"HandleUndo()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE_MISS\"}" +
        "]"

//            RULES.assignRules(0, 1, 1, 4, 1, -1, 1, 0)
//private const val jsonLogs =
//    "[" +
//            "{\"ballPoints\":1,\"ballStackLast\":\"TYPE_COLOR\",\"ballType\":\"TYPE_RED\",\"breakCount\":1,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_YELLOW\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":2,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_YELLOW\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":3,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//            ",{\"ballPoints\":2,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_YELLOW\",\"breakCount\":4,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":5,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":6,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":7,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//            ",{\"ballPoints\":3,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":8,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":9,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
//            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":10,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":11,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":12,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":13,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_GREEN\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":14,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_SAFE\"}" +
//            ",{\"ballPoints\":3,\"ballStackLast\":\"TYPE_BROWN\",\"ballType\":\"TYPE_GREEN\",\"breakCount\":15,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_BROWN\",\"ballType\":\"TYPE_WHITE\",\"breakCount\":16,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BROWN\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":17,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_BROWN\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":18,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
//            ",{\"ballPoints\":4,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_BROWN\",\"breakCount\":19,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_WHITE\",\"breakCount\":20,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FOUL\"}" +
//            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":21,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":22,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":23,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":24,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
//            ",{\"ballPoints\":6,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_PINK\",\"breakCount\":25,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":26,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":27,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_BLUE\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":28,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
//            ",{\"ballPoints\":5,\"ballStackLast\":\"TYPE_PINK\",\"ballType\":\"TYPE_BLUE\",\"breakCount\":29,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//            ",{\"ballPoints\":6,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_PINK\",\"breakCount\":29,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_BLACK\",\"breakCount\":30,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":31,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":32,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_NOBALL\",\"breakCount\":33,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_MISS\"}" +
//            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_BLACK\",\"breakCount\":34,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":35,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":36,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":37,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
//            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_BLACK\",\"breakCount\":38,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":0,\"potAction\":\"SWITCH\",\"potType\":\"TYPE_FOUL\"}" +
//            ",{\"ballPoints\":1,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_FREEBALLAVAILABLE\",\"breakCount\":39,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_AVAILABLE\"}" +
//            ",{\"ballPoints\":0,\"ballStackLast\":\"TYPE_FREEBALL\",\"ballType\":\"TYPE_FREEBALLTOGGLE\",\"breakCount\":40,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE_TOGGLE\"}" +
//            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_BLACK\",\"ballType\":\"TYPE_FREEBALL\",\"breakCount\":41,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_FREE\"}" +
//            ",{\"ballPoints\":7,\"ballStackLast\":\"TYPE_WHITE\",\"ballType\":\"TYPE_BLACK\",\"breakCount\":41,\"description\":\"handlePot()\",\"frameCount\":1,\"player\":1,\"potAction\":\"CONTINUE\",\"potType\":\"TYPE_HIT\"}" +
//            "]"