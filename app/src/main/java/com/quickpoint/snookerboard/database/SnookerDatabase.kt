package com.quickpoint.snookerboard.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DbScore::class,
        DbBreak::class,
        DbPot::class,
        DbBall::class,
        DbActionLog::class,
        DbFrame::class],
    version = 25,
    exportSchema = false
)
abstract class SnookerDatabase : RoomDatabase() {
    abstract val snookerDatabaseDao: SnookerDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: SnookerDatabase? = null

        fun getDatabase(context: Context): SnookerDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SnookerDatabase::class.java,
                        "snooker_database"
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