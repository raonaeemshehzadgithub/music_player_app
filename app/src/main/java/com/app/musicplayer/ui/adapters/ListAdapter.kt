package com.app.musicplayer.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.databinding.SongItemBinding
import com.app.musicplayer.models.ListData
import com.app.musicplayer.ui.holder.ListItemHolder

abstract class ListAdapter<ItemType> : RecyclerView.Adapter<ListItemHolder>() {

    private var _data: ListData<ItemType> = ListData()
    private var _onItemClickListener: (ItemType, Int) -> Unit = { _, _ -> }

    var items: List<ItemType>
        get() = _data.items
        set(value) {
            _data = convertDataToListData(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ListItemHolder(
        SongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
        val dataItem = getItem(position)
        holder.apply {
            setOnItemClick {
                _onItemClickListener.invoke(dataItem, position)
            }
            onBindListItem(this, dataItem)
        }
    }

    override fun getItemCount(): Int = _data.items.size

    private fun getItem(position: Int) = _data.items[position]

    open fun convertDataToListData(items: List<ItemType>) = ListData(items)

    abstract fun onBindListItem(listItemHolder: ListItemHolder, item: ItemType)

    fun setOnItemClickListener(onItemClickListener: (ItemType, Int) -> Unit) {
        _onItemClickListener = onItemClickListener
    }
}