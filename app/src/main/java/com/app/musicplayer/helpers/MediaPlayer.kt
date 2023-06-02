package com.app.musicplayer.helpers

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.app.musicplayer.services.MusicService
import com.app.musicplayer.services.MusicService.Companion.isTrackCompleted
import com.app.musicplayer.utils.COMPLETE_CALLBACK
import java.io.File
import javax.inject.Inject

object MediaPlayer :
    MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,MediaPlayer.OnCompletionListener {
    private var player: MediaPlayer? = null

    private var trackCompleteCallback: (String) -> Unit = {}
    private var playPauseCallback: (Boolean) -> Unit = {}
    private fun initMediaPlayerIfNeeded() {
        if (player != null) {
            return
        }
        player = MediaPlayer()
    }

    fun setupTrack(context: Context, path: String) {
        initMediaPlayerIfNeeded()
        player?.reset() ?: return
        try {
            player?.apply {
                setDataSource(context, Uri.fromFile(File(path)))
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentPosition(): Int? {
        return player?.currentPosition
    }

    fun getTrackDuration(): Int? {
        return player?.duration
    }

    fun playTrack(playPauseCallback: (Boolean) -> Unit) {
        this.playPauseCallback = playPauseCallback
        initMediaPlayerIfNeeded()
        player?.start()
        playPauseCallback(true)
    }

    fun playNextTrack() {
        initMediaPlayerIfNeeded()
        player?.start()
    }

    fun pauseTrack(playPauseCallback: (Boolean) -> Unit) {
        this.playPauseCallback = playPauseCallback
        player?.pause()
        playPauseCallback(false)
    }

    fun isPlaying(): Boolean {
        return try {
            player?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }

    fun seekTo(position: Int) {
        if (isPlaying()) {
            player?.seekTo(position)
        }
    }

    fun completePlayer(trackCompleteCallback: (String) -> Unit) {
        this.trackCompleteCallback = trackCompleteCallback
        player?.setOnCompletionListener {
//            trackCompleteCallback(COMPLETE_CALLBACK)
        }
    }

    fun releasePlayer() {
        player?.stop()
        player?.release()
        player = null
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        player?.reset()
        return false
    }

    override fun onSeekComplete(p0: MediaPlayer?) {

    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        isTrackCompleted = true
    }

}