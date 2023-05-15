package com.app.musicplayer.services

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.media.session.MediaButtonReceiver
import com.app.musicplayer.R
import com.app.musicplayer.extentions.getColoredBitmap
import com.app.musicplayer.extentions.hasPermission
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.helpers.MediaSessionCallback
import com.app.musicplayer.helpers.NotificationHelper
import com.app.musicplayer.helpers.NotificationHelper.Companion.NOTIFICATION_ID
import com.app.musicplayer.models.Events
import com.app.musicplayer.models.Track
import com.app.musicplayer.utils.*
import com.app.musicplayer.utils.getPermissionToRequest
import com.app.musicplayer.utils.isQPlus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import java.io.File

class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener, OnAudioFocusChangeListener {

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL = 1000L
        private var mPlaybackSpeed = 1f
        var mCurrTrack: Track? = null
        private var mCurrTrackCover: Bitmap? = null
        private var mIntentUri: Uri? = null
        var mPlayer: MediaPlayer? = null
        private var mProgressHandler = Handler()
        private var mMediaSession: MediaSessionCompat? = null

        private var mPlayOnPrepare = true
        fun isPlaying(): Boolean {
            return try {
                mPlayer?.isPlaying == true
            } catch (e: java.lang.Exception) {
                false
            }
        }
    }

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
        val trackType = object : TypeToken<Track>() {}.type
        mCurrTrack = Gson().fromJson<Track>(intent?.getStringExtra(TRACK), trackType)
        if (!isQPlus() && !hasPermission(getPermissionToRequest())) {
            return START_NOT_STICKY
        }

        val action = intent?.action
        when (action) {
            INIT -> handleInit(intent)
            INIT_PATH -> handleInitPath(intent)
            BROADCAST_STATUS -> broadcastPlayerStatus()
            PLAYPAUSE -> handlePlayPause()
            NEXT -> handleNext()
            PREVIOUS -> handlePrevious()
        }
        MediaButtonReceiver.handleIntent(mMediaSession!!, intent)
        if (action != DISMISS && action != FINISH) {
            startForegroundAndNotify()
        }
        return START_NOT_STICKY
    }

    private fun handlePrevious() {
    }

    private fun handleNext() {
        mPlayOnPrepare = true
        setupNextTrack()
    }

    private fun setupNextTrack() {
    }

    private fun handlePlayPause() {
        mPlayOnPrepare = true
        if (isPlaying()) {
            pauseTrack()
        } else {
            resumeTrack()
        }
    }

    fun resumeTrack() {
        initMediaPlayerIfNeeded()

        if (mCurrTrack == null) {
//            setupNextTrack()
        } else {
            mPlayer!!.start()
        }

//        trackStateChanged(true)
//        setPlaybackSpeed()
    }

    fun pauseTrack() {
        initMediaPlayerIfNeeded()
        mPlayer!!.pause()
//        trackStateChanged(false, notify = notify)
//        updateMediaSessionState()
//        saveTrackProgress()
        // do not call stopForeground on android 12 as it may cause a crash later
        if (!isSPlus()) {
            @Suppress("DEPRECATION")
            stopForeground(false)
        }
    }

    private fun handleInit(intent: Intent) {
        ensureBackgroundThread {
            initService(intent)
            setupTrack()
            updateUI()
        }
    }

    private fun initService(intent: Intent) {
//        toast(intent.data.toString())
    }

    private fun handleInitPath(intent: Intent) {
//        mIsThirdPartyIntent = true
        if (mIntentUri != intent.data) {
            mIntentUri = intent.data
//            initService(intent)
            initTracks()
        }
        //        else {
//            updateUI()
//        }
    }

    private fun broadcastPlayerStatus() {

    }

    private fun broadcastTrackProgress(progress: Int) {
        EventBus.getDefault().post(Events.ProgressUpdated(progress))
        //also update media session state
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
        if (isPlaying) {
            mProgressHandler.post(object : Runnable {
                override fun run() {
                    if (mPlayer?.isPlaying == true) {
                        val sec = getPosition()!!/1000
                        broadcastTrackProgress(sec)
                    }
                    mProgressHandler.removeCallbacksAndMessages(true)
                    mProgressHandler.postDelayed(
                        this,
                        (PROGRESS_UPDATE_INTERVAL / mPlaybackSpeed).toLong()
                    )
                }

            })
        } else {
            mProgressHandler.removeCallbacksAndMessages(true)
        }

    }

    private fun getPosition(): Int? {
        return mPlayer?.currentPosition
    }

    override fun onBind(intent: Intent?): IBinder? = null
    private fun setupTrack() {
        initMediaPlayerIfNeeded()

        try {
            mPlayer!!.apply {
                reset()
                setDataSource(applicationContext, Uri.fromFile(File(mCurrTrack?.data!!)))
                prepare()
                start()
            }
        } catch (ignored: Exception) {
        }
    }

    private fun initMediaPlayerIfNeeded() {
        if (mPlayer != null) {
            return
        }

        mPlayer = MediaPlayer().apply { isLooping }
    }

    private fun initTracks() {
        setupTrack()
    }

    private fun updateUI() {
        ensureBackgroundThread {
            if (mPlayer != null) {
                handleProgressHandler(isPlaying())
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        mPlayer!!.reset()
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
    }

    override fun onAudioFocusChange(p0: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
        mMediaSession?.isActive = false
        mMediaSession = null
    }

    private fun destroyPlayer() {
        mPlayer?.stop()
        mPlayer?.release()
        mPlayer = null

//        trackStateChanged(isPlaying = false, notify = false)
//        trackChanged()

        stopForegroundAndNotification()
        stopSelf()
    }

    private fun stopForegroundAndNotification() {
        notificationHandler.removeCallbacksAndMessages(null)
        stopForeground(true)
        notificationHelper?.cancel(NOTIFICATION_ID)
    }
//        updateUI()
}