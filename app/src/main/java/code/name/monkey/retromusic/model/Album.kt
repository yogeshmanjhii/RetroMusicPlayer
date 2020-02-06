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

package code.name.monkey.retromusic.model

import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Albums.ALBUM
import android.provider.MediaStore.Audio.Albums.ARTIST
import android.provider.MediaStore.Audio.Albums.FIRST_YEAR
import android.provider.MediaStore.Audio.Albums.NUMBER_OF_SONGS
import code.name.monkey.retromusic.loaders.getAlbumId

data class Album(
    var id: Long = 0,
    var title: String = "",
    var artist: String = "",
    var artistId: Long = 0,
    var songCount: Int = 0,
    var year: Int = 0
) {

    companion object {
        fun fromCursor(cursor: Cursor, artistId: Long = -1): Album {
            return Album(
                id = cursor.value(getAlbumId()),
                title = cursor.valueOrEmpty(ALBUM),
                artist = cursor.valueOrEmpty(ARTIST),
                artistId = if (artistId == -1L) cursor.value(MediaStore.Audio.AudioColumns.ARTIST_ID) else artistId,
                songCount = cursor.value(NUMBER_OF_SONGS),
                year = cursor.value(FIRST_YEAR)
            )
        }

        fun fromSong(song: Song): Album {
            return Album(song.albumId, song.albumName, song.artistName, song.artistId, -1, song.year)
        }
    }
}

fun Cursor.valueOrEmpty(name: String): String = valueOrDefault(name, "")

inline fun <reified T> Cursor.value(name: String): T {
    val index = getColumnIndexOrThrow(name)
    return when (T::class) {
        Short::class -> getShort(index) as T
        Int::class -> getInt(index) as T
        Long::class -> getLong(index) as T
        Boolean::class -> (getInt(index) == 1) as T
        String::class -> getString(index) as T
        Float::class -> getFloat(index) as T
        Double::class -> getDouble(index) as T
        ByteArray::class -> getBlob(index) as T
        else -> throw IllegalStateException("What do I do with ${T::class.java.simpleName}?")
    }
}

inline fun <reified T> Cursor.valueOrDefault(name: String, defaultValue: T): T {
    val index = getColumnIndex(name)
    if (index == -1) {
        return defaultValue
    }
    return when (T::class) {
        Short::class -> getShort(index) as? T ?: defaultValue
        Int::class -> getInt(index) as? T ?: defaultValue
        Long::class -> getLong(index) as? T ?: defaultValue
        Boolean::class -> (getInt(index) == 1) as T
        String::class -> getString(index) as? T ?: defaultValue
        Float::class -> getFloat(index) as? T ?: defaultValue
        Double::class -> getDouble(index) as? T ?: defaultValue
        ByteArray::class -> getBlob(index) as? T ?: defaultValue
        else -> throw IllegalStateException("What do I do with ${T::class.java.simpleName}?")
    }
}