package com.app.musicplayer.repository.songs

import androidx.lifecycle.LiveData
import com.app.musicplayer.models.Track

interface SongsRepository {
    fun getSongs():LiveData<List<Track>>
}