package com.app.musicplayer.ui.viewstates

import androidx.lifecycle.LiveData
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.interator.livedata.TracksLiveData
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.models.Track
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TracksViewState @Inject constructor(
    tracksRepository: TracksRepository,
    private val tracksInterator: TracksInteractor
) : ListViewState<Track>() {

    val showItemEvent = DataLiveEvent<TrackCombinedData>()
    private val tracksLiveData = tracksRepository.getTracks() as TracksLiveData
    override fun getItemsObservable(callback: (LiveData<List<Track>>) -> Unit) {
        callback.invoke(tracksLiveData)
    }

    override fun setOnItemClickListener(item: Track, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(TrackCombinedData(item, position))
    }

    override fun onFilterChanged(filter: String?) {
        super.onFilterChanged(filter)
        tracksLiveData.filter = filter
    }
}