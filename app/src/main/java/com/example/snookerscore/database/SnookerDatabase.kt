package com.example.snookerscore.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        DatabaseRanking::class,
        DatabaseFrameScore::class,
        DatabaseMatchBreak::class,
        DatabaseMatchPot::class,
        DatabaseMatchBall::class], version = 8, exportSchema = false
)

@TypeConverters(Converters::class)
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
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}