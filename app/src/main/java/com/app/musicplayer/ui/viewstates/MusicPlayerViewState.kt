package com.app.musicplayer.ui.viewstates

import android.content.Context
import androidx.lifecycle.LiveData
import com.app.musicplayer.interator.livedata.TracksLiveData
import com.app.musicplayer.interator.player.PlayerInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewState @Inject constructor(
    tracksRepository: TracksRepository,
    private val playerInteractor: PlayerInteractor
) : ListViewState<Track>() {

    private val tracksLiveData = tracksRepository.getTracks() as TracksLiveData
    override fun getItemsObservable(callback: (LiveData<List<Track>>) -> Unit) {
        callback.invoke(tracksLiveData)
    }

    fun setRingtone(context: Context, trackId: Long) {
        playerInteractor.setPhoneRingtone(context, trackId)
    }

    fun deleteTrack(trackId: Long) {
        playerInteractor.deleteTrack(trackId)
    }
    fun queryTrackList(trackList:(List<Track>)->Unit) {
        playerInteractor.queryTrackList {
            trackList.invoke(it as List<Track>)
        }
    }

}