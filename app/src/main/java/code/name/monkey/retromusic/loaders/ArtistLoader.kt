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
import code.name.monkey.retromusic.Constants.baseProjection
import code.name.monkey.retromusic.extensions.mapList
import code.name.monkey.retromusic.model.Album
import code.name.monkey.retromusic.model.Artist
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.util.PreferenceUtil

object ArtistLoader {

    fun getAllArtists(context: Context): ArrayList<Artist> {
        return getArtists(makeArtistCursor(context, null, null))
    }

    fun getArtist(context: Context, artistId: Long): Artist {
        return getArtist(makeArtistCursor(context, "_id=?", arrayOf(artistId.toString())))
    }

    private fun getArtist(cursor: Cursor?): Artist {
        return cursor?.use {
            if (cursor.moveToFirst()) {
                Artist.fromCursor(cursor)
            } else {
                null
            }
        } ?: Artist()
    }

    fun getArtists(context: Context, paramString: String): List<Artist> {
        return makeArtistCursor(context, "artist LIKE ?", arrayOf("$paramString%"))
            .mapList(true) {
                Artist.fromCursor(this)
            }
    }

    private fun getArtists(cursor: Cursor?): ArrayList<Artist> {
        val artists = ArrayList<Artist>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                artists.add(getArtistFromCursor(cursor))
            } while (cursor.moveToNext())
        }
        return artists
    }

    private fun getArtistFromCursor(cursor: Cursor): Artist {
        return Artist.fromCursor(cursor)
    }

    private fun makeArtistCursor(context: Context, selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        return context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            arrayOf(
                "_id",
                "artist",
                "number_of_albums",
                "number_of_tracks"
            ),
            selection,
            paramArrayOfString,
            PreferenceUtil.getInstance(context).artistSortOrder
        )
    }

    fun getSongsForArtist(context: Context, artistId: Long): List<Song> {
        return makeArtistSongCursor(context, artistId)
            .mapList(true) { Song.fromCursor(this, artistId = artistId) }
    }

    private fun makeArtistSongCursor(context: Context, artistId: Long): Cursor? {
        val artistSongSortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "is_music=1 AND title != '' AND artist_id=$artistId"
        return context.contentResolver.query(
            uri,
            baseProjection,
            selection,
            null,
            artistSongSortOrder
        )
    }

    fun splitIntoArtists(albums: ArrayList<Album>): ArrayList<Artist> {
        val artists = ArrayList<Artist>()
        if (albums.isNotEmpty()) {
            for (album in albums) {
                getOrCreateArtist(artists, album)
            }
        }
        return artists
    }

    private fun getOrCreateArtist(artists: ArrayList<Artist>, album: Album): Artist {
        for (artist in artists) {
            if (artist.id == album.artistId) {
                return artist
            }
        }
        val a = Artist.fromAlbum(album)
        artists.add(a)
        return a
    }
}