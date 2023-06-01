package com.app.musicplayer.repository.tracks

import androidx.lifecycle.LiveData
import com.app.musicplayer.models.Track

interface TracksRepository {
    fun getTracks():LiveData<List<Track>>
}