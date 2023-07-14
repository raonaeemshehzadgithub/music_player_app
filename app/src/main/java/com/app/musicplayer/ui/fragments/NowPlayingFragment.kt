package com.app.musicplayer.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.app.musicplayer.R
import com.app.musicplayer.databinding.FragmentNowPlayingBinding
import com.app.musicplayer.extentions.beGone
import com.app.musicplayer.extentions.beVisible
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.extentions.sendIntent
import com.app.musicplayer.extentions.updatePlayPauseDrawable
import com.app.musicplayer.helpers.PreferenceHelper
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.viewstates.TracksViewState
import com.app.musicplayer.utils.*
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NowPlayingFragment : Fragment() {

    @Inject
    lateinit var tracksInteractor: TracksInteractor

    @Inject
    lateinit var prefs: PreferenceHelper

    lateinit var binding: FragmentNowPlayingBinding
    private val intentNextPrevious = IntentFilter(NEXT_PREVIOUS_ACTION)
    private val intentPlayPause = IntentFilter(PLAY_PAUSE_ACTION)

    var playerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            when (intent?.action) {
                NEXT_PREVIOUS_ACTION -> {
                    if ((intent.getLongExtra(NEXT_PREVIOUS_TRACK_ID, 0L)) != 0L) {
                        updateTrackInfo(intent.getLongExtra(NEXT_PREVIOUS_TRACK_ID, 0L))
                    }
                }

                PLAY_PAUSE_ACTION -> {
                    when (intent.getBooleanExtra(PLAY_PAUSE_ICON, true)) {
                        true -> binding.playPauseCurrent.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_pause
                            )
                        )

                        false -> binding.playPauseCurrent.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_play
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        requireContext().registerReceiver(playerReceiver, intentNextPrevious)
        requireContext().registerReceiver(playerReceiver, intentPlayPause)
        setUpClicks()
        return view
    }

    val viewState: TracksViewState by activityViewModels()

    private fun setUpClicks() {
        binding.root.setOnClickListener {
            startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                putExtra(TRACK_ID, prefs.currentTrackId)
                putExtra(POSITION, prefs.currentTrackPosition)
                putExtra(FROM_MINI_PLAYER, true)
            })
            requireActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
        }
        binding.playPauseCurrent.setOnClickListener { requireContext().sendIntent(PLAYPAUSE) }
        binding.nextTrackCurrent.setOnClickListener { requireContext().sendIntent(NEXT) }
        binding.previousTrackCurrent.setOnClickListener { requireContext().sendIntent(PREVIOUS) }
    }

    private fun updateTrackInfo(currentTrackId: Long) {
        tracksInteractor.queryTrack(currentTrackId) { track ->
            binding.trackNameCurrent.isSelected = true
            binding.trackNameCurrent.text = track?.title ?: ""
            binding.artistNameCurrent.text = track?.artist?.isUnknownString() ?: ""
            Glide.with(requireContext()).load(track?.albumId?.getThumbnailUri() ?: "")
                .placeholder(R.drawable.ic_music).into(binding.trackThumbnail)
        }
    }

    override fun onResume() {
        super.onResume()
//        viewState.queryTrackList { trList ->
//            tracksList = trList as ArrayList<Track>
//        }
        updatePlayPauseDrawable(binding.playPauseCurrent,requireContext())
        if (prefs.currentTrackId != 0L) {
            binding.root.beVisible()
            updateTrackInfo(prefs.currentTrackId ?: 0L)
        } else {
            binding.root.beGone()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(playerReceiver)
    }
}