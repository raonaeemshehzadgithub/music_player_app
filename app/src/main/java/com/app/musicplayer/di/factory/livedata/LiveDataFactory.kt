package com.app.musicplayer.di.factory.livedata

import com.app.musicplayer.interator.livedata.SongsLiveData

interface LiveDataFactory {
    fun getSongsLiveData(songId: Long?=null): SongsLiveData
}