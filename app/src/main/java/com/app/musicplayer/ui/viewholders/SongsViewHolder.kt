package com.app.musicplayer.ui.viewholders

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.SongItemBinding
import com.app.musicplayer.models.Song
import com.app.musicplayer.ui.interfaces.SongClick
import com.bumptech.glide.Glide

class SongsViewHolder(val binding: SongItemBinding, val context: Context) :
    RecyclerView.ViewHolder(binding.root) {
    private var click: SongClick? = null

    fun onBind(
        songsList: ArrayList<Song>,
        position: Int,
        songClick: SongClick
    ) {
        this.click = songClick
        Glide.with(context).load(songsList[position].thumbnail).error(R.mipmap.ic_launcher_app)
            .into(binding.thumbnail)
        binding.songName.text = songsList[position].title
        binding.artistName.text = songsList[position].artist

        binding.root.setOnClickListener {
            click?.onSongClick(
                position,
                songsList[position].data,
                songsList
            )
        }
    }
}