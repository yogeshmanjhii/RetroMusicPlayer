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

package code.name.monkey.retromusic.glide

import android.R.anim
import android.content.Context
import code.name.monkey.retromusic.R.drawable
import code.name.monkey.retromusic.glide.palette.BitmapPaletteTranscoder
import code.name.monkey.retromusic.glide.palette.BitmapPaletteWrapper
import code.name.monkey.retromusic.util.MusicUtil
import com.bumptech.glide.BitmapRequestBuilder
import com.bumptech.glide.DrawableTypeRequest
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.DiskCacheStrategy.NONE
import com.bumptech.glide.signature.MediaStoreSignature

/**
 * Created by hemanths on 2020-02-05.
 */

class AlbumGlideRequest {

    companion object {
        private val DEFAULT_DISK_CACHE_STRATEGY = NONE
        private const val DEFAULT_ERROR_IMAGE = drawable.default_album_art
        private const val DEFAULT_ANIMATION = anim.fade_in

        private fun createBaseRequest(
            requestManager: RequestManager,
            albumId: Long
        ): DrawableTypeRequest<*> =
            requestManager.loadFromMediaStore(MusicUtil.getMediaStoreAlbumCoverUri(albumId))

        private fun createSignature(albumId: Long): Key? {
            return MediaStoreSignature("", albumId, 0)
        }
    }

    class Builder(val requestManager: RequestManager, val albumId: Long) {
        companion object {
            fun from(requestManager: RequestManager, albumId: Long): Builder {
                return Builder(requestManager, albumId)
            }
        }

        fun generatePalette(context: Context): PaletteBuilder {
            return PaletteBuilder(this, context)
        }
    }

    class PaletteBuilder(private val builder: Builder, private val context: Context) {

        fun build(): BitmapRequestBuilder<out Any, BitmapPaletteWrapper> =
            createBaseRequest(builder.requestManager, builder.albumId)
                .asBitmap()
                .transcode(BitmapPaletteTranscoder(context), BitmapPaletteWrapper::class.java)
                .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                .error(DEFAULT_ERROR_IMAGE)
                .animate(DEFAULT_ANIMATION)
                .signature(createSignature(builder.albumId))
    }
}