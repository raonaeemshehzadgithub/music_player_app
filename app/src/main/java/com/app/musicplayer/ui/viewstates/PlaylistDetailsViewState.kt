package com.app.musicplayer.ui.viewstates

import android.view.View
import androidx.lifecycle.LiveData
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.models.Track
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailsViewState @Inject constructor(
    private val trackRepository: TracksRepository
) : ListViewState<Track>() {

    val showMenuEvent = DataLiveEvent<TrackCombinedData>()
    val showItemEvent = DataLiveEvent<TrackCombinedData>()
    override fun getItemsObservable(callback: (LiveData<List<Track>>) -> Unit) {
    }

    override fun setOnMenuClickListener(item: Track, position: Int, view: View) {
        super.setOnMenuClickListener(item, position, view)
        showMenuEvent.call(TrackCombinedData(item,position))
    }

    override fun setOnItemClickListener(item: Track, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(TrackCombinedData(item,position))
    }

    suspend fun fetchSongIdsForPlaylist(playlistId: Long): List<Long>? {
        return withContext(Dispatchers.IO) {
            val trackList = trackRepository.getSongIdsForPlaylist(playlistId)
            trackList.ifEmpty {
                null
            }
        }
    }
}