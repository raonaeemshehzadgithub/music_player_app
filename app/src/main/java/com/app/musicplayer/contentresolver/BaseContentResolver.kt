package com.app.musicplayer.contentresolver

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.pushtorefresh.storio3.contentresolver.impl.DefaultStorIOContentResolver
import com.pushtorefresh.storio3.contentresolver.queries.Query
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class BaseContentResolver<ItemType>(@ApplicationContext private val context: Context) {

    private var _filter: String? = null

    //DefaultStorIOContentResolver class is used to interact with content resolver in a reactive/asynchronous way
    private val _ioContentResolver by lazy { DefaultStorIOContentResolver.builder().contentResolver(context.contentResolver).build() }

    private val _query: Query
        get() = Query.builder()
            .uri(uri)
            .columns(*projection)
            .whereArgs(*(selectionArgs ?: arrayOf()))
            .where(if (selection=="") null else selection)
            .sortOrder(if (sortOrder == "") null else sortOrder)
            .build()

    private fun queryCursor() =
        _ioContentResolver
            .get()
            .cursor()
            .withQuery(_query)
            .prepare()
            .executeAsBlocking()

    @SuppressLint("CheckResult")
    fun queryCursor(callback: (Cursor?) -> Unit): Disposable =
        _ioContentResolver
            .get()
            .cursor()
            .withQuery(_query)
            .prepare()
            .asRxSingle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(callback::invoke)

    fun queryItems() = convertCursorToItems(queryCursor())

    fun queryItems(callback:(List<ItemType>)->Unit):Disposable =
        queryCursor { callback.invoke(convertCursorToItems(it)) }

    private fun observeCursor(observer: (Cursor?) -> Unit): Disposable =
        _ioContentResolver
            .get()
            .cursor()
            .withQuery(_query)
            .prepare()
            .asRxFlowable(BackpressureStrategy.LATEST)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer::invoke)

    fun observeItems(observer:(List<ItemType>)->Unit) =
        observeCursor { observer.invoke(convertCursorToItems(it)) }


    private fun convertCursorToItems(cursor:Cursor?):ArrayList<ItemType>{
        val content = ArrayList<ItemType>()
        while (cursor != null && cursor.moveToNext()) {
            content.add(convertCursorToItem(cursor))
        }
        cursor?.close()
        return content
    }


    abstract val uri: Uri
    abstract val selection: String?
    abstract val sortOrder: String?
    abstract val projection: Array<String>
    abstract val selectionArgs: Array<String>?
    abstract fun convertCursorToItem(cursor: Cursor): ItemType
}