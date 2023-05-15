package com.app.musicplayer.ui.interfaces

import com.app.musicplayer.models.Track

interface SongClick {
    fun onSongClick(track: Track)
}