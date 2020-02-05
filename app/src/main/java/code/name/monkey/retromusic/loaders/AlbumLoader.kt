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
import android.provider.BaseColumns
import android.provider.MediaStore
import code.name.monkey.appthemehelper.util.VersionUtils
import code.name.monkey.retromusic.Constants.baseProjection
import code.name.monkey.retromusic.extensions.mapList
import code.name.monkey.retromusic.helper.SortOrder
import code.name.monkey.retromusic.model.Album
import code.name.monkey.retromusic.model.Song
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI as SONGS_URI

/**
 * Created by hemanths on 11/08/17.
 */

object AlbumLoader {

    fun getAllAlbums(context: Context): List<Album> {
        return makeAlbumsCursor(context, null, null)
            .mapList(true) {
                Album.fromCursor(this)
            }
    }

    fun getSongsForAlbum(context: Context, albumId: Long): ArrayList<Song> {
        return SongLoader.getSongs(makeAlbumSongCursor(context, albumId))
    }

    fun getAlbum(context: Context, id: Long): Album {
        return getAlbum(makeAlbumsCursor(context, "_id=?", arrayOf(id.toString())))
    }

    fun getAlbumsForArtist(context: Context, artistId: Long): List<Album> {
        return makeAlbumForArtistCursor(context, artistId).mapList(true) {
            Album.fromCursor(this, artistId)
        }
    }

    private fun makeAlbumForArtistCursor(context: Context, artistId: Long): Cursor? {
        if (artistId == -1L) {
            return null
        }
        return context.contentResolver.query(
            MediaStore.Audio.Artists.Albums.getContentUri("external", artistId),
            arrayOf(
                if (VersionUtils.hasQ()) MediaStore.Audio.Artists.Albums.ALBUM_ID else BaseColumns._ID,
                MediaStore.Audio.Artists.Albums.ALBUM,
                MediaStore.Audio.Artists.Albums.ARTIST,
                MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Artists.Albums.FIRST_YEAR
            ),
            null,
            null,
            MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
        )
    }

    private fun getAlbum(cursor: Cursor?): Album {
        return cursor?.use {
            if (cursor.moveToFirst()) {
                Album.fromCursor(cursor)
            } else {
                null
            }
        } ?: Album()
    }

    private fun getAlbums(cursor: Cursor?): ArrayList<Album> {
        val albums = ArrayList<Album>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                albums.add(getAlbumFromCursorImpl(cursor))
            } while (cursor.moveToNext())
        }
        return albums
    }

    private fun getAlbumFromCursorImpl(cursor: Cursor): Album {
        return Album.fromCursor(cursor)
    }

    private fun makeAlbumsCursor(context: Context, selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        return context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            arrayOf("_id", "album", "artist", "artist_id", "numsongs", "minyear"),
            selection,
            paramArrayOfString,
            SortOrder.AlbumSortOrder.ALBUM_A_Z
        )
    }

    private fun makeAlbumSongCursor(context: Context, albumID: Long): Cursor? {
        val selection = "is_music=1 AND title != '' AND album_id=$albumID"
        return context.contentResolver.query(
            SONGS_URI,
            baseProjection,
            selection,
            null,
            SortOrder.SongSortOrder.SONG_A_Z
        )
    }
}