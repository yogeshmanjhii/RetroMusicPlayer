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
import android.provider.MediaStore.Audio.Albums.*
import code.name.monkey.appthemehelper.util.VersionUtils
import code.name.monkey.retromusic.Constants.baseProjection
import code.name.monkey.retromusic.extensions.mapList
import code.name.monkey.retromusic.model.Album
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.util.PreferenceUtil
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI as SONGS_URI

/**
 * Created by hemanths on 11/08/17.
 */

object AlbumLoader {

    fun getAllAlbums(context: Context): List<Album> {
        return makeAlbumCursor(context, null, null)
            .mapList(true) {
                Album.fromCursor(this)
            }
    }

    fun getAlbum(context: Context, id: Long): Album {
        return getAlbum(makeAlbumCursor(context, "_id=?", arrayOf(id.toString())))
    }

    fun getSongsForAlbum(context: Context, albumId: Long): ArrayList<Song> {
        return SongLoader.getSongs(makeAlbumSongCursor(context, albumId))
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
                getAlbumId(),
                "album",
                "artist",
                "numsongs",
                "minyear"
            ),
            null,
            null,
            DEFAULT_SORT_ORDER
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

    private fun makeAlbumCursor(context: Context, selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        return context.contentResolver.query(
            EXTERNAL_CONTENT_URI,
            arrayOf(
                getAlbumId(),
                "album", "artist",
                "artist_id",
                "numsongs",
                "minyear"
            ),
            selection,
            paramArrayOfString,
            PreferenceUtil.getInstance(context).albumSortOrder
        )
    }

    private fun makeAlbumSongCursor(context: Context, albumID: Long): Cursor? {
        val selection = "is_music=1 AND title != '' AND album_id=$albumID"
        return context.contentResolver.query(
            SONGS_URI,
            baseProjection,
            selection,
            null,
            PreferenceUtil.getInstance(context).albumDetailSongSortOrder
        )
    }

    fun splitIntoAlbums(
        songs: ArrayList<Song>?
    ): ArrayList<Album> {
        val albums = ArrayList<Album>()
        if (songs != null) {
            for (song in songs) {
                getOrCreateAlbum(albums, song)
            }
        }
        return albums
    }

    private fun getOrCreateAlbum(
        albums: ArrayList<Album>,
        song: Song
    ): Album {
        for (album in albums) {
            if (album.id == song.albumId) {
                return album
            }
        }
        val album = Album.fromSong(song)
        albums.add(album)
        return album
    }

    fun getAlbums(context: Context, paramString: String): List<Album> {
        return makeAlbumCursor(context, "album LIKE ?", arrayOf("$paramString%"))
            .mapList(true) { Album.fromCursor(this) }
    }
}

/*
* Android Q and more don't have `_id` for Albums and Artist albums so we have to use album_id
* */
fun getAlbumId(): String {
    return if (VersionUtils.hasQ()) ALBUM_ID else BaseColumns._ID
}