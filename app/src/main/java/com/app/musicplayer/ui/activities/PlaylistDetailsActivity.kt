package com.app.musicplayer.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.musicplayer.databinding.ActivityPlaylistDetailsBinding
import com.app.musicplayer.extentions.beVisibleIf
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.PlaylistDetailsViewState
import com.app.musicplayer.utils.FROM_ALL_SONG
import com.app.musicplayer.utils.FROM_PLAYLIST
import com.app.musicplayer.utils.PLAYER_LIST
import com.app.musicplayer.utils.PLAYLIST_ID
import com.app.musicplayer.utils.PLAYLIST_NAME
import com.app.musicplayer.utils.POSITION
import com.app.musicplayer.utils.TRACK_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistDetailsActivity : BaseActivity<PlaylistDetailsViewState>() {
    override val viewState: PlaylistDetailsViewState by viewModels()
    private val binding by lazy { ActivityPlaylistDetailsBinding.inflate(layoutInflater) }
    override val contentView: View by lazy { binding.root }

    @Inject
    lateinit var tracksAdapter: TracksAdapter

    @Inject
    lateinit var tracksInteractor: TracksInteractor
    @SuppressLint("SetTextI18n")
    override fun onSetup() {
        onSetupViews()
        viewState.apply {
            lifecycleScope.launch {
                fetchSongIdsForPlaylist(intent.getLongExtra(PLAYLIST_ID, 0L))?.let { songIdsList ->
                    returnList(songIdsList) { trackList ->
                        tracksAdapter.items = trackList
                        showEmpty(tracksAdapter.items.isEmpty())
                    }
                }
            }
            showItemEvent.observe(this@PlaylistDetailsActivity){event->
                event.ifNew?.let {trackCombinedData->
                    startActivity(
                        Intent(
                            this@PlaylistDetailsActivity,
                            MusicPlayerActivity::class.java
                        ).apply {
                            putExtra(TRACK_ID, trackCombinedData.track.id)
                            putExtra(POSITION, trackCombinedData.position)
                            putExtra(PLAYER_LIST, FROM_PLAYLIST)
                            putExtra(PLAYLIST_ID, intent.getLongExtra(PLAYLIST_ID, 0L))
                        })
                }
            }
        }
        tracksAdapter.apply {
            setOnItemClickListener(viewState::setOnItemClickListener)
            setOnMenuClickListener(viewState::setOnMenuClickListener)
        }
    }

    private fun returnList(list: List<Long>, callback: (List<Track>) -> Unit) {
        val trackList = mutableListOf<Track>()
        var count = 0
        list.forEach { id ->
            tracksInteractor.queryTrack(id) { track ->
                track?.let {
                    trackList.add(it)
                }
                count++
                if (count == list.size) {
                    callback(trackList)
                }
            }
        }
    }


    private fun onSetupViews() {
        binding.playlistSongsRv.apply {
            this.layoutManager = LinearLayoutManager(applicationContext)
            this.adapter = tracksAdapter
        }
        binding.title.text = intent.getStringExtra(PLAYLIST_NAME)
        binding.moveBack.setOnClickListener { finish() }
    }

    private fun showEmpty(isShow: Boolean) {
        binding.apply {
            empty.emptyImage.beVisibleIf(isShow)
            empty.emptyText.beVisibleIf(isShow)
            playlistSongsRv.beVisibleIf(!isShow)
        }
    }
}