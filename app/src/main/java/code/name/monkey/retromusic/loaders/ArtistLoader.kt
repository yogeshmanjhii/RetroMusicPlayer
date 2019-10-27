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
import code.name.monkey.retromusic.model.Artist
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.util.PreferenceUtil


object ArtistLoader {
    private fun getSongLoaderSortOrder(context: Context): String {
        return PreferenceUtil.getInstance(context).artistSortOrder + ", " +
                PreferenceUtil.getInstance(context).artistAlbumSortOrder + ", " +
                PreferenceUtil.getInstance(context).albumDetailSongSortOrder + ", " +
                PreferenceUtil.getInstance(context).artistDetailSongSortOrder
    }


    fun getAllArtists(context: Context): ArrayList<Artist> {
        return getArtistsForCursor(makeArtistCursor(context, null, null));
    }

    private fun getArtistsForCursor(cursor: Cursor?): java.util.ArrayList<Artist> {
        val arrayList = ArrayList<Artist>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                arrayList.add(Artist(cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getLong(2),
                        cursor.getLong(3)))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return arrayList
    }


    fun getArtist(cursor: Cursor?): Artist {
        var artist = Artist()
        if (cursor != null) {
            if (cursor.moveToFirst())
                artist = Artist(cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getLong(2),
                        cursor.getLong(3))
        }
        cursor?.close()
        return artist
    }

    fun getArtist(context: Context, id: Long): Artist {
        return getArtist(makeArtistCursor(context, "_id=?", arrayOf(id.toString())))
    }

    fun getArtists(context: Context, paramString: String, limit: Int): List<Artist> {
        val result = getArtistsForCursor(makeArtistCursor(context, "artist LIKE ?", arrayOf("$paramString%")))
        if (result.size < limit) {
            result.addAll(getArtistsForCursor(makeArtistCursor(context, "artist LIKE ?", arrayOf("%_$paramString%"))))
        }
        return if (result.size < limit) result else result.subList(0, limit)
    }


    fun artistSongs(context: Context, artistId: Long): ArrayList<Song> {
        val arrayList = arrayListOf<Song>()
        val cursor = makeArtistSongsCursor(context, artistId)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                arrayList.add(getSongFromCursorImpl(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return arrayList
    }

    private fun getSongFromCursorImpl(
            cursor: Cursor
    ): Song {
        val id = cursor.getInt(0)
        val title = cursor.getString(1)
        val trackNumber = cursor.getInt(2)
        val year = cursor.getInt(3)
        val duration = cursor.getLong(4)
        val data = cursor.getString(5)
        val dateModified = cursor.getLong(6)
        val albumId = cursor.getInt(7)
        val albumName = cursor.getString(8)
        val artistId = cursor.getInt(9)
        val artistName = cursor.getString(10)
        val composer = cursor.getString(11)

        return Song(id, title, trackNumber, year, duration, data, dateModified, albumId,
                albumName ?: "", artistId, artistName, composer ?: "")
    }

    fun artistAlbums(context: Context, artistId: Long): ArrayList<Album> {
        val arrayList = ArrayList<Album>()
        val cursor = makeArtistAlbumsCursor(context, artistId)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                arrayList.add(Album(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                         artistId,
                        cursor.getInt(3),
                        cursor.getInt(4)))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return arrayList
    }

    private fun makeArtistAlbumsCursor(context: Context, artistId: Long): Cursor? {
        val sortOrder = PreferenceUtil.getInstance(context).artistAlbumSortOrder
        return context.contentResolver.query(MediaStore.Audio.Artists.Albums.getContentUri("external", artistId),
                arrayOf("_id",
                        MediaStore.Audio.Artists.Albums.ALBUM,
                        MediaStore.Audio.Artists.Albums.ARTIST,
                        MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS,
                        MediaStore.Audio.Artists.Albums.FIRST_YEAR),
                null,
                null,
                sortOrder)

    }

    private fun makeArtistSongsCursor(context: Context, artistId: Long): Cursor? {
        val sortOrder = PreferenceUtil.getInstance(context).artistSongSortOrder
        val selection = "is_music=1 AND title != '' AND artist_id=$artistId"
        return context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Constants.baseProjection,
                selection,
                null,
                sortOrder)
    }

    private fun makeArtistCursor(context: Context,
                                 selection: String?,
                                 selectionValues: Array<String>?
    ): Cursor? {
        val sortOrder = PreferenceUtil.getInstance(context).artistSortOrder
        return context.contentResolver.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                arrayOf("_id", "artist", "number_of_albums", "number_of_tracks"),
                selection,
                selectionValues,
                sortOrder
        )
    }
    /*fun getArtistsFlowable(context: Context, query: String): Observable<ArrayList<Artist>> {
        return Observable.create { e ->
            SongLoader.getSongsFlowable(SongLoader.makeSongCursor(
                    context,
                    AudioColumns.ARTIST + " LIKE ?",
                    arrayOf("%$query%"),
                    getSongLoaderSortOrder(context))
            ).subscribe { songs ->
                e.onNext(splitIntoArtists(AlbumLoader.splitIntoAlbums(songs)))
                e.onComplete()
            }
        }
    }

    fun getArtists(context: Context, query: String): ArrayList<Artist> {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ARTIST + " LIKE ?",
                arrayOf("%$query%"),
                getSongLoaderSortOrder(context))
        )
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songs))
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
            if (artist.albums!!.isNotEmpty() && artist.albums[0].songs!!.isNotEmpty() && artist.albums[0].songs!![0].artistId == artistId) {
                return artist
            }
        }
        val album = Artist()
        artists.add(album)
        return album
    }

    fun splitIntoArtists(albums: Observable<ArrayList<Album>>): Observable<ArrayList<Artist>> {
        return Observable.create { e ->
            val artists = ArrayList<Artist>()
            albums.subscribe { localAlbums ->
                if (localAlbums != null) {
                    for (album in localAlbums) {
                        getOrCreateArtist(artists, album.artistId).albums!!.add(album)
                    }
                }
                e.onNext(artists)
                e.onComplete()
            }
        }
    }

    fun getArtistFlowable(context: Context, artistId: Int): Observable<Artist> {
        return Observable.create { e ->
            SongLoader.getSongsFlowable(SongLoader.makeSongCursor(context, AudioColumns.ARTIST_ID + "=?",
                    arrayOf(artistId.toString()),
                    getSongLoaderSortOrder(context)))
                    .subscribe { songs ->
                        val artist = Artist(AlbumLoader.splitIntoAlbums(songs))
                        e.onNext(artist)
                        e.onComplete()
                    }
        }
    }

    fun getArtist(context: Context, artistId: Int): Artist {
        val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ARTIST_ID + "=?",
                arrayOf(artistId.toString()),
                getSongLoaderSortOrder(context))
        )
        return Artist(AlbumLoader.splitIntoAlbums(songs))
    }*/
    /*fun getAllArtistsFlowable(
           context: Context
   ): Observable<ArrayList<Artist>> {
       return Observable.create { e ->
           SongLoader.getSongsFlowable(SongLoader.makeSongCursor(
                   context, null, null,
                   getSongLoaderSortOrder(context))
           ).subscribe { songs ->
               e.onNext(splitIntoArtists(AlbumLoader.splitIntoAlbums(songs)))
               e.onComplete()
           }
       }
   }*/

}
