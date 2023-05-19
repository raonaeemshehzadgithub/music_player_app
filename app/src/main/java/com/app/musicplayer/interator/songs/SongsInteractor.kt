package com.app.musicplayer.interator.songs

import com.app.musicplayer.interator.base.BaseInterator
import com.app.musicplayer.models.Track

interface SongsInteractor:BaseInterator<SongsInteractor.Listener> {
    interface Listener

    fun deleteSong(songId: Long)

    fun querySong(songId: Long, callback: (Track?) -> Unit)
}