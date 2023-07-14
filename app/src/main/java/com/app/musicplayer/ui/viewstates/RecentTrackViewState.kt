package com.app.musicplayer.ui.viewstates

import androidx.lifecycle.LiveData
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.RecentTrackCombinedData
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecentTrackViewState @Inject constructor(
    private val tracksRepository: TracksRepository,
    private val tracksInterator: TracksInteractor
) : ListViewState<RecentTrackEntity>() {

    val showItemEvent = DataLiveEvent<RecentTrackCombinedData>()
    //    private val tracksLiveData = tracksRepository.getTracks() as TracksLiveData
    override fun getItemsObservable(callback: (LiveData<List<RecentTrackEntity>>) -> Unit) {
    }

    override fun setOnItemClickListener(item: RecentTrackEntity, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(RecentTrackCombinedData(item, position))
    }

    fun fetchRecentTrackList(): LiveData<List<RecentTrackEntity>> {
        return tracksRepository.fetchRecentTrack()
    }
}