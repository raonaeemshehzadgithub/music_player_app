package com.app.musicplayer.services

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.media.session.MediaButtonReceiver
import com.app.musicplayer.R
import com.app.musicplayer.extentions.getColoredBitmap
import com.app.musicplayer.extentions.hasPermission
import com.app.musicplayer.helpers.MediaPlayer.completePlayer
import com.app.musicplayer.helpers.MediaPlayer.getCurrentPosition
import com.app.musicplayer.helpers.MediaPlayer.getTrackDuration
import com.app.musicplayer.helpers.MediaPlayer.isPlaying
import com.app.musicplayer.helpers.MediaPlayer.pauseTrack
import com.app.musicplayer.helpers.MediaPlayer.playTrack
import com.app.musicplayer.helpers.MediaPlayer.releasePlayer
import com.app.musicplayer.helpers.MediaPlayer.seekTo
import com.app.musicplayer.helpers.MediaPlayer.setupTrack
import com.app.musicplayer.helpers.MediaSessionCallback
import com.app.musicplayer.helpers.NotificationHelper
import com.app.musicplayer.helpers.NotificationHelper.Companion.NOTIFICATION_ID
import com.app.musicplayer.interator.songs.SongsInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.utils.*
import com.app.musicplayer.utils.getPermissionToRequest
import com.app.musicplayer.utils.isQPlus
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {

    @Inject
    lateinit var songsInteractor: SongsInteractor

    var timer: ScheduledExecutorService? = null
    val intentControl = Intent(PROGRESS_CONTROLS_ACTION)
    private var mProgressHandler = Handler()
    private var positionTrack: Int = 0

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL = 1000L
        private var mPlaybackSpeed = 1f
        var mCurrTrack: Track? = null
        private var mCurrTrackCover: Bitmap? = null
        private var mMediaSession: MediaSessionCompat? = null
        var songsList = ArrayList<Track>()
    }

    private var currentTrackId: Long = 0L
    private val notificationHandler = Handler()
    private var notificationHelper: NotificationHelper? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        createMediaSession()

        notificationHelper = NotificationHelper.createInstance(context = this, mMediaSession!!)
        startForegroundAndNotify()
    }

    private fun createMediaSession() {
        mMediaSession = MediaSessionCompat(this, "MusicService")
        mMediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
        val mediaSessionCallback = MediaSessionCallback(this)
        mMediaSession!!.setCallback(mediaSessionCallback)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isQPlus() && !hasPermission(getPermissionToRequest())) {
            return START_NOT_STICKY
        }

        val action = intent?.action
        when (action) {
            INIT -> handleInit(intent)
            PLAYPAUSE -> handlePlayPause()
            NEXT -> handleNextPrevious(true)
            PREVIOUS -> handleNextPrevious(false)
            SET_PROGRESS -> handleSetProgress(intent)
            DISMISS -> dismissNotification()
        }
        MediaButtonReceiver.handleIntent(mMediaSession!!, intent)
        if (action != DISMISS && action != FINISH) {
            startForegroundAndNotify()
        }
        return START_NOT_STICKY
    }

    private fun dismissNotification() {
        if (isPlaying()) {
            releasePlayer()
        }
        stopForegroundAndNotification()
        val dismissIntent = Intent(DISMISS_PLAYER_ACTION)
        dismissIntent.putExtra(DISMISS_PLAYER, true)
        sendBroadcast(dismissIntent)
    }

    private fun handleSetProgress(intent: Intent) {
        seekTo(intent.getIntExtra(PROGRESS, getCurrentPosition()!!))
    }

    private fun handleNextPrevious(isNext: Boolean) {
        if (isNext && positionTrack != songsList.size.minus(1)) {
            positionTrack++
        } else if (!isNext && positionTrack != 0) {
            positionTrack--
        }
        currentTrackId = songsList[positionTrack].id
        val nextPreviousIntent = Intent(NEXT_PREVIOUS_ACTION)
        nextPreviousIntent.putExtra(NEXT_PREVIOUS_TRACK_ID, currentTrackId)
        sendBroadcast(nextPreviousIntent)
        setupTrack(applicationContext, songsList[positionTrack].path)
        handleProgressHandler(isPlaying())
    }

    private fun handlePlayPause() {
        val playPauseIntent = Intent(PLAY_PAUSE_ACTION)
        if (isPlaying()) {
            pauseTrack {
                if (!it) {
                    playPauseIntent.putExtra(PLAY_PAUSE, false)
                }
            }
        } else {
            playTrack {
                if (it) {
                    intentControl.putExtra(PLAY_PAUSE, true)
                }
            }
        }
        sendBroadcast(playPauseIntent)
    }

    private fun handleInit(intent: Intent) {
        positionTrack = intent.getIntExtra(POSITION, 0)
        setupTrack(applicationContext, songsList[positionTrack].path)
        handleProgressHandler(isPlaying())
        completePlayer { COMPLETE ->
//            handleNextPrevious(true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startForegroundAndNotify() {
        notificationHandler.removeCallbacksAndMessages(null)
        notificationHandler.postDelayed({
            mCurrTrackCover = resources.getColoredBitmap(R.drawable.ic_music, R.color.purple)
            songsInteractor.querySong(currentTrackId) { track ->
                notificationHelper?.createPlayerNotification(
                    trackTitle = track?.title,
                    trackArtist = track?.artist,
                    isPlaying = isPlaying(),
                    largeIcon = mCurrTrackCover,
                ) {
                    notificationHelper?.notify(NOTIFICATION_ID, it)
                    try {
                        if (isQPlus()) {
                            startForeground(
                                NOTIFICATION_ID,
                                it,
                                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                            )
                        } else {
                            startForeground(NOTIFICATION_ID, it)
                        }
                    } catch (ignored: IllegalStateException) {
                    }
                }
            }
        }, 200L)
    }

    private fun handleProgressHandler(isPlaying: Boolean) {
//        if (isPlaying) {
//            mProgressHandler.post(object : Runnable {
//                override fun run() {
//                    if (isPlaying()) {
//                        val position = getCurrentPosition()
//                        val duration = getTrackDuration()
//                        intentControl.putExtra(GET_TRACK_DURATION, duration)
//                        intentControl.putExtra(GET_CURRENT_POSITION, position)
//                        sendBroadcast(intentControl)
//                    }
//                    mProgressHandler.removeCallbacksAndMessages(null)
//                    mProgressHandler.postDelayed(
//                        this,
//                        (PROGRESS_UPDATE_INTERVAL / mPlaybackSpeed).toLong()
//                    )
//                }
//            })
//        } else {
//            mProgressHandler.removeCallbacksAndMessages(null)
//        }
        if (isPlaying) {
            timer = Executors.newScheduledThreadPool(1)
            timer?.scheduleAtFixedRate({
                if (isPlaying()) {
                    val position = getCurrentPosition()
                    val duration = getTrackDuration()
                    intentControl.putExtra(GET_TRACK_DURATION, duration)
                    intentControl.putExtra(GET_CURRENT_POSITION, position)
                    sendBroadcast(intentControl)
                }
            }, 10, 10, TimeUnit.MILLISECONDS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun stopForegroundAndNotification() {
        notificationHandler.removeCallbacksAndMessages(null)
        stopForeground(true)
        notificationHelper?.cancel(NOTIFICATION_ID)
    }
}