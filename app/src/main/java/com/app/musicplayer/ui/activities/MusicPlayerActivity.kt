package com.app.musicplayer.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.app.musicplayer.R
import com.app.musicplayer.databinding.ActivityMusicPlayerBinding
import com.app.musicplayer.extentions.*
import com.app.musicplayer.interator.songs.SongsInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.MusicPlayerViewState
import com.app.musicplayer.utils.*
import com.realpacific.clickshrinkeffect.applyClickShrink
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerActivity : BaseActivity<MusicPlayerViewState>() {
    private val binding by lazy { ActivityMusicPlayerBinding.inflate(layoutInflater) }
    var track: Track? = null
    override val viewState: MusicPlayerViewState by viewModels()
    override val contentView: View by lazy { binding.root }
    val intentProgressDurationFilter = IntentFilter(CURRENT_POSITION_ACTION)
    var playerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            when (intent?.action) {
                CURRENT_POSITION_ACTION -> {
                    val position = intent.getIntExtra(GET_CURRENT_POSITION, 0)
                    val duration = intent.getIntExtra(GET_TRACK_DURATION, 0)
                    binding.seekBar.max = duration
                    binding.seekBar.progress = position
                    setUpSeekbar()
                    binding.playedDuration.text = formatMillisToHMS(position.toLong())

                    val play_pause = intent.getBooleanExtra(PLAY_PAUSE,true)
                    val complete = intent.getStringExtra(COMPLETE)
                    if (complete == COMPLETE) {
                        onBackPressed()
//                        binding.seekBar.progress = 0
//                        binding.playedDuration.text = "00:00"
//                        binding.playPause.setImageDrawable(
//                            ContextCompat.getDrawable(
//                                this@MusicPlayerActivity,
//                                R.drawable.ic_play
//                            )
//                        )
                    }
                    when (play_pause) {
                        true -> binding.playPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MusicPlayerActivity,
                                R.drawable.ic_pause
                            )
                        )

                        false -> binding.playPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MusicPlayerActivity,
                                R.drawable.ic_play
                            )
                        )
                    }
                }
            }
        }
    }

    @Inject
    lateinit var songsInteractor: SongsInteractor

    override fun onSetup() {
        clickListenersAndShrinks()
        Intent(this, MusicService::class.java).apply {
            putExtra(TRACK_ID_SERVICE, intent.getLongExtra(TRACK_ID, 0L))
            action = INIT
            try {
                startService(this)
            } catch (e: Exception) {
            }
        }
        registerReceiver(playerReceiver, intentProgressDurationFilter)
        updateUI()
        viewState.apply {}
    }

    override fun onResume() {
        super.onResume()
//        registerReceiver(playerReceiver,intentProgressDurationFilter)
    }

    private fun updateUI() {
        songsInteractor.querySong(intent.getLongExtra(TRACK_ID, 0L)) { track ->
            binding.songName.text = track?.title
            binding.artistName.text = track?.artist
            binding.totalDuration.text = track?.let { formatMillisToHMS(it.duration) }
        }
    }

    private fun setUpSeekbar() {
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekbar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekbar: SeekBar) {
                Intent(this@MusicPlayerActivity, MusicService::class.java).apply {
                    putExtra(PROGRESS, seekbar.progress)
                    action = SET_PROGRESS
                    startService(this)
                }
            }

            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
//                val formattedProgress = progress.getFormattedDuration()
//                binding.playedDuration.text = formattedProgress
            }

        })
    }

    private fun clickListenersAndShrinks() {
        binding.playPause.applyClickShrink()
        binding.nextSong.applyClickShrink()
        binding.previousSong.applyClickShrink()
        binding.back.setOnClickListener { finish() }
        binding.playPause.setOnClickListener { sendIntent(PLAYPAUSE) }
        binding.nextSong.setOnClickListener { sendIntent(NEXT) }
        binding.previousSong.setOnClickListener { sendIntent(PREVIOUS) }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(playerReceiver)
        sendIntent(FINISH_IF_NOT_PLAYING)
    }
}