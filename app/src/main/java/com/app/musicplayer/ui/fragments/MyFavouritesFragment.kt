package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.extentions.updateFavoriteIcon
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService.Companion.tracksList
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.adapters.FavoritesAdapter
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.viewstates.FavoritesViewState
import com.app.musicplayer.utils.POSITION
import com.app.musicplayer.utils.TRACK_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFavouritesFragment : ListFragment<Track, FavoritesViewState>() {
    override val viewState: FavoritesViewState by activityViewModels()
    override val manager: RecyclerView.LayoutManager =
        LinearLayoutManager(activity?.applicationContext)
    private var favoriteList: ArrayList<Track>? = null

    @Inject
    override lateinit var listAdapter: FavoritesAdapter
    override fun onSetup() {
        super.onSetup()
        viewState.fetchFavoriteTrackList().observe(this){
            listAdapter.items = it
            showEmpty(listAdapter.items.isEmpty())
            favoriteList = it as ArrayList<Track>
        }
        viewState.showItemEvent.observe(this) { event ->
            event.ifNew?.let { trackCombinedData ->
                startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                    putExtra(TRACK_ID, trackCombinedData.track.id)
                    putExtra(POSITION, trackCombinedData.position)
                })
            }
        }
        viewState.showFavoriteEvent.observe(this){event->
            event.ifNew?.let { track->
                context?.toast(getString(R.string.remove_favorites))
                viewState.removeFavoriteTrack(track.id ?: 0L)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tracksList = favoriteList!!
    }

}