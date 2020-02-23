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
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import code.name.monkey.retromusic.model.Song

/**
 * Created by hemanths on 2020-02-23.
 */
@Database(entities = [Song::class], version = 2, exportSchema = false)
abstract class MusicPlaybackQueueStoreDatabase : RoomDatabase() {

    abstract fun queueDao(): QueueDao

    companion object {
        @Volatile
        private var INSTANCE: MusicPlaybackQueueStoreDatabase? = null

        fun getMusicDatabase(context: Context): MusicPlaybackQueueStoreDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicPlaybackQueueStoreDatabase::class.java,
                    "music_playback_state_kt"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}