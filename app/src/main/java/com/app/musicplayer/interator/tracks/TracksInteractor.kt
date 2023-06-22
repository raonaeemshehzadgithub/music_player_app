package com.app.musicplayer.interator.tracks

import com.app.musicplayer.interator.base.BaseInterator
import com.app.musicplayer.models.Track

interface TracksInteractor:BaseInterator<TracksInteractor.Listener> {
    interface Listener

    fun deleteTrack(trackId: Long)

    fun queryTrack(trackId: Long, callback: (Track?) -> Unit)
    fun queryTrackList(callback: (List<Track?>) -> Unit)
}