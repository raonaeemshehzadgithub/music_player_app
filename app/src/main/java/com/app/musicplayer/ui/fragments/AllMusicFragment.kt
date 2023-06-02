package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.viewstates.alltracks.TracksViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllMusicFragment : ListFragment<Track, TracksViewState>() {
    override val viewState: TracksViewState by activityViewModels()

    @Inject
    override lateinit var listAdapter: TracksAdapter

    override fun onSetup() {
        super.onSetup()
        viewState.apply {
            showItemEvent.observe(this@AllMusicFragment) { event ->
                event.ifNew?.let { combinedData ->
                    startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                        putExtra(TRACK_ID, combinedData.track.id)
                        putExtra(POSITION, combinedData.position)
                    })
                }
            }
        }
    }
}