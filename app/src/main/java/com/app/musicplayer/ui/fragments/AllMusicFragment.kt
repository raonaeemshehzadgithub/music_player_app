package com.app.musicplayer.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.databinding.FragmentAllMusicBinding
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.adapters.SongsAdapter
import com.app.musicplayer.ui.interfaces.SongClick
import com.app.musicplayer.helpers.SongsHelper
import com.app.musicplayer.utils.*
import com.google.gson.Gson

class AllMusicFragment : Fragment(), SongClick {

    lateinit var binding: FragmentAllMusicBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllMusicBinding.inflate(layoutInflater)

        showAllSongs()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAllSongs() {
        val songHelper = SongsHelper()
        val linearLayout = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        val adapter =
            SongsAdapter(requireContext(), songHelper.retrieveAllSongs(requireContext()), this)
        binding.playlistRv.layoutManager = linearLayout
        binding.playlistRv.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onSongClick(track: Track) {
        Intent(requireContext(), MusicPlayerActivity::class.java).apply {
            putExtra(TRACK, Gson().toJson(track))
            putExtra(RESTART_PLAYER, true)
            startActivity(this)
        }
    }

}