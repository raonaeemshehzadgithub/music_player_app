package com.app.musicplayer.ui.activities

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.musicplayer.databinding.ActivityAlbumDetailsBinding
import com.app.musicplayer.extentions.beVisibleIf
import com.app.musicplayer.extentions.deleteTrack
import com.app.musicplayer.extentions.shareTrack
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService.Companion.tracksList
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.AlbumDetailsViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlbumDetailsActivity : BaseActivity<AlbumDetailsViewState>() {
    override val viewState: AlbumDetailsViewState by viewModels()
    private val binding by lazy { ActivityAlbumDetailsBinding.inflate(layoutInflater) }
    override val contentView: View by lazy { binding.root }

    @Inject
    lateinit var tracksAdapter: TracksAdapter

    @Inject
    lateinit var tracksInteractor: TracksInteractor

    override fun onSetup() {
        onSetupViews()
        viewState.apply {
            showItemEvent.observe(this@AlbumDetailsActivity) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(
                        Intent(
                            this@AlbumDetailsActivity,
                            MusicPlayerActivity::class.java
                        ).apply {
                            putExtra(TRACK_ID, trackCombinedData.track.id)
                            putExtra(POSITION, trackCombinedData.position)
                            putExtra(PLAYER_LIST, FROM_ALBUM_LIST)
                            putExtra(ALBUM_ID, intent.getLongExtra(ALBUM_ID, 0L))
                        })
                }
            }
            showMenuEvent.observe(this@AlbumDetailsActivity) { event ->
                event.ifNew?.let { trackCombinedData ->
                    trackCombinedData.view?.let {
                        showTrackMenu(it) { callback ->
                            when (callback) {
                                PLAY_TRACK -> {
                                    startActivity(
                                        Intent(
                                            this@AlbumDetailsActivity,
                                            MusicPlayerActivity::class.java
                                        ).apply {
                                            putExtra(TRACK_ID, trackCombinedData.track.id)
                                            putExtra(POSITION, trackCombinedData.position)
                                            putExtra(PLAYER_LIST, FROM_ALBUM_LIST)
                                            putExtra(ALBUM_ID, intent.getLongExtra(ALBUM_ID, 0L))
                                        })
                                }

                                SHARE_TRACK -> {
                                    trackCombinedData.track.path?.shareTrack(this@AlbumDetailsActivity)
                                }

                                DELETE_TRACK -> {
                                    if (isRPlus()) {
                                        deleteTrack(
                                            DELETE_TRACK_CODE,
                                            trackCombinedData.track.id ?: 0L
                                        )
                                    }
                                }

                                RENAME_TRACK -> {
                                    bsRenameTrack(
                                        trackCombinedData.track.title ?: ""
                                    ) { renamedText ->
                                        tracksInteractor.renameTrack(trackCombinedData, renamedText)
                                    }
                                }

                                PROPERTIES_TRACK -> {
                                    showTrackPropertiesDialog(trackCombinedData)
                                }
                            }
                        }
                    }
                }
            }
            itemsChangedEvent.observe(this@AlbumDetailsActivity) { event ->
                event.ifNew?.let {
                    tracksAdapter.items = it
                    showEmpty(tracksAdapter.items.isEmpty())
                }
            }
            queryAlbumDetails(intent.getLongExtra(ALBUM_ID, 0L))
            getItemsObservable { it.observe(this@AlbumDetailsActivity, viewState::onItemsChanged) }
        }
        tracksAdapter.apply {
            setOnItemClickListener(viewState::setOnItemClickListener)
            setOnMenuClickListener(viewState::setOnMenuClickListener)
        }
    }

    private fun onSetupViews() {
        binding.albumsRv.apply {
            this.layoutManager = LinearLayoutManager(applicationContext)
            this.adapter = tracksAdapter
        }
        binding.title.text = intent.getStringExtra(ALBUM_TITLE)
        binding.moveBack.setOnClickListener { finish() }
    }

    private fun showEmpty(isShow: Boolean) {
        binding.apply {
            empty.emptyImage.beVisibleIf(isShow)
            empty.emptyText.beVisibleIf(isShow)
            albumsRv.beVisibleIf(!isShow)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}