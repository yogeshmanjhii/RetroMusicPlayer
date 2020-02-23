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

import code.name.monkey.retromusic.model.Song

/**
 * Created by hemanths on 2020-02-23.
 */

class MusicQueueRepository(private val queueDao: QueueDao) {

    fun getQueue(): List<Song> = queueDao.getQueue()

    fun getOriginalQueue(): List<Song> = queueDao.getQueue()

    suspend fun insertQueue(queue: List<Song>) {
        queueDao.saveQueue(queue)
    }

    suspend fun insertOriginalQueue(queue: List<Song>) {
        queueDao.saveQueue(queue)
    }
}