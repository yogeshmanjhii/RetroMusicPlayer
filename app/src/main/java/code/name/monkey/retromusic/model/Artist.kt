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
import android.provider.MediaStore.Audio.Artists.ARTIST
import android.provider.MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
import android.provider.MediaStore.Audio.Artists.NUMBER_OF_TRACKS
import android.provider.MediaStore.Audio.Artists._ID

class Artist(
    var id: Long = 0,
    var name: String = "",
    var songCount: Int = 0,
    var albumCount: Int = 0
) {

    /*val albums: ArrayList<Album>?

    val id: Int
        get() = safeGetFirstAlbum().artistId

    val name: String
        get() {
            val name = safeGetFirstAlbum().artist
            return if (MusicUtil.isArtistNameUnknown(name)) {
                UNKNOWN_ARTIST_DISPLAY_NAME
            } else name!!
        }

    val songCount: Int
        get() {
            var songCount = 0
            for (album in albums!!) {
                songCount += album.songCount
            }
            return songCount
        }

    val albumCount: Int
        get() = albums!!.size

    val songs: ArrayList<Song>
        get() {
            val songs = ArrayList<Song>()
            for (album in albums!!) {
                //songs.addAll(album.songs!!)
            }
            return songs
        }

    constructor(albums: ArrayList<Album>) {
        this.albums = albums
    }

    constructor() {
        this.albums = ArrayList()
    }

    fun safeGetFirstAlbum(): Album {
        return if (albums!!.isEmpty()) Album() else albums[0]
    }
*/
    companion object {

        fun fromCursor(cursor: Cursor): Artist {
            return Artist(
                id = cursor.value(_ID),
                name = cursor.value(ARTIST),
                songCount = cursor.value(NUMBER_OF_TRACKS),
                albumCount = cursor.value(NUMBER_OF_ALBUMS)
            )
        }

        const val UNKNOWN_ARTIST_DISPLAY_NAME = "Unknown Artist"
    }
}
