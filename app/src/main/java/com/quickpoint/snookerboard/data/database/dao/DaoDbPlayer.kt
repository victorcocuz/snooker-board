package com.quickpoint.snookerboard.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.quickpoint.snookerboard.data.database.SnookerDatabase.Companion.TABLE_PLAYER
import com.quickpoint.snookerboard.data.database.models.DbPlayer
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoDbPlayer {

    @Query("SELECT COUNT(*) FROM $TABLE_PLAYER")
    suspend fun getPlayerCount(): Int
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlayers(players: List<DbPlayer>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlayer(player: DbPlayer): Long

    @Update
    fun updatePlayer(player: DbPlayer): Int

    @Transaction
    suspend fun insertOrUpdatePlayer(player: DbPlayer): Long {
        val id = insertPlayer(player)
        return if (id==-1L) {
            updatePlayer(player)
            player.playerId
        } else {
            id
        }
    }

    @Query("SELECT * FROM $TABLE_PLAYER")
    fun getPlayers(): Flow<List<DbPlayer>>

    @Query("DELETE FROM $TABLE_PLAYER WHERE playerId = :playerId")
    fun deletePlayer(playerId: Long)

    @Query("DELETE FROM $TABLE_PLAYER")
    suspend fun clear(): Int
}