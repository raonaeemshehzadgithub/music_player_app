package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.models.Track
import com.app.musicplayer.db.entities.RecentTrackEntity
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
    override fun onSetup() {
        super.onSetup()
        viewState.fetchRecentTrackList().observe(this) {
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
        viewState.showItemEvent.observe(this) { event ->
            event.ifNew?.let { trackCombinedData ->
                startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                    putExtra(TRACK_ID, trackCombinedData.track.id)
                    putExtra(POSITION, trackCombinedData.position)
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tracksList = recentList!!
    }
}