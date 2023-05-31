package com.app.musicplayer.ui.holder

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.SongItemBinding
import com.bumptech.glide.Glide

open class ListItemHolder(protected val binding: SongItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    protected val context get() = itemView.context

    var songName: String?
        get() = binding.songName.text.toString()
        set(value) {
            binding.songName.text = value
        }

    var artist: String?
        get() = binding.artistName.text.toString()
        set(value) {
            binding.artistName.text = value
        }

    fun setDefaultImageRes(photoUri: String) {
        Glide.with(context).load(Uri.parse(photoUri)).placeholder(R.drawable.ic_music)
            .into(binding.thumbnail)
    }
    fun setOnItemClick(onItemClickListener: View.OnClickListener) {
        itemView.setOnClickListener(onItemClickListener)
    }
}