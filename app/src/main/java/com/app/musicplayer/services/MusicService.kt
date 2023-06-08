package com.app.musicplayer.services

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media.session.MediaButtonReceiver
import com.app.musicplayer.R
import com.app.musicplayer.extentions.getColoredBitmap
import com.app.musicplayer.extentions.hasPermission
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.extentions.shuffleTrack
import com.app.musicplayer.helpers.MediaPlayer
//import com.app.musicplayer.helpers.MediaPlayer.completePlayer
//import com.app.musicplayer.helpers.MediaPlayer.getCurrentPosition
//import com.app.musicplayer.helpers.MediaPlayer.getTrackDuration
//import com.app.musicplayer.helpers.MediaPlayer.isPlaying
//import com.app.musicplayer.helpers.MediaPlayer.pauseTrack
//import com.app.musicplayer.helpers.MediaPlayer.playTrack
//import com.app.musicplayer.helpers.MediaPlayer.releasePlayer
//import com.app.musicplayer.helpers.MediaPlayer.seekTo
//import com.app.musicplayer.helpers.MediaPlayer.setupTrack
import com.app.musicplayer.helpers.MediaSessionCallback
import com.app.musicplayer.helpers.NotificationHelper
import com.app.musicplayer.helpers.NotificationHelper.Companion.NOTIFICATION_ID
import com.app.musicplayer.helpers.PreferenceHelper
import com.app.musicplayer.interator.tracks.TracksInteractor
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
    lateinit var tracksInteractor: TracksInteractor

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var pref: PreferenceHelper

    var timer: ScheduledExecutorService? = null
    val intentControl = Intent(PROGRESS_CONTROLS_ACTION)
    private var mProgressHandler = Handler()

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL = 1000L
        private var mPlaybackSpeed = 1f
        var mCurrTrack: Track? = null
        private var mCurrTrackCover: Bitmap? = null
        private var mMediaSession: MediaSessionCompat? = null
        var tracksList = ArrayList<Track>()
        var positionTrack: Int = 0
        var isTrackCompleted: Boolean = false
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
            NEXT -> handleNextPrevious(isNext = true, isShuffle = SHUFFLE_TRACK_OFF)
            PREVIOUS -> handleNextPrevious(isNext = false, isShuffle = SHUFFLE_TRACK_OFF)
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
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.releasePlayer()
        }
        stopForegroundAndNotification()
        val dismissIntent = Intent(DISMISS_PLAYER_ACTION)
        dismissIntent.putExtra(DISMISS_PLAYER, true)
        sendBroadcast(dismissIntent)
    }

    private fun handleSetProgress(intent: Intent) {
        mediaPlayer.seekTo(intent.getIntExtra(PROGRESS, mediaPlayer.getCurrentPosition()!!))
    }

    private fun handleNextPrevious(isNext: Boolean, isShuffle: String) {
        if (isNext && positionTrack != tracksList.size.minus(1)) {
            positionTrack++
        } else if (!isNext && positionTrack != 0) {
            positionTrack--
        } else if (isShuffle == SHUFFLE_TRACK_ON) {
            positionTrack = tracksList.size.shuffleTrack()
        }
        currentTrackId = tracksList[positionTrack].id
        val nextPreviousIntent = Intent(NEXT_PREVIOUS_ACTION)
        nextPreviousIntent.putExtra(NEXT_PREVIOUS_TRACK_ID, currentTrackId)
        sendBroadcast(nextPreviousIntent)
        mediaPlayer.setupTrack(applicationContext, tracksList[positionTrack].path ?: "")
        handleProgressHandler(mediaPlayer.isPlaying())
    }

    private fun handlePlayPause() {
        if (mediaPlayer.isPlaying()) {
            pauseTrack()
        } else {
            resumeTrack()
        }
    }

    fun pauseTrack() {
        mediaPlayer.pauseTrack {
            if (!it) {
                val playPauseIntent = Intent(PLAY_PAUSE_ACTION)
                playPauseIntent.putExtra(PLAY_PAUSE, false)
                sendBroadcast(playPauseIntent)
            }
        }
    }

    fun resumeTrack() {
        mediaPlayer.playTrack {
            if (it) {
                val playPauseIntent = Intent(PLAY_PAUSE_ACTION)
                playPauseIntent.putExtra(PLAY_PAUSE, true)
                sendBroadcast(playPauseIntent)
            }
        }
    }

    private fun handleInit(intent: Intent) {
        positionTrack = intent.getIntExtra(POSITION, 0)
        currentTrackId = tracksList[positionTrack].id
        mediaPlayer.setupTrack(applicationContext, tracksList[positionTrack].path ?: "")
        handleProgressHandler(mediaPlayer.isPlaying())
        mediaPlayer.completePlayer { completed ->
            if (completed == COMPLETE_CALLBACK) {
                if (pref.repeatTrack == REPEAT_TRACK_ON) {
                    mediaPlayer.setupTrack(applicationContext, tracksList[positionTrack].path ?: "")
                } else if (pref.shuffleTrack == SHUFFLE_TRACK_ON) {
                    handleNextPrevious(isNext = false, isShuffle = SHUFFLE_TRACK_ON)
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startForegroundAndNotify() {
        notificationHandler.removeCallbacksAndMessages(null)
        notificationHandler.postDelayed({
            mCurrTrackCover = resources.getColoredBitmap(R.drawable.ic_music, R.color.purple)
            tracksInteractor.queryTrack(currentTrackId) { track ->
                notificationHelper?.createPlayerNotification(
                    trackTitle = track?.title ?: "Track",
                    trackArtist = track?.artist?.isUnknownString() ?: "Artist",
                    isPlaying = mediaPlayer.isPlaying(),
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
                if (mediaPlayer.isPlaying()) {
                    val position = mediaPlayer.getCurrentPosition()
                    val duration = mediaPlayer.getTrackDuration()
                    intentControl.putExtra(GET_TRACK_DURATION, duration)
                    intentControl.putExtra(GET_CURRENT_POSITION, position)
                    sendBroadcast(intentControl)
                }
            }, 10, 10, TimeUnit.MILLISECONDS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaSession?.isActive = false
        mMediaSession = null
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