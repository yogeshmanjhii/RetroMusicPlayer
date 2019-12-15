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

package code.name.monkey.retromusic.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.retromusic.App
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.adapter.song.SimpleSongAdapter
import code.name.monkey.retromusic.album.album.HorizontalAlbumAdapter
import code.name.monkey.retromusic.extensions.show
import code.name.monkey.retromusic.extensions.withArgs
import code.name.monkey.retromusic.fragments.base.AbsMainActivityFragment
import code.name.monkey.retromusic.glide.ArtistGlideRequest
import code.name.monkey.retromusic.glide.RetroMusicColoredTarget
import code.name.monkey.retromusic.glide.SongGlideRequest
import code.name.monkey.retromusic.interfaces.CabHolder
import code.name.monkey.retromusic.interfaces.MainActivityFragmentCallbacks
import code.name.monkey.retromusic.model.Album
import code.name.monkey.retromusic.model.Artist
import code.name.monkey.retromusic.mvp.presenter.AlbumDetailsPresenter
import code.name.monkey.retromusic.mvp.presenter.AlbumDetailsView
import code.name.monkey.retromusic.util.MusicUtil
import code.name.monkey.retromusic.util.RetroColorUtil
import com.afollestad.materialcab.MaterialCab
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_album.*
import kotlinx.android.synthetic.main.activity_album_content.*
import javax.inject.Inject

/**
 * Created by hemanths on 2019-12-15.
 */

class AlbumDetailsFragment : AbsMainActivityFragment(), MainActivityFragmentCallbacks, AlbumDetailsView, CabHolder {
    @Inject
    lateinit var albumDetailsPresenter: AlbumDetailsPresenter

    private lateinit var album: Album
    private lateinit var cab: MaterialCab

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.toggleBottomNavigationView(true)

        App.musicComponent.inject(this)
        albumDetailsPresenter.attachView(this)

        ActivityCompat.startPostponedEnterTransition(requireActivity())

        val albumId: Int? = arguments?.getInt(EXTRA_BUNDLE_ALBUM_ID)
        if (albumId != null) {
            albumDetailsPresenter.loadAlbum(albumId)
        }
    }

    companion object {
        private const val EXTRA_BUNDLE_ALBUM_ID: String = "extra_bundle_album_id"

        fun newInstance(albumId: Int): AlbumDetailsFragment = AlbumDetailsFragment().withArgs {
            putInt(EXTRA_BUNDLE_ALBUM_ID, albumId)
        }
    }

    override fun handleBackPress(): Boolean {
        return true
    }

    override fun album(album: Album) {
        if (album.songs.isNullOrEmpty()) {
            requireActivity().onBackPressed()
            return
        }
        this.album = album
        loadAlbumCover(album)

        albumTitle.text = album.title
        if (MusicUtil.getYearString(album.year) == "-") {
            albumText.text = String.format("%s • %s", album.artistName, MusicUtil.getReadableDurationString(MusicUtil.getTotalDuration(requireContext(), album.songs)))
        } else {
            albumText.text = String.format("%s • %s • %s", album.artistName, MusicUtil.getYearString(album.year), MusicUtil.getReadableDurationString(MusicUtil.getTotalDuration(requireContext(), album.songs)))
        }

        albumDetailsPresenter.loadMore(album.artistId)

        val simpleSongAdapter = SimpleSongAdapter(requireActivity() as AppCompatActivity, java.util.ArrayList(), R.layout.item_song, this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = false
            adapter = simpleSongAdapter
        }

    }

    private fun loadAlbumCover(album: Album) {
        SongGlideRequest.Builder.from(Glide.with(requireContext()), album.safeGetFirstSong())
                .checkIgnoreMediaStore(requireContext())
                .generatePalette(requireContext())
                .build()
                .dontAnimate()
                .dontTransform()
                .into(object : RetroMusicColoredTarget(image) {
                    override fun onColorReady(color: Int) {
                        setColors(color)
                    }
                })
    }

    private fun setColors(color: Int) {

    }

    override fun complete() {
        ActivityCompat.postponeEnterTransition(requireActivity())
    }

    override fun loadArtistImage(artist: Artist) {
        ArtistGlideRequest.Builder.from(Glide.with(requireContext()), artist)
                .generatePalette(requireContext()).build()
                .dontAnimate()
                .dontTransform().into(object : RetroMusicColoredTarget(artistImage as ImageView) {
                    override fun onColorReady(color: Int) {

                    }
                })

    }

    override fun moreAlbums(albums: ArrayList<Album>) {
        moreTitle.show()
        moreRecyclerView.show()
        moreTitle.text = String.format(getString(R.string.label_more_from), album.artistName)

        val albumAdapter = HorizontalAlbumAdapter(requireActivity() as AppCompatActivity, albums, false, null)
        moreRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        moreRecyclerView.adapter = albumAdapter
    }

    override fun openCab(menuRes: Int, callback: MaterialCab.Callback): MaterialCab {
        cab.let {
            if (it.isActive) it.finish()
        }
        cab = MaterialCab(requireActivity() as AppCompatActivity, R.id.cab_stub)
                .setMenu(menuRes)
                .setCloseDrawableRes(R.drawable.ic_close_white_24dp)
                .setBackgroundColor(RetroColorUtil.shiftBackgroundColorForLightText(ATHUtil.resolveColor(requireContext(), R.attr.colorSurface)))
                .start(callback)
        return cab
    }
}