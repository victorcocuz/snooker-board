package com.quickpoint.snookerboard.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quickpoint.snookerboard.database.dao.*
import com.quickpoint.snookerboard.database.models.*

@Database(
    entities = [
        DbActionLog::class,
        DbScore::class,
        DbBall::class,
        DbPot::class,
        DbBreak::class,
        DbFrame::class],
    version = 25,
    exportSchema = false
)
abstract class SnookerDatabase : RoomDatabase() {
    abstract val daoDbActionLog: DaoDbActionLog
    abstract val daoDbBall: DaoDbBall
    abstract val daoDbBreak: DaoDbBreak
    abstract val daoDbFrame: DaoDbFrame
    abstract val daoDbPot: DaoDbPot
    abstract val daoDbScore: DaoDbScore

    companion object {
        @Volatile
        private var INSTANCE: SnookerDatabase? = null

        private const val SNOOKER_DATABASE = "snooker_database"
        const val TABLE_MATCH_ACTION_LOG = "match_debug_action_table"
        const val TABLE_MATCH_BALL = "match_ball_stack_table"
        const val TABLE_MATCH_BREAKS = "match_breaks_table"
        const val TABLE_MATCH_FRAMES = "match_frames_table"
        const val TABLE_MATCH_POTS = "match_pots_table"
        const val TABLE_MATCH_SCORE = "match_score_table"

        fun getDatabase(context: Context): SnookerDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SnookerDatabase::class.java,
                        SNOOKER_DATABASE
                    )
                        .addMigrations(
                            MIGRATION_22_23,
                            MIGRATION_23_24,
                            MIGRATION_24_25
                        )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}