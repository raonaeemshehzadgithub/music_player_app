package com.app.musicplayer.di.factory.livedata

import com.app.musicplayer.interator.livedata.AlbumsLiveData
import com.app.musicplayer.interator.livedata.ArtistsLiveData
import com.app.musicplayer.interator.livedata.TracksLiveData

interface LiveDataFactory {
    fun getTracksLiveData(trackId: Long?=null): TracksLiveData
    fun getAlbumsLiveData(albumId: Long?=null): AlbumsLiveData
    fun getArtistsLiveData(artistId: Long?=null): ArtistsLiveData
}