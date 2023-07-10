package com.app.musicplayer.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.musicplayer.db.dao.FavoritesDao
import com.app.musicplayer.db.dao.TrackDao
import com.app.musicplayer.models.Track
import com.app.musicplayer.utils.ROOM_DB_VERSION

@Database(entities = [Track::class], version = ROOM_DB_VERSION, exportSchema = false)
abstract class MusicDB : RoomDatabase() {
    abstract fun getTrackDao(): TrackDao
    abstract fun getFavoriteDao(): FavoritesDao
}