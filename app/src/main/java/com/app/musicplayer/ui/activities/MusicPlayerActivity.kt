package com.app.musicplayer.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.app.musicplayer.R
import com.app.musicplayer.databinding.ActivityMusicPlayerBinding
import com.app.musicplayer.extentions.*
import com.app.musicplayer.helpers.PreferenceHelper
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService
import com.app.musicplayer.services.MusicService.Companion.tracksList
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.MusicPlayerViewState
import com.app.musicplayer.utils.*
import com.bumptech.glide.Glide
import com.realpacific.clickshrinkeffect.applyClickShrink
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerActivity : BaseActivity<MusicPlayerViewState>() {

    @Inject
    lateinit var pref: PreferenceHelper

    private val binding by lazy { ActivityMusicPlayerBinding.inflate(layoutInflater) }
    var track: Track? = null
    override val viewState: MusicPlayerViewState by viewModels()
    override val contentView: View by lazy { binding.root }
    private val intentProgressDurationFilter = IntentFilter(PROGRESS_CONTROLS_ACTION)
    private val intentNextPrevious = IntentFilter(NEXT_PREVIOUS_ACTION)
    private val intentPlayPause = IntentFilter(PLAY_PAUSE_ACTION)
    private val intentDismiss = IntentFilter(DISMISS_PLAYER_ACTION)
    private val intentComplete = IntentFilter(TRACK_COMPLETE_ACTION)

    companion object {
//        var tracksList = ArrayList<Track>()
    }

    var playerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            when (intent?.action) {
                PROGRESS_CONTROLS_ACTION -> {
                    val position = intent.getIntExtra(GET_CURRENT_POSITION, 0)
                    val duration = intent.getIntExtra(GET_TRACK_DURATION, 0)
                    binding.seekBar.max = duration
                    binding.seekBar.progress = position
                    setUpSeekbar()
                    binding.playedDuration.text = formatMillisToHMS(position.toLong())
                }

                NEXT_PREVIOUS_ACTION -> {
                    if ((intent.getLongExtra(NEXT_PREVIOUS_TRACK_ID, 0L)) != 0L) {
                        updateTrackInfo(intent.getLongExtra(NEXT_PREVIOUS_TRACK_ID, 0L))
                    }
                }

                DISMISS_PLAYER_ACTION -> {
                    if (intent.getBooleanExtra(DISMISS_PLAYER, false)) {
                        finish()
                    }
                }

                TRACK_COMPLETE_ACTION -> {
                    if (intent.getBooleanExtra(TRACK_COMPLETED, false)) {
                        binding.nextTrack.performClick()
                    }
                }

                PLAY_PAUSE_ACTION -> {
                    when (intent.getBooleanExtra(PLAY_PAUSE, true)) {
                        true -> binding.playPauseTrack.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MusicPlayerActivity,
                                R.drawable.ic_pause
                            )
                        )

                        false -> binding.playPauseTrack.setImageDrawable(
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
    lateinit var tracksInteractor: TracksInteractor

    override fun onSetup() {
        setUpButtons()
        registerReceivers()
        updateTrackInfo(intent.getLongExtra(TRACK_ID, 0L))
        viewState.apply {
            itemsChangedEvent.observe(this@MusicPlayerActivity) { event ->
                event.ifNew?.let {
                    tracksList = it as ArrayList<Track>
                }
            }
            getItemsObservable { it.observe(this@MusicPlayerActivity, viewState::onItemsChanged) }
        }
        Intent(this, MusicService::class.java).apply {
            putExtra(TRACK_ID_SERVICE, intent.getLongExtra(TRACK_ID, 0L))
            putExtra(POSITION, intent.getIntExtra(POSITION, 0))
            action = INIT
            startService(this)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun updateTrackInfo(id: Long) {
        tracksInteractor.queryTrack(id) { track ->
            binding.trackName.text = track?.title
            binding.artistName.text = track?.artist
            binding.totalDuration.text = track?.let { formatMillisToHMS(it.duration) }
            Glide.with(this@MusicPlayerActivity).load(track?.album_id?.getThumbnailUri())
                .placeholder(R.drawable.ic_music).into(binding.thumbnail)
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
            }

        })
    }

    private fun setUpButtons() {
        binding.playPauseTrack.applyClickShrink()
        binding.nextTrack.applyClickShrink()
        binding.previousTrack.applyClickShrink()
        binding.back.setOnClickListener { finish() }
        binding.playPauseTrack.setOnClickListener { sendIntent(PLAYPAUSE) }
        binding.nextTrack.setOnClickListener { sendIntent(NEXT) }
        binding.previousTrack.setOnClickListener { sendIntent(PREVIOUS) }
        binding.repeatTrack.setOnClickListener { repeatTrackSettings() }
        binding.shuffleTrack.setOnClickListener {  }
    }

    private fun repeatTrackSettings() {

        pref.repeatTrack = REPEAT_TRACK_ON
        binding.repeatTrack.setBackgroundColor(ContextCompat.getColor(this,R.color.purple))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(playerReceiver)
        sendIntent(FINISH_IF_NOT_PLAYING)
    }

    private fun registerReceivers() {
        registerReceiver(playerReceiver, intentProgressDurationFilter)
        registerReceiver(playerReceiver, intentNextPrevious)
        registerReceiver(playerReceiver, intentPlayPause)
        registerReceiver(playerReceiver, intentDismiss)
        registerReceiver(playerReceiver, intentComplete)
    }
}