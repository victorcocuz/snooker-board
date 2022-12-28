package com.quickpoint.snookerboard.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_24_25: Migration = object : Migration(23, 24) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE match_score_table ADD COLUMN pointsWithoutReturn INT NOT NULL DEFAULT (0)")
    }
}

val MIGRATION_23_24: Migration = object : Migration(23, 24) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE match_score_table ADD COLUMN longShotsSuccess INT NOT NULL DEFAULT (0)")
        database.execSQL("ALTER TABLE match_score_table ADD COLUMN longShotsMissed INT NOT NULL DEFAULT (0)")
        database.execSQL("ALTER TABLE match_score_table ADD COLUMN restShotsSuccess INT NOT NULL DEFAULT (0)")
        database.execSQL("ALTER TABLE match_score_table ADD COLUMN restShotsMissed INT NOT NULL DEFAULT (0)")
        database.execSQL("ALTER TABLE match_score_table ADD COLUMN pointsWithNoReturn INT NOT NULL DEFAULT (0)")
    }
}

val MIGRATION_22_23: Migration = object : Migration(22, 23) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE match_pots_table ADD COLUMN shotType INT NOT NULL DEFAULT (0)")
    }
}
