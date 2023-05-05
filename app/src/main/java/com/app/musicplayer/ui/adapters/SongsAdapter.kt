package com.app.musicplayer.ui.adapters

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.SongItemBinding
import com.app.musicplayer.models.Song
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.interfaces.SongClick
import com.app.musicplayer.ui.viewholders.SongsViewHolder
import com.bumptech.glide.Glide

class SongsAdapter(
    private val context: Context,
    private val dataList: ArrayList<Song>,
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