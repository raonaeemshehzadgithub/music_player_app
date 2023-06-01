package com.app.musicplayer.di.factory.livedata

import com.app.musicplayer.interator.livedata.TracksLiveData

interface LiveDataFactory {
    fun getTracksLiveData(trackId: Long?=null): TracksLiveData
}