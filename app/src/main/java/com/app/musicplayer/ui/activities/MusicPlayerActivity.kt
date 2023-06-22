package com.app.musicplayer.ui.activities

import android.annotation.SuppressLint
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
import com.app.musicplayer.helpers.OnSwipeTouchListener
import com.app.musicplayer.helpers.PreferenceHelper
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService
import com.app.musicplayer.services.MusicService.Companion.positionTrack
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
    lateinit var tracksInteractor: TracksInteractor

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
                    when (intent.getBooleanExtra(PLAY_PAUSE_ICON, true)) {
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

    override fun onSetup() {
        handleNotificationPermission { granted ->
            if (granted) {
                setUpButtons()
                registerReceivers()
                updateTrackInfo(intent.getLongExtra(TRACK_ID, 0L))
                viewState.apply {
                    itemsChangedEvent.observe(this@MusicPlayerActivity) { event ->
                        event.ifNew?.let {
                            tracksList = it as ArrayList<Track>
                        }
                    }
                    getItemsObservable {
                        it.observe(
                            this@MusicPlayerActivity,
                            viewState::onItemsChanged
                        )
                    }
                }
                if (!intent.getBooleanExtra(FROM_MINI_PLAYER, false)) {
                    Intent(this, MusicService::class.java).apply {
                        putExtra(TRACK_ID_SERVICE, intent.getLongExtra(TRACK_ID, 0L))
                        putExtra(POSITION, intent.getIntExtra(POSITION, 0))
                        action = INIT
                        startService(this)
                    }
                }
            }
        }
    }

    private fun updateTrackInfo(id: Long) {
        tracksInteractor.queryTrack(id) { track ->
            binding.trackName.isSelected = true
            binding.trackName.text = track?.title ?: ""
            binding.artistName.text = track?.artist?.isUnknownString() ?: ""
            binding.totalDuration.text = formatMillisToHMS(track?.duration ?: 0L)
            Glide.with(this@MusicPlayerActivity).load(track?.album_id?.getThumbnailUri() ?: "")
                .placeholder(R.drawable.ic_music).into(binding.thumbnail)
        }
    }

    private fun setUpSeekbar() {
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(seekbar: SeekBar) {}
            override fun onStopTrackingTouch(seekbar: SeekBar) {
                Intent(this@MusicPlayerActivity, MusicService::class.java).apply {
                    putExtra(PROGRESS, seekbar.progress)
                    action = SET_PROGRESS
                    startService(this)
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpButtons() {
        binding.apply {
            playPauseTrack.applyClickShrink()
            nextTrack.applyClickShrink()
            previousTrack.applyClickShrink()
            repeatTrack.applyClickShrink()
            shuffleTrack.applyClickShrink()
            favouriteTrack.applyClickShrink()
            playerMenuMore.applyClickShrink()
            back.setOnClickListener { finish() }
            playPauseTrack.setOnClickListener { sendIntent(PLAYPAUSE) }
            nextTrack.setOnClickListener { sendIntent(NEXT) }
            previousTrack.setOnClickListener { sendIntent(PREVIOUS) }
            repeatTrack.setOnClickListener { repeatTrack() }
            shuffleTrack.setOnClickListener { shuffleTrack() }
            favouriteTrack.setOnClickListener { }
            playerMenuMore.setOnClickListener { playerMenus() }
            root.setOnTouchListener(object : OnSwipeTouchListener(this@MusicPlayerActivity) {
                override fun onSwipeDown() {
                    super.onSwipeDown()
                    finish()
                    overridePendingTransition(0, R.anim.slide_down)
                }
            })
        }
        setUpPreferences()
    }

    private fun playerMenus() {
        playerMenu(binding.playerMenuMore) {
            when (it) {
                SHARE_TRACK -> {
                    tracksList[positionTrack].path?.shareTrack(this@MusicPlayerActivity) ?: ""
                }

                DELETE_TRACK -> {
                    if (isRPlus()) {
                        deleteTrack(tracksList[positionTrack].id ?: 0L)
                    }
                }

                SETTINGS -> {
                    startActivity(Intent(this@MusicPlayerActivity, SettingsActivity::class.java))
                }

                SET_TRACK_AS -> {
//                    ensureBackgroundThread {
//                        bsSetRingtone {
//                            when (it) {
//                                DONE -> {
//                                    when (pref.setRingtone) {
//                                        PHONE_RINGTONE -> viewState.setRingtone(
//                                            context = this@MusicPlayerActivity,
//                                            trackId = tracksList[positionTrack].id
//                                        )
//
//                                        ALARM_RINGTONE -> toast("")
//                                    }
//                                }
//                            }
//                        }
//                    }
                }
            }
        }
    }

    private fun setUpPreferences() {
        when (pref.repeatTrack) {
            REPEAT_TRACK_ON -> binding.repeatTrack.setSelectedTint(context = this)
        }
        when (pref.shuffleTrack) {
            SHUFFLE_TRACK_ON -> binding.shuffleTrack.setSelectedTint(context = this)
        }
        binding.seekBar.max = pref.currentTrackTotalDuration?:0
        binding.seekBar.progress = pref.currentTrackProgress?:0
        setUpSeekbar()
        binding.playedDuration.text = pref.currentTrackProgress?.let { formatMillisToHMS(it.toLong()) }
    }

    private fun repeatTrack() {
        when {
            pref.shuffleTrack == SHUFFLE_TRACK_ON -> {
                pref.shuffleTrack = SHUFFLE_TRACK_OFF
                binding.shuffleTrack.setUnSelectedTint(context = this)
            }

            pref.repeatTrack == REPEAT_TRACK_OFF -> {
                pref.repeatTrack = REPEAT_TRACK_ON
                binding.repeatTrack.setSelectedTint(context = this)
            }

            pref.repeatTrack == REPEAT_TRACK_ON -> {
                pref.repeatTrack = REPEAT_TRACK_OFF
                binding.repeatTrack.setUnSelectedTint(context = this)
            }
        }
    }

    private fun shuffleTrack() {
        when {
            pref.repeatTrack == REPEAT_TRACK_ON -> {
                pref.repeatTrack = REPEAT_TRACK_OFF
                binding.repeatTrack.setUnSelectedTint(context = this)
            }

            pref.shuffleTrack == SHUFFLE_TRACK_OFF -> {
                pref.shuffleTrack = SHUFFLE_TRACK_ON
                binding.shuffleTrack.setSelectedTint(context = this)
            }

            pref.shuffleTrack == SHUFFLE_TRACK_ON -> {
                pref.shuffleTrack = SHUFFLE_TRACK_OFF
                binding.shuffleTrack.setUnSelectedTint(context = this)
            }
        }
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