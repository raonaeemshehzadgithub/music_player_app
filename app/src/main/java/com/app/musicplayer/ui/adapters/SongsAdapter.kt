package com.app.musicplayer.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.databinding.SongItemBinding
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.interfaces.SongClick
import com.app.musicplayer.ui.viewholders.SongsViewHolder

class SongsAdapter(
    private val context: Context,
    private val dataList: ArrayList<Track>,
    private val songClick: SongClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SongsViewHolder(
            SongItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), context
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SongsViewHolder).onBind(dataList, position, songClick)
    }

    override fun getItemCount(): Int = dataList.size
}