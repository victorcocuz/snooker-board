package com.example.snookerscore.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DatabaseRanking::class, DatabaseFrame::class], version = 2, exportSchema = false)
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