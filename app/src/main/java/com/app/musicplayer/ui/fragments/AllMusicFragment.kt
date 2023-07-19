package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.extentions.deleteTrack
import com.app.musicplayer.extentions.shareTrack
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService.Companion.tracksList
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.viewstates.TracksViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllMusicFragment : ListFragment<Track, TracksViewState>() {
    override val viewState: TracksViewState by activityViewModels()
    override val manager: RecyclerView.LayoutManager =
        LinearLayoutManager(activity?.applicationContext)

    @Inject
    override lateinit var listAdapter: TracksAdapter
    @Inject
    lateinit var tracksInteractor: TracksInteractor

    override fun onSetup() {
        super.onSetup()
        listAdapter.viewHolderType = ALL_TRACKS_VT
        viewState.apply {
            getTrackList { trList ->
                tracksList = trList as ArrayList<Track>
            }
            showItemEvent.observe(this@AllMusicFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                        putExtra(TRACK_ID, trackCombinedData.track.id)
                        putExtra(POSITION, trackCombinedData.position)
                    })
                }
            }
            showMenuEvent.observe(this@AllMusicFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    trackCombinedData.view?.let {
                        baseActivity.showTrackMenu(it) { callback ->
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
                                    if (isRPlus()) {
                                        activity?.deleteTrack(
                                            DELETE_TRACK_CODE,
                                            trackCombinedData.track.id ?: 0L
                                        )
                                    }
                                }

                                RENAME_TRACK -> {
                                    baseActivity.showTrackRenameMenu(
                                        trackCombinedData.track.title ?: ""
                                    ) { renamedText ->
                                        tracksInteractor.renameTrack(trackCombinedData, renamedText)
                                    }
                                }
                                PROPERTIES_TRACK->{
                                    baseActivity.showTrackPropertiesDialog(trackCombinedData)
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
        viewState.getTrackList { trList ->
            tracksList = trList as ArrayList<Track>
        }
    }
}