package com.app.musicplayer.ui.viewstates

import android.view.View
import androidx.lifecycle.LiveData
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaylistViewState @Inject constructor(
    private val trackRepository: TracksRepository
): ListViewState<PlaylistEntity>() {

    val showMenuEvent = DataLiveEvent<PlaylistEntity>()
    val showItemEvent = DataLiveEvent<PlaylistEntity>()
    override fun getItemsObservable(callback: (LiveData<List<PlaylistEntity>>) -> Unit) {
    }
    fun fetchPlaylists(): LiveData<List<PlaylistEntity>> {
        return trackRepository.fetchPlaylistsLiveList()
    }

    override fun setOnMenuClickListener(item: PlaylistEntity, position: Int, view: View) {
        super.setOnMenuClickListener(item, position, view)
        showMenuEvent.call(item)
    }

    override fun setOnItemClickListener(item: PlaylistEntity, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(item)
    }
}