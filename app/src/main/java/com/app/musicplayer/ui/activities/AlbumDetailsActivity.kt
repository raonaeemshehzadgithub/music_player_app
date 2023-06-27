package com.app.musicplayer.ui.activities

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.musicplayer.databinding.ActivityAlbumDetailsBinding
import com.app.musicplayer.extentions.beVisibleIf
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.TracksViewState
import com.app.musicplayer.utils.ALBUM_ID
import com.app.musicplayer.utils.ALBUM_TITLE
import com.app.musicplayer.utils.POSITION
import com.app.musicplayer.utils.TRACK_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlbumDetailsActivity : BaseActivity<TracksViewState>() {
    override val viewState: TracksViewState by viewModels()
    private val binding by lazy { ActivityAlbumDetailsBinding.inflate(layoutInflater) }
    override val contentView: View by lazy { binding.root }
    @Inject
    lateinit var tracksAdapter: TracksAdapter

    override fun onSetup() {
        onSetupViews()
        Log.wtf("album id", intent.getLongExtra(ALBUM_ID, 0L).toString())
        viewState.apply {
            getTracksOfAlbum(intent.getLongExtra(ALBUM_ID, 0L)){
                tracksAdapter.items = it
                showEmpty(tracksAdapter.items.isEmpty())
            }
            showItemEvent.observe(this@AlbumDetailsActivity) { event ->
                event.ifNew?.let { trackCombinedData ->
//                    startActivity(Intent(this@AlbumDetailsActivity, MusicPlayerActivity::class.java).apply {
//                        putExtra(TRACK_ID, trackCombinedData.track.id)
//                        putExtra(POSITION, trackCombinedData.position)
//                    })
                }
            }
//            itemsChangedEvent.observe(this@AlbumDetailsActivity) { event ->
//                event.ifNew?.let {
//                    tracksAdapter.items = it
//                    showEmpty(tracksAdapter.items.isEmpty())
//                }
//            }
            getItemsObservable { it.observe(this@AlbumDetailsActivity, viewState::onItemsChanged) }
        }
        tracksAdapter.apply {
            setOnItemClickListener(viewState::setOnItemClickListener)
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

    protected open fun showEmpty(isShow: Boolean) {
        binding.apply {
            empty.emptyImage.beVisibleIf(isShow)
            empty.emptyText.beVisibleIf(isShow)
            albumsRv.beVisibleIf(!isShow)
        }
    }
}