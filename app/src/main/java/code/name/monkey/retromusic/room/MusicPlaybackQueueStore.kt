/*
 * Copyright (c) 2020 Hemanth Savarala.
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by hemanths on 2020-02-24.
 */

class MusicPlaybackQueueStoreKt(context: Context) {

    private val musicQueueRepository: MusicQueueRepository

    init {
        val queueDao = MusicPlaybackQueueStoreDatabase.getMusicDatabase(context).queueDao()
        musicQueueRepository = MusicQueueRepository(queueDao)
    }

    fun saveQueue(songs: List<Song>) = GlobalScope.launch {
        musicQueueRepository.insertQueue(songs)
    }

    fun saveOriginalQueue(playingQueue: List<Song>) = GlobalScope.launch {
        musicQueueRepository.insertOriginalQueue(playingQueue)
    }

    fun getQueue(): List<Song> = musicQueueRepository.getQueue()

    fun getOriginalQueue(): List<Song> = musicQueueRepository.getOriginalQueue()
}