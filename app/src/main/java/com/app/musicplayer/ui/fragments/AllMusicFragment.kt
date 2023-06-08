package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.viewstates.TracksViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllMusicFragment : ListFragment<Track, TracksViewState>() {
    override val viewState: TracksViewState by activityViewModels()
    override val manager: RecyclerView.LayoutManager = LinearLayoutManager(activity?.applicationContext)

    @Inject
    override lateinit var listAdapter: TracksAdapter

    override fun onSetup() {
        super.onSetup()
        viewState.apply {
            showItemEvent.observe(this@AllMusicFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                        putExtra(TRACK_ID, trackCombinedData.track.id)
                        putExtra(POSITION, trackCombinedData.position)
                    })
                }
            }
        }
    }
}