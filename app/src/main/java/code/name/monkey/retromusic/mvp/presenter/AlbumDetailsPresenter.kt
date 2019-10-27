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
import code.name.monkey.retromusic.mvp.BaseView
import code.name.monkey.retromusic.mvp.Presenter
import code.name.monkey.retromusic.mvp.PresenterImpl
import code.name.monkey.retromusic.providers.interfaces.Repository
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


/**
 * Created by hemanths on 20/08/17.
 */
interface AlbumDetailsView : BaseView {
    fun album(album: Album)

    fun complete()

    fun loadArtistImage(artist: Artist)

    fun moreAlbums(albums: ArrayList<Album>)
}

interface AlbumDetailsPresenter : Presenter<AlbumDetailsView> {
    fun loadAlbum(albumId: Long)

    fun loadMore(artistId: Long)

    class AlbumDetailsPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<AlbumDetailsView>(), AlbumDetailsPresenter, CoroutineScope {

        private val job = Job()
        private lateinit var album: Album
        private var disposable: CompositeDisposable = CompositeDisposable()

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        override fun loadMore(artistId: Long) {
            /* disposable += repository.getArtistByIdFlowable(artistId)
                     .map {
                         view?.loadArtistImage(it)
                         return@map it.albums
                     }
                     .map {
                         it.filter { filterAlbum -> album.id != filterAlbum.id }
                     }
                     .subscribe({
                         if (it.isEmpty()) {
                             return@subscribe
                         }
                         view?.moreAlbums(ArrayList(it))
                     }, { t -> println(t) })*/
        }


        override fun loadAlbum(albumId: Long) {

            launch {
                when (val result = repository.getAlbum(albumId)) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.album(result.data)
                    }
                    is Result.Error -> withContext(Dispatchers.Main) {
                        view?.showEmptyView()
                    }
                }
            }
            /*disposable += repository.getAlbum(albumId)
                    .doOnComplete {
                        view?.complete()
                    }
                    .subscribe({
                        album = it
                        view?.album(it)
                    }, { t -> println(t) })*/
        }

        override fun detachView() {
            super.detachView()
            job.cancel()
            disposable.dispose()
        }
    }
}