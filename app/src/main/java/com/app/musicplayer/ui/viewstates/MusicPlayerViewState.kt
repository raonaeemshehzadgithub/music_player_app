package com.app.musicplayer.ui.viewstates

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.musicplayer.interator.livedata.TracksLiveData
import com.app.musicplayer.interator.player.PlayerInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewState @Inject constructor(
    private val tracksRepository: TracksRepository,
    private val playerInteractor: PlayerInteractor
) : ListViewState<Track>() {

    private val tracksLiveData = tracksRepository.getTracks() as TracksLiveData
    override fun getItemsObservable(callback: (LiveData<List<Track>>) -> Unit) {
        callback.invoke(tracksLiveData)
    }

    fun insertRecentTrack(track: RecentTrackEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertRecentTrack(track)
        }
    }
    fun removeFavoriteTrack(trackId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.removeFavoriteTrack(trackId)
        }
    }
    fun insertFavoriteTrack(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertFavoriteTrack(track)
        }
    }
    suspend fun fetchFavorites():List<Track>? {
        return withContext(Dispatchers.IO) {
            val trackList = tracksRepository.fetchFavorites()
            trackList.ifEmpty {
                null
            }
        }
    }
}