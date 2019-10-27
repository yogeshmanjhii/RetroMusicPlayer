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
import code.name.monkey.retromusic.Constants
import code.name.monkey.retromusic.model.Album
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.util.PreferenceUtil


/**
 * Created by hemanths on 11/08/17.
 */

object AlbumLoader {

    fun allAlbums(context: Context): ArrayList<Album> {
        return getAlbumsForCursor(makeAlbumCursor(context, null, null))
    }

    private fun getAlbumsForCursor(cursor: Cursor?): ArrayList<Album> {
        val arrayList = ArrayList<Album>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                arrayList.add(albumCursorImpl(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return arrayList
    }

    private fun makeAlbumCursor(
            context: Context,
            selection: String?,
            paramArrayOfString: Array<String>?
    ): Cursor? {
        val albumSortOrder = PreferenceUtil.getInstance(context).albumSortOrder
        return context.contentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                arrayOf("_id",
                        MediaStore.Audio.Albums.ALBUM,
                        MediaStore.Audio.Albums.ARTIST,
                        MediaStore.Audio.AudioColumns.ARTIST_ID,
                        MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                        MediaStore.Audio.Albums.FIRST_YEAR),
                selection,
                paramArrayOfString,
                albumSortOrder
        )
    }

    fun getAlbum(context: Context, id: Long): Album {
        return getAlbum(makeAlbumCursor(context, "_id=?", arrayOf(id.toString())))
    }

    private fun getAlbum(cursor: Cursor?): Album {
        val album = Album()
        if (cursor != null && cursor.moveToFirst()) {
            albumCursorImpl(cursor)
        }
        cursor?.close()
        return album
    }

    private fun albumCursorImpl(cursor: Cursor): Album {
        return Album(cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getLong(3),
                cursor.getInt(4),
                cursor.getInt(5))
    }

    fun getAlbums(context: Context, paramString: String, limit: Int): MutableList<Album> {
        val result = getAlbumsForCursor(makeAlbumCursor(context, "album LIKE ?", arrayOf("$paramString%")))
        if (result.size < limit) {
            result.addAll(getAlbumsForCursor(makeAlbumCursor(context, "album LIKE ?", arrayOf("%_$paramString%"))))
        }
        return if (result.size < limit) result else result.subList(0, limit)
    }

    fun getAlbumSong(context: Context, albumId: Long): ArrayList<Song> {
        val arrayList = ArrayList<Song>()
        val cursor = makeAlbumSongsCursor(context, albumId)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                arrayList.add(SongLoader.getSong(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return arrayList
    }

    private fun makeAlbumSongsCursor(context: Context, albumId: Long): Cursor? {
        val sortOrder = PreferenceUtil.getInstance(context).albumSongSortOrder
        val selection = "is_music=1 AND title != '' AND album_id=$albumId"
        return context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Constants.baseProjection, selection, null, sortOrder)
    }

    /*fun getAllAlbumsFlowable(
            context: Context
    ): Observable<ArrayList<Album>> {
        val songs = SongLoader.getSongsFlowable(
                SongLoader.makeSongCursor(
                        context, null, null,
                        getSongLoaderSortOrder(context))
        )

        return splitIntoAlbumsFlowable(songs)
    }

    fun getAlbumsFlowable(
            context: Context,
            query: String
    ): Observable<ArrayList<Album>> {
        val songs = SongLoader.getSongsFlowable(
                SongLoader.makeSongCursor(
                        context,
                        AudioColumns.ALBUM + " LIKE ?",
                        arrayOf("%$query%"),
                        getSongLoaderSortOrder(context))
        )
        return splitIntoAlbumsFlowable(songs)
    }

    fun getAlbums(
            context: Context,
            query: String
    ): ArrayList<Album> {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ALBUM + " LIKE ?",
                arrayOf("%$query%"),
                getSongLoaderSortOrder(context))
        )
        return splitIntoAlbums(songs)
    }

    fun getAlbumFlowable(
            context: Context,
            albumId: Int
    ): Observable<Album> {
        return Observable.create { e ->
            val songs = SongLoader.getSongsFlowable(
                    SongLoader.makeSongCursor(
                            context,
                            AudioColumns.ALBUM_ID + "=?",
                            arrayOf(albumId.toString()),
                            getSongLoaderSortOrder(context)
                    )
            )
            *//*songs.subscribe { songs1 ->
                e.onNext(Album(songs1))
                e.onComplete()
            }*//*
        }
    }

    fun getAlbum(
            context: Context,
            albumId: Int
    ): Album {
        val songs = SongLoader.getSongs(
                SongLoader.makeSongCursor(
                        context,
                        AudioColumns.ALBUM_ID + "=?",
                        arrayOf(albumId.toString()),
                        getSongLoaderSortOrder(context)))
        val album = Album( )
        sortSongsByTrackNumber(album)
        return album
    }

    fun splitIntoAlbumsFlowable(
            songs: Observable<ArrayList<Song>>?
    ): Observable<ArrayList<Album>> {
        return Observable.create { e ->
            val albums = ArrayList<Album>()
            songs?.subscribe { songs1 ->
                for (song in songs1) {
                    getOrCreateAlbumFlowable(albums, song.albumId).subscribe { album ->
                        //album.songs!!.add(song)
                    }
                }
            }
            for (album in albums) {
                sortSongsByTrackNumber(album)
            }
            e.onNext(albums)
            e.onComplete()
        }
    }

    fun getAllAlbums(
            context: Context
    ): ArrayList<Album> {
        val songs = SongLoader.getSongs(
                SongLoader.makeSongCursor(
                        context, null, null,
                        getSongLoaderSortOrder(context))
        )

        return splitIntoAlbums(songs)
    }

    fun splitIntoAlbums(
            songs: ArrayList<Song>?
    ): ArrayList<Album> {
        val albums = ArrayList<Album>()
        if (songs != null) {
            for (song in songs) {
                getOrCreateAlbum(albums, song.albumId).songs?.add(song)
            }
        }
        for (album in albums) {
            sortSongsByTrackNumber(album)
        }
        return albums
    }

    private fun getOrCreateAlbumFlowable(
            albums: ArrayList<Album>,
            albumId: Int
    ): Observable<Album> {
        return Observable.create { e ->
            for (album in albums) {
                if (!album.songs!!.isEmpty() && album.songs[0].albumId == albumId) {
                    e.onNext(album)
                    e.onComplete()
                    return@create
                }
            }
            val album = Album()
            albums.add(album)
            e.onNext(album)
            e.onComplete()
        }
    }

    private fun getOrCreateAlbum(
            albums: ArrayList<Album>,
            albumId: Int
    ): Album {
        for (album in albums) {
            if (album.songs!!.isNotEmpty() && album.songs[0].albumId == albumId) {
                return album
            }
        }
        val album = Album()
        albums.add(album)
        return album
    }

    private fun sortSongsByTrackNumber(
            album: Album
    ) {
        album.songs?.sortWith(Comparator { o1, o2 -> o1.trackNumber - o2.trackNumber })
    }

    private fun getSongLoaderSortOrder(context: Context): String {
        return PreferenceUtil.getInstance(context).albumSortOrder + ", " +
                PreferenceUtil.getInstance(context).albumDetailSongSortOrder
    }*/
}
