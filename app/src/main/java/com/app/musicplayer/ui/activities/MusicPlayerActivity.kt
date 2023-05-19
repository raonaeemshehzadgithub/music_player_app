package com.app.musicplayer.ui.activities

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import com.app.musicplayer.databinding.ActivityMusicPlayerBinding
import com.app.musicplayer.extentions.*
import com.app.musicplayer.interator.songs.SongsInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.MusicPlayerViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerActivity : BaseActivity<MusicPlayerViewState>() {
    private val binding by lazy { ActivityMusicPlayerBinding.inflate(layoutInflater) }
    var track: Track? = null
    override val viewState: MusicPlayerViewState by viewModels()
    override val contentView: View by lazy { binding.root }

    @Inject
    lateinit var songsInteractor: SongsInteractor

    override fun onSetup() {
        clickListeners()
        Intent(this, MusicService::class.java).apply {
            putExtra(TRACK_ID_SERVICE, intent.getLongExtra(TRACK_ID, 0L))
            action = INIT
            try {
                startService(this)
            } catch (e: Exception) {
            }
        }
        updateUI()
        viewState.apply {}
    }

    private fun updateUI() {
        songsInteractor.querySong(intent.getLongExtra(TRACK_ID, 0L)) { track ->
            binding.songName.text = track?.title
            binding.artistName.text = track?.artist
            binding.totalDuration.text = track?.let { formatMillisToHMS(it.duration) }
//            track?.let { setUpSeekbar(it.duration) }
        }

    }

    private fun setUpSeekbar(duration: Long) {
//        binding.seekBar.max = (duration / 1000).toInt()
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                val formattedProgress = progress.getFormattedDuration()
                binding.playedDuration.text = formattedProgress
            }

        })
    }

    private fun clickListeners() {
        binding.back.setOnClickListener { finish() }
        binding.playPause.setOnClickListener { sendIntent(PLAYPAUSE) }
        binding.nextSong.setOnClickListener { sendIntent(NEXT) }
        binding.previousSong.setOnClickListener { sendIntent(PREVIOUS) }
    }

    override fun onDestroy() {
        super.onDestroy()
        sendIntent(FINISH_IF_NOT_PLAYING)
    }
}