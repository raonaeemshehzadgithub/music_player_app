package com.app.musicplayer.repository.tracks

import android.content.Context
import androidx.lifecycle.LiveData
import com.app.musicplayer.models.Track

interface TracksRepository {
    fun getTracks(): LiveData<List<Track>>

    fun getTracksOfAlbum(albumId: Long? = null, callback: (List<Track>) -> Unit)
    fun getTracksOfArtist(artistId: Long? = null, callback: (List<Track>) -> Unit)

    fun insertRecentTrack(track: Track)
    fun fetchRecentTrack(): LiveData<List<Track>>
    fun insertFavoriteTrack(track: Track)
    fun removeFavoriteTrack(trackId: Long)
    fun fetchFavoriteTrack(): LiveData<List<Track>>
    fun fetchFavorites(): List<Track>
    fun isFavorite(context:Context): Boolean
}