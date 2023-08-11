package com.app.musicplayer.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.models.Track

@Dao
interface PlaylistSongCrossRefDao {

    @Insert
    fun insert(crossRef: PlaylistSongCrossRef)

    @Query("SELECT songId from playlist_song_cross_ref WHERE playlistId = :playlistId")
    fun getSongIdsForPlaylist(playlistId:Long):List<Long>
}