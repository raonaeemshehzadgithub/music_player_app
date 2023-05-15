package com.app.musicplayer.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.app.musicplayer.databinding.ActivityMusicPlayerBinding
import com.app.musicplayer.extentions.*
import com.app.musicplayer.models.Events
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService
import com.app.musicplayer.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MusicPlayerActivity : BaseActivity() {
    lateinit var binding: ActivityMusicPlayerBinding
    var track: Track? = null
    private var bus: EventBus? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        clickListeners()

        Intent(this, MusicService::class.java).apply {
            putExtra(TRACK, Gson().toJson(track))
            action = INIT
            try {
                startService(this)
            } catch (e: Exception) {
            }
        }

    }

    private fun initViews() {
        bus = EventBus.getDefault()
        bus!!.register(this)
        val trackType = object : TypeToken<Track>() {}.type
        track = Gson().fromJson<Track>(intent.getStringExtra(TRACK), trackType)
            ?: MusicService.mCurrTrack
        binding.songName.text = track?.title
        binding.artistName.text = track?.artist
        binding.totalDuration.text = formatMillisToHMS(track?.duration!!)
        setUpSeekbar()
    }

    private fun setUpSeekbar() {
        binding.seekBar.max = (track!!.duration / 1000).toInt()
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                val formattedProgress = progress.getFormattedDuration()
                binding.playedDuration.text = formattedProgress
            }

        })
    }

    private fun clickListeners() {
        binding.back.setOnClickListener { finish() }
        binding.playPause.setOnClickListener { sendIntent(PLAYPAUSE) }
        binding.nextSong.setOnClickListener { sendIntent(NEXT) }
        binding.previousSong.setOnClickListener { sendIntent(PREVIOUS) }
    }

    override fun onDestroy() {
        super.onDestroy()
        bus?.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun progressUpdated(event: Events.ProgressUpdated) {
        binding.seekBar.progress = event.progress
        Log.wtf("audio progress", event.progress.getFormattedDuration(true))
    }
}