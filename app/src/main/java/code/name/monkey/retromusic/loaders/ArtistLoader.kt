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
import code.name.monkey.retromusic.helper.SortOrder
import code.name.monkey.retromusic.model.Artist
import code.name.monkey.retromusic.model.Song

object ArtistLoader {
    /*private fun getSongLoaderSortOrder(context: Context): String {
        return PreferenceUtil.getInstance(context).artistSortOrder + ", " + PreferenceUtil.getInstance(context).artistAlbumSortOrder + ", " + PreferenceUtil.getInstance(context).albumSongSortOrder
    }

    fun getAllArtists(context: Context): ArrayList<Artist> {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                null, null,
                getSongLoaderSortOrder(context))
        )
        return splitIntoArtists(null)
    }

    fun getArtists(context: Context, query: String): ArrayList<Artist> {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ARTIST + " LIKE ?",
                arrayOf("%$query%"),
                getSongLoaderSortOrder(context))
        )
        return splitIntoArtists(null)
    }

    fun splitIntoArtists(albums: ArrayList<Album>?): ArrayList<Artist> {
        val artists = ArrayList<Artist>()
        if (albums != null) {
            for (album in albums) {
                getOrCreateArtist(artists, album.artistId).albums!!.add(album)
            }
        }
        return artists
    }

    private fun getOrCreateArtist(artists: ArrayList<Artist>, artistId: Int): Artist {
        for (artist in artists) {
            *//*if (artist.albums!!.isNotEmpty() && artist.albums[0].songs!!.isNotEmpty() && artist.albums[0].songs!![0].artistId == artistId) {
                return artist
            }*//*
        }
        val album = Artist()
        artists.add(album)
        return album
    }

    fun getArtist(context: Context, artistId: Int): Artist {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ARTIST_ID + "=?",
                arrayOf(artistId.toString()),
                getSongLoaderSortOrder(context))
        )
        return Artist(ArrayList())
    }*/

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
            arrayOf("_id", "artist", "number_of_albums", "number_of_tracks"),
            selection,
            paramArrayOfString,
            SortOrder.ArtistSortOrder.ARTIST_A_Z
        )
    }

    fun getSongsForArtist(context: Context, artistId: Long): List<Any> {
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
}