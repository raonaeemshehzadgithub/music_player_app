package com.app.musicplayer.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.musicplayer.databinding.ActivityMusicPlayerBinding
import com.app.musicplayer.extentions.createWaveform

class MusicPlayerActivity : AppCompatActivity() {
    lateinit var binding: ActivityMusicPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpPlayer()
        clickListeners()
    }

    private fun setUpPlayer() {
        binding.waveformSeekBar.setWaveform(createWaveform(), true)
    }

    private fun clickListeners() {
        binding.back.setOnClickListener { finish() }
    }

}