package com.app.musicplayer.ui.activities

import android.content.Intent
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.musicplayer.databinding.ActivityArtistDetailsBinding
import com.app.musicplayer.extentions.beVisibleIf
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService.Companion.tracksList
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.TracksViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArtistDetailsActivity : BaseActivity<TracksViewState>() {
    override val viewState: TracksViewState by viewModels()
    private val binding by lazy { ActivityArtistDetailsBinding.inflate(layoutInflater) }
    override val contentView: View by lazy { binding.root }

    @Inject
    lateinit var tracksAdapter: TracksAdapter
    override fun onSetup() {
        onSetupViews()
        viewState.apply {
            getTracksOfArtist(intent.getLongExtra(ARTIST_ID, 0L)) {
                tracksAdapter.items = it
                showEmpty(tracksAdapter.items.isEmpty())
                tracksList = it as ArrayList<Track>
            }
            showItemEvent.observe(this@ArtistDetailsActivity) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(Intent(this@ArtistDetailsActivity, MusicPlayerActivity::class.java).apply {
                        putExtra(TRACK_ID, trackCombinedData.track.id)
                        putExtra(POSITION, trackCombinedData.position)
                    })
                }
            }
//            itemsChangedEvent.observe(this@AlbumDetailsActivity) { event ->
//                event.ifNew?.let {
//                    tracksAdapter.items = it
//                    showEmpty(tracksAdapter.items.isEmpty())
//                }
//            }
            getItemsObservable { it.observe(this@ArtistDetailsActivity, viewState::onItemsChanged) }
        }
        tracksAdapter.apply {
            setOnItemClickListener(viewState::setOnItemClickListener)
        }
    }

    private fun onSetupViews() {
        binding.artistsRv.apply {
            this.layoutManager = LinearLayoutManager(applicationContext)
            this.adapter = tracksAdapter
        }
        binding.title.text = intent.getStringExtra(ARTIST_TITLE)
        val builder = SpannableStringBuilder()
        val songs = intent.getStringExtra(SONGS_IN_ARTIST)
        val albums = intent.getStringExtra(ALBUMS_IN_ARTIST)
        binding.songsAndAlbums.text =
            builder.append("$songs Songs").append(" • ").append("$albums Albums")
        binding.moveBack.setOnClickListener { finish() }
    }

    private fun showEmpty(isShow: Boolean) {
        binding.apply {
            empty.emptyImage.beVisibleIf(isShow)
            empty.emptyText.beVisibleIf(isShow)
            artistsRv.beVisibleIf(!isShow)
        }
    }
}