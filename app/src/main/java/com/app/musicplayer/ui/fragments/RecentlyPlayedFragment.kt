package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.extentions.shareTrack
import com.app.musicplayer.extentions.toTrack
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.TrackCombinedData
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
            }
            showItemEvent.observe(this@RecentlyPlayedFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                        putExtra(TRACK_ID, trackCombinedData.track.id)
                        putExtra(POSITION, trackCombinedData.position)
                        putExtra(PLAYER_LIST, FROM_RECENT)
                    })
                }
            }
            showMenuEvent.observe(this@RecentlyPlayedFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    trackCombinedData.view?.let {
                        baseActivity.showTrackMenu(it, true) { callback ->
                            when (callback) {
                                PLAY_TRACK -> {
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            MusicPlayerActivity::class.java
                                        ).apply {
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
                                    viewState.removeRecentTrack(trackCombinedData.track.id ?: 0L)
                                }

                                RENAME_TRACK -> {
                                }

                                PROPERTIES_TRACK -> {
                                    val recentTrackCombined = TrackCombinedData(
                                        trackCombinedData.track.toTrack(),
                                        trackCombinedData.position,
                                        trackCombinedData.view
                                    )
                                    baseActivity.showTrackPropertiesDialog(recentTrackCombined)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}