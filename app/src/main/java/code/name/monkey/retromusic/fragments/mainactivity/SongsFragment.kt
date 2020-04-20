package code.name.monkey.retromusic.fragments.mainactivity

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.monkey.retromusic.App
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.adapter.song.ShuffleButtonSongAdapter
import code.name.monkey.retromusic.adapter.song.SongAdapter
import code.name.monkey.retromusic.fragments.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment
import code.name.monkey.retromusic.interfaces.MainActivityFragmentCallbacks
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.mvp.presenter.SongPresenter
import code.name.monkey.retromusic.mvp.presenter.SongView
import code.name.monkey.retromusic.util.PreferenceUtil
import java.util.*
import javax.inject.Inject

class SongsFragment :
    AbsLibraryPagerRecyclerViewCustomGridSizeFragment<SongAdapter, LinearLayoutManager>(),
    SongView, MainActivityFragmentCallbacks {

    @Inject
    lateinit var songPresenter: SongPresenter

    override val emptyMessage: Int
        get() = R.string.no_songs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.musicComponent.inject(this)
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songPresenter.attachView(this)
    }

<<<<<<< HEAD
    override fun createLayoutManager(): GridLayoutManager {
        return GridLayoutManager(requireActivity(), getGridSize()).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0) {
                        getGridSize()
                    } else {
                        1
                    }
                }
            }
        }
=======
    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireActivity())
>>>>>>> parent of c6fc672c... Added adapitve icon and grid size change forsong
    }

    override fun createAdapter(): SongAdapter {
        val dataSet = if (adapter == null) mutableListOf() else adapter!!.dataSet
        return ShuffleButtonSongAdapter(
            mainActivity,
            dataSet,
            R.layout.item_list,
            mainActivity
        )
    }

    override fun songs(songs: List<Song>) {
        adapter?.swapDataSet(songs)
    }

    override fun onMediaStoreChanged() {
        songPresenter.loadSongs()
    }

    override fun loadGridSize(): Int {
        return PreferenceUtil.getInstance(requireContext()).getSongGridSize(requireActivity())
    }

    override fun saveGridSize(gridColumns: Int) {
        PreferenceUtil.getInstance(requireContext()).setSongGridSize(gridColumns)
    }

    override fun loadGridSizeLand(): Int {
        return PreferenceUtil.getInstance(requireContext()).getSongGridSizeLand(requireActivity())
    }

    override fun saveGridSizeLand(gridColumns: Int) {
        PreferenceUtil.getInstance(requireContext()).setSongGridSizeLand(gridColumns)
    }

    override fun setGridSize(gridSize: Int) {
        adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        if (adapter?.dataSet.isNullOrEmpty())
            songPresenter.loadSongs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        songPresenter.detachView()
    }

    override fun showEmptyView() {
        adapter?.swapDataSet(ArrayList())
    }

    override fun loadSortOrder(): String {
        return PreferenceUtil.getInstance(requireContext()).songSortOrder
    }

    override fun saveSortOrder(sortOrder: String) {
        PreferenceUtil.getInstance(requireContext()).songSortOrder = sortOrder
    }

    override fun setSortOrder(sortOrder: String) {
        songPresenter.loadSongs()
    }

    companion object {

        @JvmField
        var TAG: String = SongsFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): SongsFragment {
            val args = Bundle()
            val fragment = SongsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun setLayoutRes(@LayoutRes layoutRes: Int) {
    }

    @LayoutRes
    override fun loadLayoutRes(): Int {
        return R.layout.item_list
    }

    override fun saveLayoutRes(@LayoutRes layoutRes: Int) {
    }

    override fun handleBackPress(): Boolean {
        return false
    }
}
