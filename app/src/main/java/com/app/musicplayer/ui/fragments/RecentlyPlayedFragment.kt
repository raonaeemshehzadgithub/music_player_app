package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.models.Track
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.extentions.deleteTrack
import com.app.musicplayer.extentions.shareTrack
import com.app.musicplayer.extentions.toTrack
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.RecentTrackCombinedData
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.services.MusicService.Companion.tracksList
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.adapters.RecentTracksAdapter
import com.app.musicplayer.ui.viewstates.RecentTrackViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecentlyPlayedFragment : ListFragment<RecentTrackEntity, RecentTrackViewState>() {
    override val viewState: RecentTrackViewState by activityViewModels()
    override val manager: RecyclerView.LayoutManager =
        LinearLayoutManager(activity?.applicationContext)
    private var recentList: ArrayList<Track>? = null

    @Inject
    override lateinit var listAdapter: RecentTracksAdapter
    @Inject
    lateinit var tracksInteractor: TracksInteractor
    override fun onSetup() {
        super.onSetup()
        listAdapter.viewHolderType = RECENT_TRACK_VT
        viewState.apply {
            fetchRecentTrackList().observe(this@RecentlyPlayedFragment) {
                listAdapter.items = it
                showEmpty(listAdapter.items.isEmpty())
                val recentTrackList: ArrayList<Track> = ArrayList(it.map { track ->
                    Track(
                        id = track.id,
                        title = track.title,
                        artist = track.artist,
                        duration = track.duration,
                        path = track.path,
                        albumId = track.albumId
                    )
                })
                recentList = recentTrackList
            }
            showItemEvent.observe(this@RecentlyPlayedFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                        putExtra(TRACK_ID, trackCombinedData.track.id)
                        putExtra(POSITION, trackCombinedData.position)
                    })
                }
            }
            showMenuEvent.observe(this@RecentlyPlayedFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    trackCombinedData.view?.let {
                        baseActivity.showTrackMenu(it,true) { callback ->
                            when (callback) {
                                PLAY_TRACK -> {
                                    startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                                        putExtra(TRACK_ID, trackCombinedData.track.id)
                                        putExtra(POSITION, trackCombinedData.position)
                                    })
                                }

                                SHARE_TRACK -> {
                                    context?.let { it2 ->
                                        trackCombinedData.track.path?.shareTrack(it2)
                                    }
                                }

                                DELETE_TRACK -> {
                                    removeRecentTrack(trackCombinedData.track.id?:0L)
                                }

                                RENAME_TRACK -> {
                                    baseActivity.showTrackRenameMenu(
                                        trackCombinedData.track.title ?: ""
                                    ) { renamedText ->
//                                        tracksInteractor.renameTrack(recentTrackCombine, renamedText)
                                    }
                                }
                                PROPERTIES_TRACK->{
                                    val recentTrackCombined = TrackCombinedData(trackCombinedData.track.toTrack(),trackCombinedData.position,trackCombinedData.view)
                                    baseActivity.showTrackPropertiesDialog(recentTrackCombined)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tracksList = recentList!!
    }
}