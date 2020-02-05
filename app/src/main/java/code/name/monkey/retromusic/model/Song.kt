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
import android.os.Parcelable
import android.provider.MediaStore.Audio.Media
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Song(
    val id: Long,
    val title: String,
    val trackNumber: Int,
    val year: Int,
    val duration: Long,
    val data: String,
    val dateModified: Long,
    val albumId: Int,
    val albumName: String,
    val artistId: Long,
    val artistName: String,
    val composer: String?
) : Parcelable {


    companion object {
        @JvmStatic
        val emptySong = Song(
            -1,
            "",
            -1,
            -1,
            -1,
            "",
            -1,
            -1,
            "",
            -1,
            "",
            ""
        )

        fun fromCursor(cursor: Cursor, artistId: Long): Any {
            return Song(
                id = cursor.value(Media._ID),
                albumId = cursor.value(Media.ALBUM_ID),
                artistId = cursor.value(Media.ARTIST_ID),
                albumName = cursor.valueOrEmpty(Media.ALBUM),
                artistName = cursor.valueOrEmpty(Media.ARTIST),
                composer = cursor.valueOrEmpty(Media.COMPOSER),
                data = cursor.valueOrEmpty(Media.DATA),
                dateModified = cursor.value(Media.DATE_ADDED),
                duration = cursor.value(Media.DURATION),
                title = cursor.valueOrEmpty(Media.TITLE),
                trackNumber = cursor.value(Media.TRACK),
                year = cursor.value(Media.YEAR)
            )
        }
    }
}