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

    companion object {

        fun fromCursor(cursor: Cursor): Artist {
            return Artist(
                id = cursor.value(_ID),
                name = cursor.value(ARTIST),
                songCount = cursor.value(NUMBER_OF_TRACKS),
                albumCount = cursor.value(NUMBER_OF_ALBUMS)
            )
        }

        fun fromAlbum(album: Album): Artist {
            return Artist(album.artistId, album.artist, -1, -1)
        }

        const val UNKNOWN_ARTIST_DISPLAY_NAME = "Unknown Artist"
    }
}
