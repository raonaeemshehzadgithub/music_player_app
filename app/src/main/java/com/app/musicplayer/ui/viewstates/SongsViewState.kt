package com.app.musicplayer.ui.viewstates

import androidx.lifecycle.LiveData
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.interator.livedata.SongsLiveData
import com.app.musicplayer.interator.songs.SongsInteractor
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.models.Track
import com.app.musicplayer.repository.songs.SongsRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongsViewState @Inject constructor(
    songsRepository: SongsRepository,
    private val songsInterator: SongsInteractor
) : ListViewState<Track>() {

    val showItemEvent = DataLiveEvent<TrackCombinedData>()
    private val songsLiveData = songsRepository.getSongs() as SongsLiveData
    override fun getItemsObservable(callback: (LiveData<List<Track>>) -> Unit) {
        callback.invoke(songsLiveData)
    }

    override fun setOnItemClickListener(item: Track, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(TrackCombinedData(item, position))
    }
}