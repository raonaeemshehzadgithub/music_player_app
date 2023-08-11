package com.app.musicplayer.extentions

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_22_23 = object : Migration(22, 23) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("CREATE TABLE IF NOT EXISTS `PlaylistEntity` (`playlist_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `playlist_name` TEXT NOT NULL)")
      database.execSQL("CREATE TABLE IF NOT EXISTS `playlist_song_cross_ref` (`playlistId` INTEGER NOT NULL, `songId` INTEGER NOT NULL, PRIMARY KEY(`playlistId`, `songId`))")
    }
}