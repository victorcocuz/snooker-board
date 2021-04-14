package com.example.snookerscore.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SnookerDatabaseDao {
    @Insert
    fun insert(ranking: DatabaseRanking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg rankings: DatabaseRanking)

    @Update
    fun update(ranking: DatabaseRanking)

    @Query("SELECT * from rankings_table WHERE position = :id")
    fun get (id: Int): DatabaseRanking

    @Query("DELETE FROM rankings_table")
    fun clear()

    @Query("SELECT * FROM rankings_table ORDER BY position ASC")
    fun getRankings(): LiveData<List<DatabaseRanking>>
}