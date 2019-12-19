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

package code.name.monkey.retromusic.room

import android.content.Context
import code.name.monkey.retromusic.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

/**
 * Created by hemanths on 2019-12-19.
 */

class PlaylistEntityRepository(context: Context) : CoroutineScope {
    private var playlistDatabase: PlaylistDatabase = PlaylistDatabase.getInstance(context)

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    fun playlistEntries(): List<PlaylistEntity> = runBlocking {
        playlistDatabase.playlistDao().playlistEntities()
    }

    fun insertSongs(songs: List<Song>) {
        launch {
            playlistDatabase.playlistDao().insertSongs(songs)
        }
    }

    fun insertSong(song: Song) {
        launch {
            playlistDatabase.playlistDao().insertSong(song)
        }
    }
}