package com.app.musicplayer.ui.adapters.holders

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.TrackItemBinding
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.extentions.beGone
import com.bumptech.glide.Glide

open class PlaylistsViewHolder (protected val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(context: Context, playlist: PlaylistEntity) {
        Glide.with(context).load("")
            .placeholder(R.drawable.ic_playlist)
            .into(binding.thumbnail)
        binding.trackName.text = playlist.playlistName
        binding.artistName.text = "10 Songs"
    }
    fun showPlaylistNames() {
        binding.thumbnail.beGone()
        binding.artistName.beGone()
        binding.menuTrack.beGone()
    }
    fun setOnMenuClick(onMenuClickListener: View.OnClickListener) {
        binding.menuTrack.setOnClickListener(onMenuClickListener)
    }
    fun setOnItemClick(onItemClickListener: View.OnClickListener) {
        binding.root.setOnClickListener(onItemClickListener)
    }
}