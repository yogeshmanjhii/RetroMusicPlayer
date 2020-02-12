/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package code.name.monkey.retromusic.loaders

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import code.name.monkey.retromusic.Constants.baseProjection
import code.name.monkey.retromusic.Constants.baseSelection
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.util.PreferenceUtil
import java.util.ArrayList

/**
 * Created by hemanths on 10/08/17.
 */

object SongLoader {


    fun getAllSongs(
        context: Context
    ): ArrayList<Song> {
        val cursor = makeSongCursor(context, null, null)
        return getSongs(cursor)
    }

    fun getSongs(
        cursor: Cursor?
    ): ArrayList<Song> {
        val songs = arrayListOf<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursorImpl(cursor))
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return songs
    }

    fun getSongs(
        context: Context,
        query: String
    ): ArrayList<Song> {
        val cursor = makeSongCursor(context, AudioColumns.TITLE + " LIKE ?", arrayOf("%$query%"))
        return getSongs(cursor)
    }

    private fun getSong(
        cursor: Cursor?
    ): Song {
        val song: Song
        if (cursor != null && cursor.moveToFirst()) {
            song = getSongFromCursorImpl(cursor)
        } else {
            song = Song.emptySong
        }
        cursor?.close()
        return song
    }

    @JvmStatic
    fun getSong(context: Context, queryId: Long): Song {
        val cursor = makeSongCursor(context, AudioColumns._ID + "=?", arrayOf(queryId.toString()))
        return getSong(cursor)
    }

    private fun getSongFromCursorImpl(
        cursor: Cursor
    ): Song = Song.fromCursor(cursor)

    @JvmStatic
    @JvmOverloads
    fun makeSongCursor(
        context: Context,
        selectionString: String?,
        selectionValuesArray: Array<String>?,
        sortOrder: String = PreferenceUtil.getInstance(context).songSortOrder
    ): Cursor {
        var selectionValues: Array<String>? = arrayOf()
        var selection = if (selectionString != null && selectionString.trim() != "") {
            "$baseSelection AND $selectionString"
        } else {
            baseSelection
        }

        // Blacklist
        /*val paths = BlacklistStore.getInstance(context).paths
        if (paths.isNotEmpty()) {
            selection = generateBlacklistSelection(selection, paths.size)
            selectionValues = addBlacklistSelectionValues(selectionValuesArray, paths)
        }*/
        if (PreferenceUtil.getInstance(context).filterLength != 0) {
            selection =
                "$selection AND ${MediaStore.Audio.Media.DURATION} >= ${PreferenceUtil.getInstance(context).filterLength * 1000}"
        }

        return context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            baseProjection,
            selection,
            selectionValuesArray,
            sortOrder
        )
            ?: throw IllegalStateException("Unable to query ${MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}, system returned null.")
    }
}

fun generateBlacklistSelection(
    selection: String?,
    pathCount: Int
): String {
    val newSelection = StringBuilder(
        if (selection != null && selection.trim { it <= ' ' } != "") "$selection AND " else "")
    newSelection.append(AudioColumns.DATA + " NOT LIKE ?")
    for (i in 0 until pathCount - 1) {
        newSelection.append(" AND " + AudioColumns.DATA + " NOT LIKE ?")
    }
    return newSelection.toString()
}

fun addBlacklistSelectionValues(
    selectionValues: Array<String>?,
    paths: ArrayList<String>
): Array<String>? {
    var selectionValuesFinal = selectionValues
    if (selectionValuesFinal == null) {
        selectionValuesFinal = emptyArray()
    }
    val newSelectionValues = Array(selectionValuesFinal.size + paths.size) {
        "n = $it"
    }
    System.arraycopy(selectionValuesFinal, 0, newSelectionValues, 0, selectionValuesFinal.size)
    for (i in selectionValuesFinal.size until newSelectionValues.size) {
        newSelectionValues[i] = paths[i - selectionValuesFinal.size] + "%"
    }
    return newSelectionValues
}