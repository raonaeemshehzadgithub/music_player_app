package com.app.musicplayer.ui.holder

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.AlbumItemBinding
import com.bumptech.glide.Glide

open class AlbumViewHolder(protected val binding: AlbumItemBinding) :
RecyclerView.ViewHolder(binding.root) {
    protected val context get() = itemView.context

    var albumName: String?
    get() = binding.albumName.text.toString()
    set(value) {
        binding.albumName.text = value
    }

    var tracks_count: String?
    get() = binding.tracksCount.text.toString()
    set(value) {
        binding.tracksCount.text = value
    }

    fun setDefaultImageRes(photoUri: String) {
        Glide.with(context).load(Uri.parse(photoUri)).placeholder(R.drawable.ic_music)
            .into(binding.albumIcon)
    }

    fun setOnItemClick(onItemClickListener: View.OnClickListener) {
        itemView.setOnClickListener(onItemClickListener)
    }
}