package com.app.musicplayer.repository.tracks

import androidx.lifecycle.LiveData
import com.app.musicplayer.models.Track

interface TracksRepository {
    fun getTracks():LiveData<List<Track>>

    fun getTracksOfAlbum(albumId: Long? = null,callback:(List<Track>)->Unit)
    fun getTracksOfArtist(artistId: Long? = null,callback:(List<Track>)->Unit)
}