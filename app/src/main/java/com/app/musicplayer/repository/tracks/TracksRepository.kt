package com.app.musicplayer.repository.tracks

import android.content.Context
import androidx.lifecycle.LiveData
import com.app.musicplayer.models.Track
import com.app.musicplayer.db.entities.RecentTrackEntity

interface TracksRepository {
    fun getTracks(): LiveData<List<Track>>

    fun getTracksOfAlbum(albumId: Long? = null, callback: (List<Track>) -> Unit)
    fun getTracksOfArtist(artistId: Long? = null, callback: (List<Track>) -> Unit)

    fun insertRecentTrack(track: RecentTrackEntity)
    fun fetchRecentTrack(): LiveData<List<RecentTrackEntity>>
    fun insertFavoriteTrack(track: Track)
    fun removeFavoriteTrack(trackId: Long)
    fun removeRecentTrack(trackId: Long)
    fun fetchFavoriteTrack(): LiveData<List<Track>>
    fun fetchFavorites(): List<Track>
}