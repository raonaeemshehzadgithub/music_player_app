package com.app.musicplayer.ui.views

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.musicplayer.R
import com.app.musicplayer.databinding.ViewCurrentTrackBarBinding
import com.app.musicplayer.extentions.fadeIn
import com.app.musicplayer.extentions.fadeOut
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.extentions.sendIntent
import com.app.musicplayer.models.Track
import com.app.musicplayer.utils.NEXT
import com.app.musicplayer.utils.PLAYPAUSE
import com.app.musicplayer.utils.PREVIOUS
import com.bumptech.glide.Glide

class CurrentTrackBar(context: Context, attributeSet:AttributeSet):RelativeLayout(context,attributeSet) {
//    private lateinit var binding:ViewCurrentTrackBarBinding
//    init {
//        inflateLayout()
//        initTexts()
//    }
//    private fun inflateLayout() {
//        binding = ViewCurrentTrackBarBinding.inflate(LayoutInflater.from(context), this, true)
//    }
//
//    fun initTexts() {
//        binding.trackNameCurrent.text = "Waleed"

        // Use imageView and textView as needed
//    }
//    val binding=CurrentTrackBarBinding.inflate(LayoutInflater.from(context), null,false)
//    fun updateColors() {
//        background = ColorDrawable(context.getProperBackgroundColor())
//        binding.previousTrackCurrent.setOnClickListener {
//            context.sendIntent(PREVIOUS)
//        }
//        binding.playPauseCurrent.setOnClickListener {
//            context.sendIntent(PLAYPAUSE)
//        }
//        binding.nextTrackCurrent.setOnClickListener {
//            context.sendIntent(NEXT)
//        }
//    }
//    fun updateCurrentTrack(track: Track?) {
//        if (track == null) {
//            fadeOut()
//            return
//        } else {
//            fadeIn()
//        }
//
//        binding.trackNameCurrent.text = track.title
//        binding.artistNameCurrent.text = track.artist
//        Glide.with(context).load(track.album_id?.getThumbnailUri())
//            .placeholder(R.drawable.ic_music).into(binding.trackThumbnail)
//    }
//
//    fun updateTrackState(isPlaying: Boolean) {
//        current_track_play_pause.updatePlayPauseIcon(isPlaying, context.getProperTextColor())
//    }
}