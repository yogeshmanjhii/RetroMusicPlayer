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

package code.name.monkey.retromusic.mvp.presenter

import code.name.monkey.retromusic.Result
import code.name.monkey.retromusic.model.Album
import code.name.monkey.retromusic.model.Artist
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.mvp.BaseView
import code.name.monkey.retromusic.mvp.Presenter
import code.name.monkey.retromusic.mvp.PresenterImpl
import code.name.monkey.retromusic.providers.interfaces.Repository
import code.name.monkey.retromusic.rest.model.LastFmArtist
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


/**
 * Created by hemanths on 20/08/17.
 */
interface ArtistDetailsView : BaseView {
    fun artist(artist: Artist)

    fun artistSong(songs: ArrayList<Song>)

    fun artistAlbums(albums: ArrayList<Album>)

    fun artistInfo(lastFmArtist: LastFmArtist?)

    fun complete()
}

interface ArtistDetailsPresenter : Presenter<ArtistDetailsView> {

    fun loadArtist(artistId: Long)

    fun loadArtistSongs(artistId: Long)

    fun loadArtistAlbums(artistId: Long)

    fun loadBiography(name: String,
                      lang: String? = Locale.getDefault().language,
                      cache: String?)

    class ArtistDetailsPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<ArtistDetailsView>(), ArtistDetailsPresenter, CoroutineScope {

        override fun loadArtistAlbums(artistId: Long) {
            launch {
                when (val result = repository.getArtistAlbums(artistId)) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.artistAlbums(result.data)
                    }
                    is Result.Error -> withContext(Dispatchers.Main) {
                        view?.showEmptyView()
                    }
                }
            }
        }

        override fun loadArtistSongs(artistId: Long) {
            launch {
                when (val result = repository.getArtistSongs(artistId)) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.artistSong(result.data)
                    }
                    is Result.Error -> withContext(Dispatchers.Main) {
                        view?.showEmptyView()
                    }
                }
            }
        }

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        private val job = Job()

        override fun loadBiography(name: String,
                                   lang: String?,
                                   cache: String?) {
            disposable += repository.artistInfoFloable(name, lang, cache)
                    .subscribe {
                        view?.artistInfo(it)
                    }
        }

        private var disposable = CompositeDisposable()

        override fun loadArtist(artistId: Long) {
            launch {
                when (val result = repository.getArtistById(artistId)) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.artist(result.data)
                        view?.complete()
                    }
                    is Result.Error -> withContext(Dispatchers.Main) {
                        view?.showEmptyView()
                    }
                }
            }
        }

        override fun detachView() {
            super.detachView()
            job.cancel()
            disposable.dispose()
        }
    }
}