package com.app.musicplayer.ui.fragments

import androidx.fragment.app.activityViewModels
import com.app.musicplayer.models.Album
import com.app.musicplayer.ui.adapters.AlbumsAdapter
import com.app.musicplayer.ui.viewstates.AlbumsViewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlbumsFragment : ListFragment<Album, AlbumsViewState>() {
    override val viewState: AlbumsViewState by activityViewModels()
    @Inject
    override lateinit var listAdapter: AlbumsAdapter

    override fun onSetup() {
        super.onSetup()
        viewState.apply {
            showItemEvent.observe(this@AlbumsFragment) { event ->
                event.ifNew?.let { album ->
                }
            }
        }
    }

}