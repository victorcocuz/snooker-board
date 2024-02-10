package com.quickpoint.snookerboard.domain.repository

import com.quickpoint.snookerboard.data.database.models.DbFrameWithScoreAndBreakWithPotsAndBallStack
import com.quickpoint.snookerboard.domain.models.DomainActionLog
import com.quickpoint.snookerboard.domain.models.DomainFrame
import com.quickpoint.snookerboard.domain.models.DomainScore
import kotlinx.coroutines.flow.Flow

interface GameRepository {

//    var cachedPlayers: List<DomainPlayer>
//    suspend fun getPlayers()
//    suspend fun updatePlayer(player: DbPlayer): Long
    suspend fun getCrtFrame(): DbFrameWithScoreAndBreakWithPotsAndBallStack?
    suspend fun saveCrtFrame(frame: DomainFrame)
    suspend fun deleteCrtFrame(frameId: Long)
    suspend fun deleteCrtMatch()
    suspend fun getTotals(playerId: Int): DomainScore
    val score: Flow<ArrayList<Pair<DomainScore, DomainScore>>>
    suspend fun getDomainActionLogs(): List<DomainActionLog>

}