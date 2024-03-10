package com.quickpoint.snookerboard.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quickpoint.snookerboard.data.database.dao.DaoDbActionLog
import com.quickpoint.snookerboard.data.database.dao.DaoDbAppReleaseNotes
import com.quickpoint.snookerboard.data.database.dao.DaoDbAppReleaseVersion
import com.quickpoint.snookerboard.data.database.dao.DaoDbBall
import com.quickpoint.snookerboard.data.database.dao.DaoDbBreak
import com.quickpoint.snookerboard.data.database.dao.DaoDbFrame
import com.quickpoint.snookerboard.data.database.dao.DaoDbPlayer
import com.quickpoint.snookerboard.data.database.dao.DaoDbPot
import com.quickpoint.snookerboard.data.database.dao.DaoDbScore
import com.quickpoint.snookerboard.data.database.models.DbActionLog
import com.quickpoint.snookerboard.data.database.models.DbAppReleaseNotes
import com.quickpoint.snookerboard.data.database.models.DbAppReleaseVersion
import com.quickpoint.snookerboard.data.database.models.DbBall
import com.quickpoint.snookerboard.data.database.models.DbBreak
import com.quickpoint.snookerboard.data.database.models.DbFrame
import com.quickpoint.snookerboard.data.database.models.DbPlayer
import com.quickpoint.snookerboard.data.database.models.DbPot
import com.quickpoint.snookerboard.data.database.models.DbScore

@Database(
    entities = [
        DbActionLog::class,
        DbScore::class,
        DbBall::class,
        DbPot::class,
        DbBreak::class,
        DbFrame::class,
        DbPlayer::class,
        DbAppReleaseVersion::class,
        DbAppReleaseNotes::class],
    version = 28,
    exportSchema = false
)
abstract class SnookerDatabase : RoomDatabase() {
    abstract val daoDbActionLog: DaoDbActionLog
    abstract val daoDbBall: DaoDbBall
    abstract val daoDbBreak: DaoDbBreak
    abstract val daoDbFrame: DaoDbFrame
    abstract val daoDbPot: DaoDbPot
    abstract val daoDbScore: DaoDbScore
    abstract val daoDbPlayer: DaoDbPlayer
    abstract val daoDbAppReleaseVersion: DaoDbAppReleaseVersion
    abstract val daoDbAppReleaseNotes: DaoDbAppReleaseNotes

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
        const val TABLE_PLAYER = "match_player_table"
        const val TABLE_APP_RELEASE_VERSION = "match_app_release_version_table"
        const val TABLE_APP_RELEASE_NOTES = "match_app_release_notes_table"

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