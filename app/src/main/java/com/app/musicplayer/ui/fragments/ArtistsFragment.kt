package com.app.musicplayer.ui.fragments

import androidx.fragment.app.activityViewModels
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.models.Artist
import com.app.musicplayer.ui.adapters.ArtistsAdapter
import com.app.musicplayer.ui.viewstates.ArtistsViewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArtistsFragment : ListFragment<Artist, ArtistsViewState>() {
    override val viewState: ArtistsViewState by activityViewModels()

    @Inject
    override lateinit var listAdapter: ArtistsAdapter

    override fun onSetup() {
        super.onSetup()
        viewState.apply {
            showItemEvent.observe(this@ArtistsFragment) { event ->
                event.ifNew?.let { artist ->
                }
            }
        }
    }
}