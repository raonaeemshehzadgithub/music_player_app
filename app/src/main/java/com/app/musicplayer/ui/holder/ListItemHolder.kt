package com.app.musicplayer.ui.holder

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.TrackItemBinding
import com.bumptech.glide.Glide

open class ListItemHolder(protected val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    protected val context get() = itemView.context

    var trackName: String?
        get() = binding.trackName.text.toString()
        set(value) {
            binding.trackName.text = value
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