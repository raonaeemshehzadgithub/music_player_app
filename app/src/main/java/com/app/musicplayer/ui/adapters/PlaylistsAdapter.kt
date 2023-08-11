package com.app.musicplayer.ui.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.models.ListData
import com.app.musicplayer.ui.adapters.holders.PlaylistsViewHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlaylistsAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    ListAdapter<PlaylistEntity>() {
    var isListOnly: Boolean? = false
    override fun onBindListItem(holder: RecyclerView.ViewHolder, item: PlaylistEntity) {
        (holder as PlaylistsViewHolder).apply {
            bindData(context, item)
            if (isListOnly == true) {
                showPlaylistNames()
            }
        }
    }

    override fun convertDataToListData(items: List<PlaylistEntity>) =
        ListData.fromPlaylists(items)

}