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
import com.app.musicplayer.models.Song
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.bumptech.glide.Glide

class SongsAdapter(
    private val context: Context,
    private val dataList: ArrayList<Song>
) : RecyclerView.Adapter<SongsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songName.text = dataList[position].title
        holder.artistName.text = dataList[position].artist
        Glide.with(context).load(dataList[position].thumbnail).error(R.mipmap.ic_launcher_app)
            .into(holder.thumbnail)

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, MusicPlayerActivity::class.java))
        }
    }

    override fun getItemCount(): Int = dataList.size

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val songName: TextView = itemView.findViewById(R.id.song_name)
        val artistName: TextView = itemView.findViewById(R.id.artist_name)
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
    }
}