package com.app.musicplayer.interator.player

import android.content.Context

interface PlayerInteractor {
    interface Listener

    fun deleteTrack(trackId: Long)

    fun setPhoneRingtone(context: Context, trackId: Long)
}