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
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import code.name.monkey.retromusic.model.Song

/**
 * Created by hemanths on 2019-12-19.
 */

@Database(entities = [PlaylistEntity::class, Song::class], version = 1, exportSchema = false)
abstract class PlaylistDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: PlaylistDatabase? = null

        fun getInstance(context: Context): PlaylistDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(PlaylistDatabase::class) {
                val instance = Room.databaseBuilder(context.applicationContext, PlaylistDatabase::class.java, "playlist_database").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}