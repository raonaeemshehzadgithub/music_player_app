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
import androidx.lifecycle.lifecycleScope
import com.app.musicplayer.R
import com.app.musicplayer.databinding.ActivityMusicPlayerBinding
import com.app.musicplayer.extentions.*
import com.app.musicplayer.helpers.OnSwipeTouchListener
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.services.MusicService
import com.app.musicplayer.services.MusicService.Companion.positionTrack
import com.app.musicplayer.services.MusicService.Companion.tracksList
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.MusicPlayerViewState
import com.app.musicplayer.utils.*
import com.bumptech.glide.Glide
import com.realpacific.clickshrinkeffect.applyClickShrink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerActivity : BaseActivity<MusicPlayerViewState>() {

    @Inject
    lateinit var tracksInteractor: TracksInteractor

    private val binding by lazy { ActivityMusicPlayerBinding.inflate(layoutInflater) }
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
                        prefs.currentTrackId = intent.getLongExtra(NEXT_PREVIOUS_TRACK_ID, 0L)
                        updateTrackInfo(prefs.currentTrackId ?: 0L)
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
                        true -> binding.playPauseTrack.updatePlayIcon(this@MusicPlayerActivity, false)
                        false -> binding.playPauseTrack.updatePlayIcon(this@MusicPlayerActivity, true)
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

    override fun onPause() {
        super.onPause()
        tracksInteractor.queryTrack(prefs.currentTrackId ?: 0L) { track ->
            track?.toRecentTrackEntity()?.let { viewState.insertRecentTrack(it) }
        }
    }

    private fun updateTrackInfo(id: Long) {
        lifecycleScope.launch {
            viewState.fetchFavorites().let { list ->
                tracksInteractor.queryTrack(id) { track ->
                    if (list?.contains(track) == true) {
                        binding.favouriteTrack.updateFavoriteIcon(this@MusicPlayerActivity, true)
                    } else {
                        binding.favouriteTrack.updateFavoriteIcon(this@MusicPlayerActivity, false)
                    }
                    binding.trackName.isSelected = true
                    binding.trackName.text = track?.title ?: ""
                    binding.artistName.text = track?.artist?.isUnknownString() ?: ""
                    binding.totalDuration.text = formatMillisToHMS(track?.duration ?: 0L)
                    updatePlayPauseDrawable(binding.playPauseTrack, this@MusicPlayerActivity)
                    Glide.with(this@MusicPlayerActivity)
                        .load(track?.albumId?.getThumbnailUri() ?: "")
                        .placeholder(R.drawable.ic_music).into(binding.thumbnail)
                }
            }
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
            playerMenuMore.setOnClickListener { playerMenus() }
            favouriteTrack.setOnClickListener { favoriteTrack() }
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
                        deleteTrack(DELETE_PLAYING_TRACK, tracksList[positionTrack].id ?: 0L)
                    }
                }

                SETTINGS -> {
                    startActivity(Intent(this@MusicPlayerActivity, SettingsActivity::class.java))
                }

                SET_TRACK_AS -> {
                    bsSetRingtone {
                        when (it) {
                            DONE -> {
                                when (prefs.setRingtone) {
                                    PHONE_RINGTONE -> viewState.setRingtone(
                                        context = this@MusicPlayerActivity,
                                        trackId = tracksList[positionTrack].id ?: 0L
                                    )

                                    ALARM_RINGTONE -> {
                                        tracksList[positionTrack].path?.setDefaultAlarmTone(this@MusicPlayerActivity)
                                            ?: ""
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setUpPreferences() {
        when (prefs.repeatTrack) {
            REPEAT_TRACK_ON -> binding.repeatTrack.setSelectedTint(context = this)
        }
        when (prefs.shuffleTrack) {
            SHUFFLE_TRACK_ON -> binding.shuffleTrack.setSelectedTint(context = this)
        }
        binding.seekBar.max = prefs.currentTrackTotalDuration ?: 0
        binding.seekBar.progress = prefs.currentTrackProgress ?: 0
        setUpSeekbar()
        binding.playedDuration.text =
            prefs.currentTrackProgress?.let { formatMillisToHMS(it.toLong()) }
    }

    private fun favoriteTrack() {
        lifecycleScope.launch {
            viewState.fetchFavorites().let { list ->
                tracksInteractor.queryTrack(prefs.currentTrackId ?: 0L) {
                    if (list?.contains(it) == true) {
                        toast(getString(R.string.remove_favorites))
                        binding.favouriteTrack.updateFavoriteIcon(this@MusicPlayerActivity, false)
                        viewState.removeFavoriteTrack(prefs.currentTrackId ?: 0L)
                    } else {
                        toast(getString(R.string.add_favorites))
                        binding.favouriteTrack.updateFavoriteIcon(this@MusicPlayerActivity, true)
                        tracksInteractor.queryTrack(prefs.currentTrackId ?: 0L) { track ->
                            track?.let { it1 -> viewState.insertFavoriteTrack(it1) }
                        }
                    }
                }
            }
        }
    }

    private fun repeatTrack() {
        when {
            prefs.shuffleTrack == SHUFFLE_TRACK_ON -> {
                prefs.shuffleTrack = SHUFFLE_TRACK_OFF
                binding.shuffleTrack.setUnSelectedTint(context = this)
            }

            prefs.repeatTrack == REPEAT_TRACK_OFF -> {
                prefs.repeatTrack = REPEAT_TRACK_ON
                binding.repeatTrack.setSelectedTint(context = this)
            }

            prefs.repeatTrack == REPEAT_TRACK_ON -> {
                prefs.repeatTrack = REPEAT_TRACK_OFF
                binding.repeatTrack.setUnSelectedTint(context = this)
            }
        }
    }

    private fun shuffleTrack() {
        when {
            prefs.repeatTrack == REPEAT_TRACK_ON -> {
                prefs.repeatTrack = REPEAT_TRACK_OFF
                binding.repeatTrack.setUnSelectedTint(context = this)
            }

            prefs.shuffleTrack == SHUFFLE_TRACK_OFF -> {
                prefs.shuffleTrack = SHUFFLE_TRACK_ON
                binding.shuffleTrack.setSelectedTint(context = this)
            }

            prefs.shuffleTrack == SHUFFLE_TRACK_ON -> {
                prefs.shuffleTrack = SHUFFLE_TRACK_OFF
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}