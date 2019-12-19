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

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "songs")
@Parcelize
open class Song(
        @PrimaryKey
        val id: Int = -1,
        val title: String = "",
        @ColumnInfo(name = "track_number")
        val trackNumber: Int = -1,
        val year: Int = -1,
        val duration: Long = -1,
        val data: String = "",
        @ColumnInfo(name = "date_modified")
        val dateModified: Long = -1,
        @ColumnInfo(name = "album_id")
        val albumId: Int = -1,
        @ColumnInfo(name = "album_name")
        val albumName: String = "",
        @ColumnInfo(name = "artist_id")
        val artistId: Int = -1,
        @ColumnInfo(name = "artist_name")
        val artistName: String = "",
        val composer: String? = ""
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
    }
}