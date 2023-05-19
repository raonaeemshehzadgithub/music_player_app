package com.app.musicplayer.ui.fragments

import android.content.Intent
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.viewstates.SongsViewState
import com.app.musicplayer.utils.TRACK_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllMusicFragment : ListFragment<Track, SongsViewState>() {
    override val viewState: SongsViewState by activityViewModels()

    @Inject
    override lateinit var listAdapter: com.app.musicplayer.ui.adapters.allmusic.SongsAdapter

    override fun onSetup() {
        super.onSetup()
        viewState.apply {
            showItemEvent.observe(this@AllMusicFragment) { event ->
                event.ifNew?.let { track ->
                    startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                        putExtra(TRACK_ID, track.id)
                    })
                }
            }
        }
    }
}