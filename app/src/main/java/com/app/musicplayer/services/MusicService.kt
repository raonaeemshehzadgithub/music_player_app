package com.app.musicplayer.services

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.net.Uri
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
import com.app.musicplayer.helpers.MediaPlayerHolder
import com.app.musicplayer.helpers.MediaSessionCallback
import com.app.musicplayer.helpers.NotificationHelper
import com.app.musicplayer.helpers.NotificationHelper.Companion.NOTIFICATION_ID
import com.app.musicplayer.interator.songs.SongsInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.repository.songs.SongsRepository
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

    private var mediaPlayer: MediaPlayerHolder? = null
    var timer: ScheduledExecutorService? = null
    val intentControl = Intent(CURRENT_POSITION_ACTION)

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL = 1000L
        private var mPlaybackSpeed = 1f
        var mCurrTrack: Track? = null
        private var mCurrTrackCover: Bitmap? = null
        private var mMediaSession: MediaSessionCompat? = null

    }

    private val notificationHandler = Handler()
    private var notificationHelper: NotificationHelper? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        createMediaSession()

        mediaPlayer = MediaPlayerHolder(this)
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
        songsInteractor.querySong(intent!!.getLongExtra(TRACK_ID_SERVICE, 0L)) { track ->
            mCurrTrack = track
        }
        if (!isQPlus() && !hasPermission(getPermissionToRequest())) {
            return START_NOT_STICKY
        }

        val action = intent.action
        when (action) {
            INIT -> handleInit(intent)
            PLAYPAUSE -> handlePlayPause()
            NEXT -> handleNext()
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
        if (mediaPlayer?.isPlaying() == true) {
            mediaPlayer?.pauseTrack{
                if (!it) {
                    intentControl.putExtra(PLAY_PAUSE, false)
                    sendBroadcast(intentControl)
                }
            }
        }
        stopForegroundAndNotification()
    }

    private fun handleSetProgress(intent: Intent) {
        mediaPlayer?.seekTo(intent.getIntExtra(PROGRESS, mediaPlayer?.getCurrentPosition()!!))
//        mediaPlayer?.playTrack()
    }

    private fun handleNext() {
    }

    private fun handlePlayPause() {
        if (mediaPlayer!!.isPlaying()) {
            mediaPlayer?.pauseTrack {
                if (!it) {
                    intentControl.putExtra(PLAY_PAUSE, false)
                }
            }
        } else {
            mediaPlayer?.playTrack {
                if (it) {
                    intentControl.putExtra(PLAY_PAUSE, true)
                }
            }
        }
        sendBroadcast(intentControl)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun trackStateChanged(isPlaying: Boolean, notify: Boolean) {
        handleProgressHandler(isPlaying)
        if (notify) {
            startForegroundAndNotify()
        }
    }

    private fun handleInit(intent: Intent) {
        songsInteractor.querySong(intent.getLongExtra(TRACK_ID_SERVICE, 0L)) { track ->
            mediaPlayer?.setupTrack(track)
            handleProgressHandler(mediaPlayer!!.isPlaying())
            mediaPlayer?.completePlayer { COMPLETE ->
                mediaPlayer?.releasePlayer()
                intentControl.putExtra(COMPLETE, COMPLETE)
                sendBroadcast(intentControl)
                stopForeground(true)
                stopSelf()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startForegroundAndNotify() {
        notificationHandler.removeCallbacksAndMessages(null)
        notificationHandler.postDelayed({
            if (mCurrTrackCover?.isRecycled == true) {
                mCurrTrackCover = resources.getColoredBitmap(R.drawable.ic_music, R.color.purple)
            }

            //create notification
            mCurrTrack?.let {
                notificationHelper?.createPlayerNotification(
                    track = it,
                    isPlaying = mediaPlayer!!.isPlaying(),
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

        if (isPlaying) {
            timer = Executors.newScheduledThreadPool(1)
            timer?.scheduleAtFixedRate({
                if (mediaPlayer!!.isPlaying()) {
                    val position = mediaPlayer!!.getCurrentPosition()
                    val duration = mediaPlayer?.getTrackDuration()
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