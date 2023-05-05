package com.app.musicplayer.ui.activities

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.app.musicplayer.R
import com.app.musicplayer.databinding.ActivityMusicPlayerBinding
import com.app.musicplayer.extentions.createWaveform
import com.app.musicplayer.extentions.formatMillisToHMS
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.models.Song
import com.app.musicplayer.utils.Constants
import com.frolo.waveformseekbar.WaveformSeekBar


class MusicPlayerActivity : BaseActivity(), Runnable, WaveformSeekBar.Callback {
    lateinit var binding: ActivityMusicPlayerBinding
    var songList: ArrayList<Song>? = null
    var position: Int? = null
    private var mediaPlayer: MediaPlayer? = null
    var wasPlaying: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpPlayer()
        clickListeners()
    }

    private fun initViews() {
        songList = intent.getSerializableExtra(Constants.SERIALIZED_LIST) as ArrayList<Song>
        position = intent.getIntExtra(Constants.POSITION, 0)
        binding.songName.text = songList!![position!!].title
        binding.artistName.text = songList!![position!!].artist
        binding.totalDuration.text = formatMillisToHMS(songList!![position!!].duration)
    }

    private fun setUpPlayer() {
        binding.waveformSeekBar.setWaveform(createWaveform(), true)

        try {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                clearMediaPlayer()
                binding.waveformSeekBar.setProgressInPercentage(0F)
                wasPlaying = true
//                binding.playPause.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        this@MusicPlayerActivity,
//                        android.R.drawable.ic_media_play
//                    )
//                )
            }
            if (!wasPlaying) {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()
                }
//                fab.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        this@MainActivity,
//                        R.drawable.ic_media_pause
//                    )
//                )
                val descriptor = songList!![position!!].data
                mediaPlayer!!.setDataSource(
                    descriptor
                )
                mediaPlayer!!.prepare()
                mediaPlayer!!.setVolume(0.5f, 0.5f)
                mediaPlayer!!.isLooping = false
                binding.waveformSeekBar.setProgressInPercentage(mediaPlayer!!.duration.toFloat())
                mediaPlayer!!.start()
                Thread(this).start()
            }
            wasPlaying = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer?.setOnCompletionListener {
            binding.playPause.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MusicPlayerActivity,
                    R.drawable.ic_play
                )
            )
        }
    }

    private fun clickListeners() {
        binding.back.setOnClickListener { finish() }
        binding.playPause.setOnClickListener {  }
    }

    override fun run() {
        var currentPosition = mediaPlayer!!.currentPosition
        val total = mediaPlayer!!.duration


        while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            currentPosition = try {
                Thread.sleep(1000)
                mediaPlayer!!.currentPosition
            } catch (e: InterruptedException) {
                return
            } catch (e: java.lang.Exception) {
                return
            }
            binding.waveformSeekBar.setProgressInPercentage(currentPosition.toFloat())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }

    private fun clearMediaPlayer() {
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
        mediaPlayer = null
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onProgressChanged(seekBar: WaveformSeekBar?, percent: Float, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: WaveformSeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: WaveformSeekBar?) {
    }
}