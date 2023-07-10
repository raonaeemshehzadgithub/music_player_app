package com.app.musicplayer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.musicplayer.models.Track

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentTrack(track: Track)

    @Query("select * from Track")
    fun fetchTrackList():LiveData<List<Track>>
}