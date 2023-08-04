package com.app.musicplayer.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.musicplayer.databinding.ActivityArtistDetailsBinding
import com.app.musicplayer.extentions.beVisibleIf
import com.app.musicplayer.extentions.deleteTrack
import com.app.musicplayer.extentions.shareTrack
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService.Companion.tracksList
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.ArtistDetailsViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArtistDetailsActivity : BaseActivity<ArtistDetailsViewState>() {
    override val viewState: ArtistDetailsViewState by viewModels()
    private val binding by lazy { ActivityArtistDetailsBinding.inflate(layoutInflater) }
    override val contentView: View by lazy { binding.root }

    @Inject
    lateinit var tracksAdapter: TracksAdapter
    @Inject
    lateinit var tracksInteractor: TracksInteractor
    @SuppressLint("SetTextI18n")
    override fun onSetup() {
        onSetupViews()
        viewState.apply {
            showItemEvent.observe(this@ArtistDetailsActivity) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(Intent(this@ArtistDetailsActivity, MusicPlayerActivity::class.java).apply {
                        putExtra(TRACK_ID, trackCombinedData.track.id)
                        putExtra(POSITION, trackCombinedData.position)
                    })
                }
            }
            showMenuEvent.observe(this@ArtistDetailsActivity) { event ->
                event.ifNew?.let { trackCombinedData ->
                    trackCombinedData.view?.let {
                        showTrackMenu(it) { callback ->
                            when (callback) {
                                PLAY_TRACK -> {
                                    startActivity(Intent(this@ArtistDetailsActivity, MusicPlayerActivity::class.java).apply {
                                        putExtra(TRACK_ID, trackCombinedData.track.id)
                                        putExtra(POSITION, trackCombinedData.position)
                                    })
                                }
                                SHARE_TRACK -> {
                                    trackCombinedData.track.path?.shareTrack(this@ArtistDetailsActivity)
                                }
                                DELETE_TRACK -> {
                                    if (isRPlus()) {
                                        deleteTrack(DELETE_TRACK_CODE, trackCombinedData.track.id ?: 0L)
                                    }
                                }
                                RENAME_TRACK -> {
                                    bsRenameTrack(trackCombinedData.track.title ?: "") {renamedText->
                                        tracksInteractor.renameTrack(trackCombinedData, renamedText)
                                    }
                                }
                                PROPERTIES_TRACK->{
                                    showTrackPropertiesDialog(trackCombinedData)
                                }
                            }
                        }
                    }
                }
            }
            itemsChangedEvent.observe(this@ArtistDetailsActivity) { event ->
                event.ifNew?.let {list->
                    tracksAdapter.items = list
                    showEmpty(tracksAdapter.items.isEmpty())
                    tracksList = list as ArrayList<Track>
                    binding.songsAndAlbums.text = "${list.size} Songs"
                }
            }
            queryArtistDetails(intent.getLongExtra(ARTIST_ID, 0L))
            getItemsObservable { it.observe(this@ArtistDetailsActivity, viewState::onItemsChanged) }
        }
        tracksAdapter.apply {
            setOnItemClickListener(viewState::setOnItemClickListener)
            setOnMenuClickListener(viewState::setOnMenuClickListener)
        }
    }

    private fun onSetupViews() {
        binding.artistsRv.apply {
            this.layoutManager = LinearLayoutManager(applicationContext)
            this.adapter = tracksAdapter
        }
        binding.title.text = intent.getStringExtra(ARTIST_TITLE)
//        val builder = SpannableStringBuilder()
//        val songs = intent.getStringExtra(SONGS_IN_ARTIST)
//        val albums = intent.getStringExtra(ALBUMS_IN_ARTIST)
//        binding.songsAndAlbums.text ="${tracksList.size} Songs"
//            builder.append("$songs Songs").append(" • ").append("$albums Albums")
        binding.moveBack.setOnClickListener { finish() }
    }

    private fun showEmpty(isShow: Boolean) {
        binding.apply {
            empty.emptyImage.beVisibleIf(isShow)
            empty.emptyText.beVisibleIf(isShow)
            artistsRv.beVisibleIf(!isShow)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}