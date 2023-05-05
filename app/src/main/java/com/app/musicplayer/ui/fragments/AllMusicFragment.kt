package com.app.musicplayer.ui.fragments

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.databinding.FragmentAllMusicBinding
import com.app.musicplayer.models.Playlist
import com.app.musicplayer.models.Song
import com.app.musicplayer.ui.adapters.SongsAdapter
import com.app.musicplayer.utils.SongsHelper

class AllMusicFragment : Fragment() {

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
        val adapter = SongsAdapter(requireContext(), songHelper.retrieveAllSongs(requireContext()))
        binding.playlistRv.layoutManager = linearLayout
        binding.playlistRv.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}