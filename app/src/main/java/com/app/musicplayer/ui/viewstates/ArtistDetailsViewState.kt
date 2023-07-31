package com.app.musicplayer.ui.viewstates

import android.view.View
import androidx.lifecycle.LiveData
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.interator.livedata.TracksLiveData
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistDetailsViewState @Inject constructor(
    private val tracksRepository: TracksRepository,
    private val tracksInterator: TracksInteractor
) : ListViewState<Track>() {

    var getArtistLiveList: LiveData<List<Track>>? = null
    val showItemEvent = DataLiveEvent<TrackCombinedData>()
    val showMenuEvent = DataLiveEvent<TrackCombinedData>()

    override fun getItemsObservable(callback: (LiveData<List<Track>>) -> Unit) {
        getArtistLiveList?.let {
            callback.invoke(it)
        }
    }

    fun queryArtistDetails(id: Long) {
        getArtistLiveList = tracksRepository.getArtistTracks(id) as TracksLiveData
    }

    override fun setOnItemClickListener(item: Track, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(TrackCombinedData(item, position))
    }

    override fun setOnMenuClickListener(item: Track, position: Int, view: View) {
        super.setOnMenuClickListener(item, position, view)
        showMenuEvent.call(TrackCombinedData(item, position, view))
    }
}