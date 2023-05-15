package com.app.musicplayer.helpers

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.app.musicplayer.services.MusicService

class MediaSessionCallback(private val service: MusicService) : MediaSessionCompat.Callback() {

    override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
        return super.onMediaButtonEvent(mediaButtonEvent)
    }

    override fun onPlay() {
        service.resumeTrack()
    }

    override fun onPause() {
        service.pauseTrack()
    }

    override fun onSkipToNext() {
        Log.wtf("next on notification", "yes")
        super.onSkipToNext()
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        super.onCustomAction(action, extras)
    }
}