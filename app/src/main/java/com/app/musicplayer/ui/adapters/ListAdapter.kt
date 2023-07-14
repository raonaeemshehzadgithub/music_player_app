package com.app.musicplayer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.databinding.ListItemBinding
import com.app.musicplayer.models.ListData
import com.app.musicplayer.ui.holder.ListItemHolder

abstract class ListAdapter<ItemType> : RecyclerView.Adapter<ListItemHolder>() {

    private var _data: ListData<ItemType> = ListData()
    private var _onItemClickListener: (ItemType, Int) -> Unit = { _, _ -> }
    private var _onItemMenuClickListener: (ItemType,Int,View) -> Unit = {_,_,_->}
    private var _onItemFavoriteClickListener: (ItemType) -> Unit = {}

    var items: List<ItemType>
        get() = _data.items
        set(value) {
            _data = convertDataToListData(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ListItemHolder(
        ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
        val dataItem = getItem(position)
        holder.apply {
            setOnItemClick {
                _onItemClickListener.invoke(dataItem, position)
            }
            setOnTrackMenuClick{
                _onItemMenuClickListener.invoke(dataItem,position,it)
            }
            setOnTrackFavoriteClick{
                _onItemFavoriteClickListener.invoke(dataItem)
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
    fun setOnMenuClickListener(onItemMenuClickListener:(ItemType,Int,View)->Unit){
        _onItemMenuClickListener = onItemMenuClickListener
    }
    fun setOnFavoriteClickListener(onItemFavoriteClickListener:(ItemType)->Unit){
        _onItemFavoriteClickListener = onItemFavoriteClickListener
    }
}